package com.milvus.ksql.java11.model;

public class HelloMessage {
    private String messageId;
    private String textMessage;
    
    public HelloMessage(String messageId, String textMessage) {
        this.messageId = messageId;
        this.textMessage = textMessage;
    }
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getTextMessage() {
        return textMessage;
    }
    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
    
    
}
