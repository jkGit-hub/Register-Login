package com.jkapps.loginphpmysql;

public class User {

    private int id;
    private String username, email, member;

    public User(int id, String username, String email, String member) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.member = member;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getMember() {
        return member;
    }
}
