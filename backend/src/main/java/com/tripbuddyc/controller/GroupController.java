package com.tripbuddyc.controller;

import com.tripbuddyc.model.*;
import com.tripbuddyc.repository.*;
import com.tripbuddyc.schema.response.GroupAddedResponse;
import com.tripbuddyc.schema.response.GroupResponse;
import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/groups")
public class GroupController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TripService tripService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    BillRepository billRepository;

    @Autowired
    BillService billService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getGroupById(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(!group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not a member of the group!"), HttpStatus.FORBIDDEN);
        }

        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setId(group.getId());
        groupResponse.setOwnerId(group.getOwnerId());
        groupResponse.setMembersIds(group.getMembersIds());
        groupResponse.setPendingUsersIds(group.getPendingUsersIds());
        groupResponse.setTrip(tripService.loadTripById(group.getTripId()));
        groupResponse.setChatId(group.getChatId());
        groupResponse.setCurrency(group.getCurrency());

        return new ResponseEntity<>(groupResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/trip/{id}")
    public ResponseEntity<?> getGroupByTripId(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer tripId) {
        Trip trip = tripService.loadTripById(tripId);

        if(trip == null) {
            return new ResponseEntity<>(new MessageResponse("Trip does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(trip.getGroupId() == null) {
            return new ResponseEntity<>(new MessageResponse("Trip does not have a group!"), HttpStatus.BAD_REQUEST);
        }

        return getGroupById(loggedUser, trip.getGroupId());

    }

    @PostMapping(path = "/trip/{id}")
    public ResponseEntity<?> addGroupByTripId(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer tripId) {
        Trip trip = tripService.loadTripById(tripId);

        if(trip == null) {
            return new ResponseEntity<>(new MessageResponse("Trip does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != trip.getUserId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the trip!"), HttpStatus.FORBIDDEN);
        }
        if(trip.getGroupId() != null) {
            return new ResponseEntity<>(new MessageResponse("A group for the trip already exists!"), HttpStatus.BAD_REQUEST);
        }

        List<Integer> members = new ArrayList<>();
        members.add(loggedUser.getId());
        List<Integer> pendingUsers = new ArrayList<>();

        Group group = new Group(loggedUser.getId(), members, pendingUsers, tripId);

        groupRepository.save(group);

        trip.setGroupId(group.getId());

        tripRepository.save(trip);

        ResponseEntity<?> chatAddedResponse = chatService.addChatByGroupId(group.getId());

        if(chatAddedResponse.getStatusCode() != HttpStatus.OK) {
            return chatAddedResponse;
        }

        GroupAddedResponse groupAddedResponse = new GroupAddedResponse();
        groupAddedResponse.setMessage("Group added successfully!");
        groupAddedResponse.setGroupId(group.getId());
        groupAddedResponse.setChatId((Integer) chatAddedResponse.getBody());

        return new ResponseEntity<>(groupAddedResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/currency")
    public ResponseEntity<?> changeGroupCurrencyById(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId,
                                                     @RequestBody MessageResponse message) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(!group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not a member of the group!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.BAD_REQUEST);
        }

        group.setCurrency(message.getMessage());

        groupRepository.save(group);

        return new ResponseEntity<>(new MessageResponse("The currency changed successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/apply/{id}")
    public ResponseEntity<?> applyToGroupById(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are already a member of the group!"), HttpStatus.BAD_REQUEST);
        }
        if(group.getPendingUsersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are already a pending user in the group!"), HttpStatus.BAD_REQUEST);
        }

        group.addPendingUser(loggedUser.getId());

        groupRepository.save(group);

        return new ResponseEntity<>(new MessageResponse("Applied to the group successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<?> joinGroupById(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are already a member of the group!"), HttpStatus.BAD_REQUEST);
        }

        Trip trip = tripService.loadTripById(group.getTripId());

        if(trip == null) {
            return new ResponseEntity<>(new MessageResponse("Group's trip does not exist!"), HttpStatus.BAD_REQUEST);
        }

        group.addMember(loggedUser.getId());

        if(group.getPendingUsersIds().contains(loggedUser.getId())) {
            group.removePendingUser(loggedUser.getId());
        }

        groupRepository.save(group);

        //trip gets copied
        Trip newTrip = new Trip(loggedUser.getId(), trip.getName(), trip.getContinent(), trip.getCountry(),
                trip.getCity(), trip.getDateFrom(), trip.getDateTo(), trip.getActivities(), trip.getGroupType(),
                trip.getGroupSizeFrom(), trip.getGroupSizeTo(), trip.getAgeRangeFrom(), trip.getAgeRangeTo());
        newTrip.setGroupId(groupId);

        tripRepository.save(newTrip);

        //new user is added to the chat
        Chat chat = chatService.loadChatById(group.getChatId());

        chat.addUserId(loggedUser.getId());

        chatRepository.save(chat);

        loggedUser.addChatId(chat.getId());

        userRepository.save(loggedUser);

        return new ResponseEntity<>(new MessageResponse("Joined the group successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/accept/{groupId}/{userId}")
    public ResponseEntity<?> acceptUserToGroup(@AuthenticationPrincipal User loggedUser, @PathVariable("groupId") Integer groupId,
                                               @PathVariable("userId") Integer userId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.BAD_REQUEST);
        }
        if(!group.getPendingUsersIds().contains(userId)) {
            return new ResponseEntity<>(new MessageResponse("User is not a pending user!"), HttpStatus.BAD_REQUEST);
        }

        User user;

        try {
            user = userService.loadUserById(userId);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        return joinGroupById(user, groupId);
    }

    @PostMapping(path = "/reject/{groupId}/{userId}")
    public ResponseEntity<?> rejectUserToGroup(@AuthenticationPrincipal User loggedUser, @PathVariable("groupId") Integer groupId,
                                               @PathVariable("userId") Integer userId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.BAD_REQUEST);
        }
        if(!group.getPendingUsersIds().contains(userId)) {
            return new ResponseEntity<>(new MessageResponse("User is not a pending user!"), HttpStatus.BAD_REQUEST);
        }

        User user;

        try {
            user = userService.loadUserById(userId);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.BAD_REQUEST);
        }

        group.removePendingUser(userId);

        groupRepository.save(group);

        return new ResponseEntity<>(new MessageResponse("User rejected successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/leave/{id}")
    public ResponseEntity<?> leaveGroup(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId) {
        boolean changedOwnership = false;

        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(!group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>("You are not a member of the group!", HttpStatus.BAD_REQUEST);
        }

        group.removeMember(loggedUser.getId());

        if(loggedUser.getId() == group.getOwnerId()) {
            if(group.getMembersIds().size() == 0) { //delete the group
                groupRepository.delete(group);

                Trip trip = tripService.loadTripByUserIdAndGroupId(loggedUser.getId(), groupId);

                if(trip != null) {
                    trip.setGroupId(null);

                    tripRepository.save(trip);
                }
                else {
                    return new ResponseEntity<>("Group does not have a trip!", HttpStatus.BAD_REQUEST);
                }

                //remove the chat
                Chat chat = chatService.loadChatById(group.getChatId());

                if(chat == null) {
                    return new ResponseEntity<>("Group does not have a chat!", HttpStatus.BAD_REQUEST);
                }

                chatRepository.delete(chat);

                loggedUser.removeChatId(chat.getId());

                userRepository.save(loggedUser);

                return new ResponseEntity<>(new MessageResponse("Left the group successfully!\n" +
                        "You were the only member, the group got removed!"), HttpStatus.OK);
            }

            group.setOwnerId(group.getMembersIds().get(0));

            Trip trip = tripService.loadTripByUserIdAndGroupId(group.getMembersIds().get(0), groupId);

            if(trip != null) {
                group.setTripId(trip.getId());
            }
            else {
                return new ResponseEntity<>("Group does not have a trip!", HttpStatus.BAD_REQUEST);
            }

            changedOwnership = true;
        }

        groupRepository.save(group);

        Trip trip = tripService.loadTripByUserIdAndGroupId(loggedUser.getId(), groupId);

        if(trip != null) {
            trip.setGroupId(null);

            tripRepository.save(trip);
        }
        else {
            return new ResponseEntity<>("Group does not have a trip!", HttpStatus.BAD_REQUEST);
        }

        //user is removed from the chat
        Chat chat = chatService.loadChatById(group.getChatId());

        if(chat == null) {
            return new ResponseEntity<>("Group does not have a chat!", HttpStatus.BAD_REQUEST);
        }

        chat.removeUserId(loggedUser.getId());

        chatRepository.save(chat);

        loggedUser.removeChatId(chat.getId());

        userRepository.save(loggedUser);

        if(changedOwnership) {
            return new ResponseEntity<>(new MessageResponse("Left the group successfully!\n" +
                    "Since you were the owner, another member got the ownership!"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new MessageResponse("Left the group successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer groupId) {
        Group group = groupService.loadGroupById(groupId);

        if(group == null) {
            return new ResponseEntity<>(new MessageResponse("Group does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.FORBIDDEN);
        }
        if(group.getMembersIds().size() > 1) {
            return new ResponseEntity<>(new MessageResponse("There are other members of the group, can not delete!"), HttpStatus.FORBIDDEN);
        }

        groupRepository.delete(group);

        Trip trip = tripService.loadTripById(group.getTripId());

        if(trip != null) {
            trip.setGroupId(null);

            tripRepository.save(trip);
        }
        else {
            return new ResponseEntity<>("Group does not have a trip!", HttpStatus.BAD_REQUEST);
        }

        //remove the chat
        Chat chat = chatService.loadChatById(group.getChatId());

        if(chat == null) {
            return new ResponseEntity<>("Group does not have a chat!", HttpStatus.BAD_REQUEST);
        }

        chatRepository.delete(chat);

        loggedUser.removeChatId(chat.getId());

        userRepository.save(loggedUser);

        //remove the bills

        List<Bill> bills = billService.loadBillsByChatId(groupId);

        for (Bill bill: bills) {
            billRepository.delete(bill);
        }

        return new ResponseEntity<>(new MessageResponse("The group removed successfully!"), HttpStatus.OK);
    }
}
