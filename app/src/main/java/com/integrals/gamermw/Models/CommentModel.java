package com.integrals.gamermw.Models;

public class CommentModel {
    private String message;
    private String user;
    private Long timestamp;

    public CommentModel() {
    }

    public CommentModel(String message, String user, Long timestamp) {
        this.message = message;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
