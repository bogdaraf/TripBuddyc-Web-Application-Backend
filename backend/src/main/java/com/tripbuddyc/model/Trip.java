package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripbuddyc.config.db.StringListConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "trips", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @NotBlank
    @JsonProperty("userId")
    private Integer userId;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @JsonProperty("continent")
    private String continent;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @NotBlank
    @JsonProperty("location")
    @JsonIgnore
    private String location;

    @NotBlank
    @JsonProperty("dateFrom")
    @JsonFormat(pattern="d-M-yyyy")
    private LocalDate dateFrom;

    @NotBlank
    @JsonProperty("dateTo")
    @JsonFormat(pattern="d-M-yyyy")
    private LocalDate dateTo;

    @Convert(converter = StringListConverter.class)
    @NotBlank
    @JsonProperty("activities")
    private List<String> activities;

    @NotBlank
    @JsonProperty("groupType")
    private String groupType;

    @NotBlank
    @JsonProperty("groupSizeFrom")
    private Integer groupSizeFrom;

    @NotBlank
    @JsonProperty("groupSizeTo")
    private Integer groupSizeTo;

    @NotBlank
    @JsonProperty("ageRangeFrom")
    private Integer ageRangeFrom;

    @NotBlank
    @JsonProperty("ageRangeTo")
    private Integer ageRangeTo;

    @JsonProperty("groupId")
    private Integer groupId;


    public Trip() {

    }

    public Trip(Integer userId, String name, String continent, String country, String city, LocalDate dateFrom,
                LocalDate dateTo, List<String> activities, String groupType, Integer groupSizeFrom,
                Integer groupSizeTo, Integer ageRangeFrom, Integer ageRangeTo) {
        this.userId = userId;
        this.name = name;
        this.continent = continent;
        this.country = country;
        this.city = city;
        this.location = joinedLocation();
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.activities = activities;
        this.groupType = groupType;
        this.groupSizeFrom = groupSizeFrom;
        this.groupSizeTo = groupSizeTo;
        this.ageRangeFrom = ageRangeFrom;
        this.ageRangeTo = ageRangeTo;
        groupId = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGroupType() {
        return groupType;
    }

    public Integer getGroupSizeFrom() {
        return groupSizeFrom;
    }

    public Integer getGroupSizeTo() {
        return groupSizeTo;
    }

    public Integer getAgeRangeFrom() {
        return ageRangeFrom;
    }

    public Integer getAgeRangeTo() {
        return ageRangeTo;
    }

    public List<String> getActivities() {
        return activities;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String joinedLocation() {
        if(continent == null) {
            continent = "";
        }
        if(country == null) {
            country = "";
        }
        if(city == null) {
            city = "";
        }

        return new String(continent + ";" + country + ";" + city);
    }
}
