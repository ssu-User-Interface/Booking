package com.example.booking.data.model;

public class LastReview {
    private String lastComment;
    private int score;

    public LastReview (String lastComment, int score) {
        this.lastComment = lastComment;
        this.score =score;
    }

    public String getLastComment(){
        return lastComment;
    }
    public int getScore(){
        return score;
    }

}
