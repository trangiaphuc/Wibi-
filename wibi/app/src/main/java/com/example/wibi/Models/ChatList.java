package com.example.wibi.Models;

public class ChatList {

    private String id;

    public ChatList(){
    }

    public ChatList(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static void test(){
        System.out.println("test OK");
    }
}
