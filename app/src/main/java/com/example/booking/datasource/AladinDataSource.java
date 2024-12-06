package com.example.booking.datasource;

import java.io.IOException;

public interface AladinDataSource {
    int fetchPageCount(String title) throws IOException;
}
