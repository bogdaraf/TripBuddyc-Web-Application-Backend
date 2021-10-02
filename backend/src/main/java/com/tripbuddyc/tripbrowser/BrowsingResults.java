package com.tripbuddyc.tripbrowser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripbuddyc.model.Chat;
import com.tripbuddyc.model.Group;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.model.User;
import com.tripbuddyc.schema.response.GroupResponse;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.GroupService;
import com.tripbuddyc.service.TripService;
import com.tripbuddyc.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrowsingResults {

    @JsonProperty("users")
    Set<User> users;

    @JsonProperty("groups")
    Set<GroupResponse> groupResponses;


    public BrowsingResults() {
        users = new HashSet<>();
        groupResponses = new HashSet<>();
    }

    public BrowsingResults getResults(User loggedUser, UserService userService, TripService tripService,
                                      GroupService groupService, ChatService chatService, List<Trip> trips) {

        List<Integer> usersGroupsIds = groupService.loadAllGroupsIdsByUser(loggedUser);

        HashSet<Integer> uniqueGroupsIds = new HashSet<>();

        for(int i=0; i<trips.size(); i++) {
            try {
                if(trips.get(i).getUserId() != null) {
                    User user = userService.loadUserById(trips.get(i).getUserId());

                    if(user != null) {
                        List<Chat> chats = chatService.loadAllByUserId(loggedUser.getId());

                        Integer connectedChatId = null;

                        for(int j=0; j<chats.size(); j++) {
                            if(chats.get(j).getName() == null && chats.get(j).getUsersIds().contains(user.getId())) {
                                connectedChatId = chats.get(j).getId();

                                break;
                            }
                        }

                        if(loggedUser.getId() != user.getId()) {
                            user.setConnectedChatId(connectedChatId);
                        } else {
                            user.setConnectedChatId(null);
                        }

                        users.add(user);
                    }
                }

                if(trips.get(i).getGroupId() != null) {
                    Group group = groupService.loadGroupById(trips.get(i).getGroupId());
                    if(group != null && !uniqueGroupsIds.contains(trips.get(i).getGroupId())
                    && !usersGroupsIds.contains(trips.get(i).getGroupId())) {
                        GroupResponse groupResponse = new GroupResponse();
                        groupResponse.setId(group.getId());
                        groupResponse.setOwnerId(group.getOwnerId());
                        groupResponse.setMembersIds(group.getMembersIds());
                        groupResponse.setPendingUsersIds(group.getPendingUsersIds());
                        groupResponse.setTrip(tripService.loadTripById(group.getTripId()));
                        groupResponse.setChatId(group.getChatId());

                        groupResponses.add(groupResponse);

                        uniqueGroupsIds.add(trips.get(i).getGroupId());
                    }
                }
            } catch(Exception e) {
                continue;
            }
        }

        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<GroupResponse> getGroupResponses() {
        return groupResponses;
    }
}
