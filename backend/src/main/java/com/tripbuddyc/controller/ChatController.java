package com.tripbuddyc.controller;

import com.tripbuddyc.model.Chat;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.ChatRepository;
import com.tripbuddyc.repository.UserRepository;
import com.tripbuddyc.schema.response.ChatAddedResponse;
import com.tripbuddyc.schema.response.ChatResponse;
import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getChatsByUserId(@AuthenticationPrincipal User loggedUser) throws Exception {
        List<Chat> chats = chatService.loadAllByUserId(loggedUser.getId());

        List<ChatResponse> chatResponses = new ArrayList<>();

        for(int i=0; i<chats.size(); i++) {
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setChatId(chats.get(i).getId());

            if(chats.get(i).getName() == null || chats.get(i).getName() == "") { //then it is a private chat
                List<Integer> otherUserId = new ArrayList<>(chats.get(i).getUsersIds());
                otherUserId.remove(loggedUser.getId());

                User user = userService.loadUserById(otherUserId.get(0));

                if(user == null) {
                    return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.BAD_REQUEST);
                }

                chatResponse.setName(user.getFirstName() + " " + user.getLastName());
            }
            else { //group chat
                chatResponse.setName(chats.get(i).getName());
            }

            chatResponses.add(chatResponse);
        }

        return new ResponseEntity<>(chatResponses, HttpStatus.OK);
    }

    @PostMapping("/userId/{id}")
    public ResponseEntity<?> addPrivateChat(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer userId) throws Exception {
        if(loggedUser.getId() == userId) {
            return new ResponseEntity<>(new MessageResponse("You can not create a chat with yourself!"), HttpStatus.BAD_REQUEST);
        }

        User user = userService.loadUserById(userId);

        if(user == null) {
            return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        Chat chat = new Chat();
        chat.setName(null);

        List<Integer> usersIds = new ArrayList<>();
        usersIds.add(loggedUser.getId());
        usersIds.add(userId);

        chat.setUsersIds(usersIds);

        chatRepository.save(chat);

        loggedUser.addChatId(chat.getId());

        userRepository.save(loggedUser);

        user.addChatId(chat.getId());

        userRepository.save((user));

        ChatAddedResponse chatAddedResponse = new ChatAddedResponse();
        chatAddedResponse.setMessage("Chat added successfully!");
        chatAddedResponse.setChatId(chat.getId());

        return new ResponseEntity<>(chatAddedResponse, HttpStatus.OK);
    }

}
