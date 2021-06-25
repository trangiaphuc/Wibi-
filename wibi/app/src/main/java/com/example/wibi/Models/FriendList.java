package com.example.wibi.Models;

public class FriendList {
    private String id;

    public FriendList(){}

    public FriendList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FriendList{" +
                "id='" + id + '\'' +
                '}';
    }
}
