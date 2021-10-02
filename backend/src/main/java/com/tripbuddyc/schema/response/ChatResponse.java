package com.tripbuddyc.schema.response;

public class ChatResponse {

    Integer chatId;

    String name;


    public ChatResponse() {

    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
