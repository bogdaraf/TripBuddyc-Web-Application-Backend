package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripbuddyc.config.db.IntegerListConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "chats", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @Convert(converter = IntegerListConverter.class)
    @NotBlank
    @JsonProperty("usersIds")
    private List<Integer> usersIds;


    public Chat() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUserId(Integer userId) {
        usersIds.add(userId);
    }

    public void removeUserId(Integer userId) {
        usersIds.remove(userId);
    }

    public List<Integer> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }
}
