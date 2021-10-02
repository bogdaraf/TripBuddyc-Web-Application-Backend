package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripbuddyc.config.db.IntegerListConverter;
import com.tripbuddyc.config.db.StringListConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("ownerId")
    private Integer ownerId;

    @Convert(converter = IntegerListConverter.class)
    @NotBlank
    @JsonProperty("membersIds")
    private List<Integer> membersIds;

    @Convert(converter = IntegerListConverter.class)
    @NotBlank
    @JsonProperty("pendingUsersIds")
    private List<Integer> pendingUsersIds;

    @JsonProperty("tripId")
    private Integer tripId;

    @JsonProperty("chatId")
    private Integer chatId;

    @JsonProperty("currency")
    private String currency;


    public Group() {

    }

    public Group(Integer ownerId, List<Integer> membersIds, List<Integer> pendingUsersIds, Integer tripId) {
        this.ownerId = ownerId;
        this.membersIds = membersIds;
        this.pendingUsersIds = pendingUsersIds;
        this.tripId = tripId;

    }

    public Integer getId() {
        return id;
    }

    public Integer getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public void addMember(Integer memberId) {
        membersIds.add(memberId);
    }

    public void removeMember(Integer memberId) {
        membersIds.remove(memberId);
    }

    public List<Integer> getMembersIds() {
        return membersIds;
    }

    public void addPendingUser(Integer userId) {
        pendingUsersIds.add(userId);
    }

    public void removePendingUser(Integer userId) {
        pendingUsersIds.remove(userId);
    }

    public List<Integer> getPendingUsersIds() {
        return pendingUsersIds;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
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
