package com.example.letschat.model;

public class MessageModel {
    private String from, to;
    private String message;
    private long timeStamp;
    private String userToId;
    private String userFromId;

    public MessageModel(){}

    public MessageModel(String from, String to, String message, long timeStamp, String userToId, String userFromId) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timeStamp = timeStamp;
        this.userToId = userToId;
        this.userFromId = userFromId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserToId() {
        return userToId;
    }

    public void setUserToId(String userToId) {
        this.userToId = userToId;
    }

    public String getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(String userFromId) {
        this.userFromId = userFromId;
    }
}
