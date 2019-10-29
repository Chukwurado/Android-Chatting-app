package com.example.letschat.model;

public class UserApi {
    private String userId, username;
    private static UserApi instance;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static UserApi getInstance() {
        if (instance == null){
            instance = new UserApi();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "UserId: "  + userId+ " Username: " + username;
    }
}
