package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "bills", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("groupId")
    private Integer groupId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("key")
    private String key;


    public Bill() {

    }

    public Bill(Integer groupId, String title, Integer value, String key) {
        this.groupId = groupId;
        this.title = title;
        this.value = value;
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setChatId(Integer chatId) {
        this.groupId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
