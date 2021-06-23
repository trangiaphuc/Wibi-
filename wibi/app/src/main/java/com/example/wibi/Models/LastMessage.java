package com.example.wibi.Models;

public class LastMessage {
    private String message;
    private String dateSend;
    private String isSeen;
    private String chatWith;
    private String imgURL;

    public LastMessage(){}

    public LastMessage(String message, String dateSend, String isSeen, String receiver, String imgURL) {
        this.message = message;
        this.dateSend = dateSend;
        this.isSeen = isSeen;
        this.chatWith = receiver;
        this.imgURL = imgURL;
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
}
