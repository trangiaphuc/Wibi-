package com.example.wibi.Models;

public class LastMessage {
    private String message;
    private String dateSend;
    private String isSeen;
    private String chatWith;
    private String imgURL;
    private String count;
    private String id;

    public LastMessage(){}

    public LastMessage(String message, String dateSend, String isSeen, String receiver, String imgURL, String count, String id) {
        this.message = message;
        this.dateSend = dateSend;
        this.isSeen = isSeen;
        this.chatWith = receiver;
        this.imgURL = imgURL;
        this.count = count;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    @Override
    public String toString() {
        return "LastMessage{" +
                "message='" + message + '\'' +
                ", dateSend='" + dateSend + '\'' +
                ", isSeen='" + isSeen + '\'' +
                ", chatWith='" + chatWith + '\'' +
                ", imgURL='" + imgURL + '\'' +
                '}';
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
