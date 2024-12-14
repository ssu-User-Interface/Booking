package com.example.booking.presentation.map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.Manifest;
import com.example.booking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.TrackingManager;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.camera.CameraAnimation;


import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng startPosition = null;
    private ProgressBar progressBar;
    private MapView mapView;
    private Label centerLabel;
    private boolean requestingLocationUpdates = false;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private KakaoMap kakaoMap;
    private final Map<Label, String> labelDataMap = new HashMap<>();
    private boolean isFirstLoad = true; // 첫 시작 여부 확인
    private View myLocationButton; // 내 위치 버튼


    private final KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull KakaoMap map) {
            Log.d("MapFragment", "onMapReady called");

            progressBar.setVisibility(View.GONE);
            kakaoMap = map;

            loadMarkersFromFirestore();

            // 사용자 위치 마커 추가
            if (startPosition != null) {
                // 검색된 위치로 카메라 이동
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(startPosition);
                kakaoMap.moveCamera(cameraUpdate);

                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                centerLabel = layer.addLabel(LabelOptions.from("centerLabel", startPosition)
                        .setStyles(LabelStyle.from(R.drawable.blue_dot).setAnchorPoint(0.5f, 0.5f))
                        .setRank(1));
//                TrackingManager trackingManager = kakaoMap.getTrackingManager();
//                trackingManager.startTracking(centerLabel);
//                startLocationUpdates();

                // 현재 위치 업데이트 필요 시 시작
                if (isFirstLoad) {
                    startLocationUpdates();
                    isFirstLoad = false;
                }
            }
            // 지도 클릭 리스너 추가
            kakaoMap.setOnMapClickListener((kakaoMap, latLng, pointF, layer) -> {
                // 지도 터치 시 TrackingManager의 stopTracking 호출
                if (kakaoMap.getTrackingManager() != null) {
                    kakaoMap.getTrackingManager().stopTracking();
                    Log.d("MapFragment", "Tracking stopped because user touched the map.");
                }
            });

            kakaoMap.setOnLabelClickListener((kakaoMap,layer,label) -> {
                String placeName = labelDataMap.get(label);
                showBottomSheet(placeName);
            });
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // ProgressBar와 MapView 초기화
        mapView = view.findViewById(R.id.map_view);
        progressBar = view.findViewById(R.id.progressBar);

        // NavController 가져오기
        NavController navController = Navigation.findNavController(container);

        // EditText 설정 및 클릭 리스너 추가
        EditText editText = view.findViewById(R.id.et_home_search);
        editText.setOnClickListener(v -> {
            navController.navigate(R.id.action_mapFragment_to_mapSearchFragment);
        });

        myLocationButton = view.findViewById(R.id.ic_my_location);
        myLocationButton.setOnClickListener(v -> moveToCurrentLocation());

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build();

        // 인자로 전달받은 위도, 경도 값 처리
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble("latitude", 0.0);
            double longitude = args.getDouble("longitude", 0.0);
            Log.d("MapFragment", "Received Latitude: " + latitude + ", Longitude: " + longitude);

            if (latitude != 0.0 && longitude != 0.0) {
                startPosition = LatLng.from(latitude, longitude); // 번들 값으로 startPosition 설정
                mapView.start(readyCallback); // 번들 값으로 즉시 지도 시작
            }
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    if (centerLabel != null) {
                        centerLabel.moveTo(LatLng.from(location.getLatitude(), location.getLongitude()));
                    }
                }
            }
        };

        if (hasLocationPermissions()) {
            getStartLocation();
        } else {
            requestPermissions(locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void getStartLocation() {
        if(isFirstLoad) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            startPosition = LatLng.from(location.getLatitude(), location.getLongitude());
                            mapView.start(readyCallback);
                            isFirstLoad = false;
                        }
                    });
        } else {
            mapView.start(readyCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (android.location.Location location : locationResult.getLocations()) {
                        if (centerLabel != null) {
                            centerLabel.moveTo(LatLng.from(location.getLatitude(), location.getLongitude()));
                        }
                    }
                }
            };
        }
        requestingLocationUpdates = true;
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartLocation();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void moveToCurrentLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null && kakaoMap != null) {
                        LatLng currentLocation = LatLng.from(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation);
                        kakaoMap.moveCamera(cameraUpdate);
                    }
                });
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setMessage("위치 권한이 필요합니다.")
                .setPositiveButton("권한 설정하기", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + requireContext().getPackageName()));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("앱 종료", (dialog, which) -> requireActivity().finish())
                .setCancelable(false)
                .show();
    }

    private void loadMarkersFromFirestore() {
        Log.d("MapFragment", "loadMarkersFromFirestore called"); // 디버깅 로그 추가
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnSuccessListener(booksSnapshot -> {
                    for (DocumentSnapshot book : booksSnapshot) {
                        String bookId = book.getId();
                        Log.d("MapFragment", "Book ID: " + bookId);

                        db.collection("users")
                                .document(userId)
                                .collection("books")
                                .document(bookId)
                                .collection("records")
                                .get()
                                .addOnSuccessListener(recordsSnapshot -> {
                                    for (DocumentSnapshot record : recordsSnapshot) {
                                        // 모든 데이터 출력 (디버깅용)
                                        Log.d("Firestore", "Record Data: " + record.getData());

                                        String placeName = record.getString("address");
                                        String placeAddress = record.getString("placeAddress");
                                        Double latitude = record.getDouble("latitude");
                                        Double longitude = record.getDouble("longitude");

                                        // 추가 로그
                                        Log.d("MapFragment", "PlaceName: " + placeName + ", Latitude: " + latitude + ", Longitude: " + longitude);

                                        // latitude, longitude가 없는 데이터를 건너뜀
                                        if (latitude == null || longitude == null) {
                                            Log.w("MapFragment", "Skipping record with missing latitude/longitude for document: " + record.getId());
                                            continue;
                                        }

                                        // 필요한 데이터가 없는 경우 건너뜀
                                        if (placeName == null) {
                                            Log.w("MapFragment", "Skipping record with missing placeName for document: " + record.getId());
                                            continue;
                                        }

                                        // latitude, longitude가 없는 데이터를 건너뜀
                                        if (latitude == null || longitude == null) {
                                            Log.w("Firestore", "Skipping record with missing latitude/longitude for document: " + record.getId());
                                            continue;
                                        }

                                        // 필요한 데이터가 없는 경우 건너뜀
                                        if (placeName == null || placeAddress == null) {
                                            Log.w("Firestore", "Skipping record with missing placeName/placeAddress for document: " + record.getId());
                                            continue;
                                        }

                                        // 유효한 데이터만 마커로 추가
                                        addMarkerToMap(placeName, placeAddress, latitude, longitude);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to load records", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to load books", e));
    }

    private void addMarkerToMap(String placeName, String placeAddress, double latitude, double longitude) {
        if (kakaoMap != null) {
            Log.d("MapFragment", "Adding marker: " + placeName + " at " + latitude + ", " + longitude);

            try {
                LabelLayer labelLayer = kakaoMap.getLabelManager().getLayer();
                if (labelLayer == null) {
                    Log.e("MapFragment", "LabelLayer is null.");
                    return;
                }

                LabelOptions options = LabelOptions.from(placeName, LatLng.from(latitude, longitude))
                        .setStyles(LabelStyle.from(R.drawable.custommarker) // 기본 마커 아이콘 적용
                                .setAnchorPoint(0.5f, 1.0f)) // 마커의 앵커 포인트 (중앙 하단)
                        .setRank(1); // 레이블의 우선순위

                Label markerLabel = labelLayer.addLabel(options);
                if (markerLabel != null) {
                    Log.d("MapFragment", "Marker successfully added: " + placeName);
                    labelDataMap.put(markerLabel, placeName);

                } else {
                    Log.e("MapFragment", "Failed to create marker label: " + placeName);
                }
            } catch (Exception e) {
                Log.e("MapFragment", "Error adding marker: " + placeName, e);
            }
        } else {
            Log.e("MapFragment", "KakaoMap is not ready.");
        }
    }
    private void showBottomSheet(String placeName) {
        MapBottomSheetDialogFragment bottomSheetDialog = MapBottomSheetDialogFragment.newInstance(placeName);
        bottomSheetDialog.show(getChildFragmentManager(), "MapBottomSheetDialog");
    }
}