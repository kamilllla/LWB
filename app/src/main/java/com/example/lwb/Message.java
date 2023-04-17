package com.example.lwb;

import java.util.Date;

public class Message {
    public Message() {

    }

    private String userName;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    private String receiverId;
    private String textMessage;
    private String messageTime;
    private Date messageTimeDate;

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public Message(String userName, String receiverId, String textMessage, String messageTime, Date messageTimeDate) {
        this.userName = userName;
        this.receiverId = receiverId;
        this.textMessage = textMessage;
        this.messageTime = messageTime;

        this.messageTimeDate = messageTimeDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }


    public Date getMessageTimeDate() {
        return messageTimeDate;
    }

    public void setMessageTimeDate(Date messageTimeDate) {
        this.messageTimeDate = messageTimeDate;
    }
}
