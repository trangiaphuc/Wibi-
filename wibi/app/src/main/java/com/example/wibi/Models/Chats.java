package com.example.wibi.Models;

public class Chats {
    private String sender;
    private String receiver;
    private String message;
    private String imgURL;
    private String dateSend;
    private String isSeen;

    public Chats(){}

    public Chats(String sender, String receiver, String message, String imgURL, String dateSend, String isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.imgURL = imgURL;
        this.dateSend = dateSend;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    @Override
    public String toString() {
        return "Chats{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", dateSend='" + dateSend + '\'' +
                ", isSeen=" + isSeen +
                '}';
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
    public String getIsSeen(){
        return isSeen;
    }
}
