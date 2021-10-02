package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripbuddyc.config.db.IntegerListConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("chatId")
    private Integer chatId;

    @JsonProperty("senderId")
    private Integer senderId;

    @JsonProperty("timestamp")
    @JsonFormat(pattern="d-M-yyyy H:m:s")
    private LocalDateTime timestamp;

    @Column(name = "message", length = 4096)
    @JsonProperty("message")
    private String message;


    public ChatMessage() {

    }

    public ChatMessage(Integer chatId, Integer senderId, LocalDateTime timestamp, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
