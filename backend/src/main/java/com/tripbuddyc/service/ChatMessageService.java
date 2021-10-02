package com.tripbuddyc.service;

import com.tripbuddyc.model.ChatMessage;
import com.tripbuddyc.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Transactional
    public List<ChatMessage> loadMessagesByChatId(Integer chatId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByChatId(chatId);

        return messages;
    }

    @Transactional
    public void addChatMessage(Integer chatId, Integer senderId, LocalDateTime timestamp, String message) {
        ChatMessage chatMessage = new ChatMessage(chatId, senderId, timestamp, message);

        chatMessageRepository.save(chatMessage);
    }
}
