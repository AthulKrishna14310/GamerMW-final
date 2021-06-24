package com.integrals.gamermw.Models;

public class ChatModel {

    private String message;
    private String user;
    private String timestamp;

    public ChatModel(String message, String user, String timestamp) {
        this.message = message;
        this.user = user;
        this.timestamp = timestamp;
    }

    public ChatModel() {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}