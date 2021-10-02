package com.tripbuddyc.schema.response;

public class ChatAddedResponse {
    private String message;

    private Integer chatId;

    public ChatAddedResponse() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer tripId) {
        this.chatId = tripId;
    }
}
