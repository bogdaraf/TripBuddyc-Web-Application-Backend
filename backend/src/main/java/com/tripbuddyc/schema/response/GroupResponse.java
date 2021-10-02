package com.tripbuddyc.schema.response;

import com.tripbuddyc.model.Trip;

import java.util.List;

public class GroupResponse {

    private Integer id;

    private Integer ownerId;

    private List<Integer> membersIds;

    private List<Integer> pendingUsersIds;

    private Trip trip;

    private Integer chatId;

    private String currency;


    public GroupResponse() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public List<Integer> getMembersIds() {
        return membersIds;
    }

    public void setMembersIds(List<Integer> membersIds) {
        this.membersIds = membersIds;
    }

    public List<Integer> getPendingUsersIds() {
        return pendingUsersIds;
    }

    public void setPendingUsersIds(List<Integer> pendingUsersIds) {
        this.pendingUsersIds = pendingUsersIds;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
