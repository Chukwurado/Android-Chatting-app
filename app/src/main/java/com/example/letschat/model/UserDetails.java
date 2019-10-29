package com.example.letschat.model;

public class UserDetails {
    private String email, username, userid;

    public UserDetails(String email, String username, String userid) {
        this.email = email;
        this.username = username;
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }
}
