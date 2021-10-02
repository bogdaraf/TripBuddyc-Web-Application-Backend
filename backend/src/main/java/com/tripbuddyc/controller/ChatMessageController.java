package com.tripbuddyc.controller;

import com.tripbuddyc.model.Chat;
import com.tripbuddyc.schema.response.ChatMessageResponse;
import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.model.ChatMessage;
import com.tripbuddyc.model.User;
import com.tripbuddyc.service.ChatMessageService;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/chatMessages")
public class ChatMessageController {

    @Autowired
    ChatMessageService chatMessageService;

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

    @GetMapping(path = "/chat/{id}")
    public ResponseEntity<?> getMessagesByChat(@AuthenticationPrincipal User loggedUser,
                                               @PathVariable("id") Integer chatId) throws Exception {
        Chat chat = chatService.loadChatById(chatId);

        if(chat != null && !chat.getUsersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not the member of the chat!"), HttpStatus.BAD_REQUEST);
        }

        HashMap<Integer, User> chatUsers = new HashMap<>();

        for(int i=0; i<chat.getUsersIds().size(); i++) {
            User user = userService.loadUserById(chat.getUsersIds().get(i));

            if(user == null) {
                return new ResponseEntity<>(new MessageResponse("One of the chat users does not exist!"), HttpStatus.BAD_REQUEST);
            }

            chatUsers.put(user.getId(), user);
        }

        List<ChatMessage> messages = chatMessageService.loadMessagesByChatId(chatId);

        List<ChatMessageResponse> chatMessageResponses = new ArrayList<>();

        for(int i=0; i<messages.size(); i++) {
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
            chatMessageResponse.setType("message");
            chatMessageResponse.setSenderId(messages.get(i).getSenderId());
            chatMessageResponse.setName(chatUsers.get(messages.get(i).getSenderId()).getFirstName() + " "
                    + chatUsers.get(messages.get(i).getSenderId()).getLastName());
            chatMessageResponse.setTimestamp(messages.get(i).getTimestamp());
            chatMessageResponse.setMessage(messages.get(i).getMessage());

            chatMessageResponses.add(chatMessageResponse);
        }

        return new ResponseEntity<>(chatMessageResponses, HttpStatus.OK);
    }
}
