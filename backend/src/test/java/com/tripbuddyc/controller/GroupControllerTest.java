package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.schema.response.GroupAddedResponse;
import com.tripbuddyc.schema.response.GroupResponse;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import com.tripbuddyc.schema.response.TripAddedResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupControllerTest {
    /*PREREQUISITES:
    * test@gmail.com user exists
    * test2@gmail.com user exists*/

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthController authController;

    static Integer userId = null;
    static String email = "test@gmail.com";
    static String password = "password";
    static String token = null;

    static Integer user2Id = null;
    static String email2 = "test2@gmail.com";
    static String password2 = "password";
    static String token2 = null;

    static Integer newTripId = null;
    static Integer newGroupId = null;

    @BeforeAll
    public void init() throws Exception {
        //log in as user test
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(email);
        jwtSignInRequest.setPassword(password);
        ResponseEntity<?> response =  authController.authenticateUser(jwtSignInRequest);
        JwtTokenResponse jwtTokenResponse = (JwtTokenResponse) response.getBody();
        userId = jwtTokenResponse.getId();
        token = jwtTokenResponse.getAccessToken();

        //log in as user test2
        JwtSignInRequest jwtSignInRequest2 = new JwtSignInRequest();
        jwtSignInRequest2.setEmail(email2);
        jwtSignInRequest2.setPassword(password2);
        ResponseEntity<?> response2 =  authController.authenticateUser(jwtSignInRequest2);
        JwtTokenResponse jwtTokenResponse2 = (JwtTokenResponse) response2.getBody();
        user2Id = jwtTokenResponse2.getId();
        token2 = jwtTokenResponse2.getAccessToken();

        //create a trip
        LocalDate dateFrom = LocalDate.now().plusMonths(4);
        LocalDate dateTo = LocalDate.now().plusMonths(4).plusDays(8);
        List<String> activities = new ArrayList<>();
        activities.add("road trip");

        Trip trip = new Trip(userId, "My trip", "Europe", "Germany", "", dateFrom,
                dateTo, activities, "Mixed group", 2, 5, 18, 100);

        MvcResult addTripResponse = mockMvc.perform(post("/api/trips")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(trip)))
                .andReturn();
        TripAddedResponse tripAddedResponse = objectMapper.readValue(addTripResponse.getResponse().getContentAsString(),
                TripAddedResponse.class);

        newTripId = tripAddedResponse.getTripId();
    }

    @Test
    @Order(1)
    void addGroupByTripId() throws Exception {
        MvcResult addGroupResponse =  mockMvc.perform(post("/api/groups/trip/{tripId}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupAddedResponse groupAddedResponse = objectMapper.readValue(addGroupResponse.getResponse().getContentAsString(),
                GroupAddedResponse.class);

        newGroupId = groupAddedResponse.getGroupId();

        assert addGroupResponse.getResponse().getStatus() == HttpStatus.OK.value() && newGroupId != 0;
    }

    @Test
    @Order(2)
    void getGroupById() throws Exception {
        MvcResult getGroupResponse =  mockMvc.perform(get("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupResponse groupResponse = objectMapper.readValue(getGroupResponse.getResponse().getContentAsString(),
                GroupResponse.class);

        assert getGroupResponse.getResponse().getStatus() == HttpStatus.OK.value()
                && groupResponse.getId() == newGroupId;
    }

    @Test
    @Order(3)
    void getGroupByTripId() throws Exception {
        MvcResult getGroupResponse =  mockMvc.perform(get("/api/groups/trip/{tripId}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupResponse groupResponse = objectMapper.readValue(getGroupResponse.getResponse().getContentAsString(),
                GroupResponse.class);

        assert getGroupResponse.getResponse().getStatus() == HttpStatus.OK.value()
                && groupResponse.getId() == newGroupId;
    }

    @Test
    @Order(4)
    void applyToGroupById() throws Exception {
        mockMvc.perform(post("/api/groups/apply/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void acceptUserToGroup_joinGroupById_Returns200() throws Exception {
        mockMvc.perform(post("/api/groups/accept/{groupId}/{userId}", newGroupId, user2Id)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6) //if joined the group correctly, then should be able to fetch group's info
    void acceptUserToGroup_joinGroupById_ReturnsGroup() throws Exception {
        MvcResult getGroupResponse =  mockMvc.perform(get("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andReturn();
        GroupResponse groupResponse = objectMapper.readValue(getGroupResponse.getResponse().getContentAsString(),
                GroupResponse.class);

        assert getGroupResponse.getResponse().getStatus() == HttpStatus.OK.value()
                && groupResponse.getId() == newGroupId;
    }

    @Test
    @Order(7) //user test2 leaves the group
    void leaveGroup_Returns200() throws Exception {
        mockMvc.perform(post("/api/groups/leave/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8) //if joined the group correctly, then should be able to fetch group's info
    void afterLeaveGroup_getGroup_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9) //apply again to the group
    void againApplyToGroupById() throws Exception {
        mockMvc.perform(post("/api/groups/apply/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10) //apply again to the group
    void rejectUserToGroup_Returns200() throws Exception {
        mockMvc.perform(post("/api/groups/reject/{groupId}/{userId}", newGroupId, user2Id)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11) //if rejected from admission, then still should not be able to fetch group's info
    void afterRejected_getGroup_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    void deleteGroup() throws Exception {
        mockMvc.perform(post("/api/groups/delete/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(12) //if removed the group successfully, then should not be able to fetch group's info
    void afterDeleted_getGroup_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andExpect(status().isBadRequest());
    }
}