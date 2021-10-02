package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.response.GroupAddedResponse;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import com.tripbuddyc.schema.response.TripAddedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatMessageControllerTest {
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

    static Integer newTripId = null;
    static Integer newGroupId = null;
    static Integer newChatId = null;

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

        //create a group - it will add a group chat
        MvcResult addGroupResponse =  mockMvc.perform(post("/api/groups/trip/{tripId}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupAddedResponse groupAddedResponse = objectMapper.readValue(addGroupResponse.getResponse().getContentAsString(),
                GroupAddedResponse.class);

        newGroupId = groupAddedResponse.getGroupId();
        newChatId = groupAddedResponse.getChatId();
    }

    @Test
    void getMessagesByChat_Returns200() throws Exception {
        mockMvc.perform(get("/api/chatMessages/chat/{chatId}", newChatId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }
}