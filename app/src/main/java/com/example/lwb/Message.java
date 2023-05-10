package com.example.lwb;

import java.util.Date;

public class Message {
    public Message() {

    }

    private String userName;


    private String receiverId;
    private String textMessage;
    private String messageTime;
    private Date messageTimeDate;
    private String  conversionId, coversitionName;



    public Message(String userName, String receiverId, String textMessage, String messageTime, Date messageTimeDate) {
        this.userName = userName;
        this.receiverId = receiverId;
        this.textMessage = textMessage;
        this.messageTime = messageTime;

        this.messageTimeDate = messageTimeDate;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getCoversitionName() {
        return coversitionName;
    }

    public void setCoversitionName(String coversitionName) {
        this.coversitionName = coversitionName;
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
