package com.tripbuddyc.service;

import com.tripbuddyc.model.*;
import com.tripbuddyc.repository.ChatRepository;
import com.tripbuddyc.repository.GroupRepository;
import com.tripbuddyc.schema.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    TripService tripService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupService groupService;

    @Transactional
    public Chat loadChatById(Integer id) {
        Chat chat = chatRepository.findById(id);

        return chat;
    }

    @Transactional
    public List<Chat> loadAllByUserId(Integer userId) {
        List<Chat> allChats = chatRepository.findAll();
        List<Chat> chats = new ArrayList<>();

        for(int i=0; i<allChats.size(); i++) {
            if(allChats.get(i).getUsersIds().contains(userId)) {
                chats.add(allChats.get(i));
            }
        }

        return chats;
    }

    @Transactional
    public ResponseEntity<?> addChatByGroupId(Integer groupId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(group.getChatId() != null) {
            return new ResponseEntity<>(new MessageResponse("Group already has a chat!"), HttpStatus.BAD_REQUEST);
        }

        Trip trip = tripService.loadTripById(group.getTripId());

        if(trip == null) {
            return new ResponseEntity<>(new MessageResponse("Trip does not exist!"), HttpStatus.BAD_REQUEST);
        }

        Chat chat = new Chat();
        chat.setName(trip.getName());
        chat.setUsersIds(group.getMembersIds()); //only one list should exist in memory

        chatRepository.save(chat);

        group.setChatId(chat.getId());

        groupRepository.save(group);

        return new ResponseEntity<>(chat.getId(), HttpStatus.OK);
    }
}
