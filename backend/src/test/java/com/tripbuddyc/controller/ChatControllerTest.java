package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.response.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatControllerTest {
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
    static Integer newGroupChatId = null;
    static Integer newPrivateChatId = null;

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

        //add a group to the trip
        MvcResult addGroupResponse =  mockMvc.perform(post("/api/groups/trip/{tripId}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupAddedResponse groupAddedResponse = objectMapper.readValue(addGroupResponse.getResponse().getContentAsString(),
                GroupAddedResponse.class);

        newGroupId = groupAddedResponse.getGroupId();

        newGroupChatId = groupAddedResponse.getChatId();

        //user tests2 joins the group - and the group chat
        mockMvc.perform(post("/api/groups/{groupId}", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andReturn();
    }

    @Test
    @Order(1)
    void getChatsByUserId_ByUserTest1_containsNewGroupChat() throws Exception {
        MvcResult getChatResponse =  mockMvc.perform(get("/api/chat")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        ChatResponse[] chatResponses = objectMapper.readValue(getChatResponse.getResponse().getContentAsString(),
                ChatResponse[].class);

        boolean containsNewChat = false;
        for (ChatResponse chatResponse: chatResponses) {
            if(chatResponse.getChatId() == newGroupChatId) {
                containsNewChat = true;

                break;
            }
        }

        assert getChatResponse.getResponse().getStatus() == HttpStatus.OK.value() && containsNewChat;
    }

    @Test
    @Order(2)
    void getChatsByUserId_ByUserTest2_containsNewGroupChat() throws Exception {
        MvcResult getChatResponse =  mockMvc.perform(get("/api/chat")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andReturn();
        ChatResponse[] chatResponses = objectMapper.readValue(getChatResponse.getResponse().getContentAsString(),
                ChatResponse[].class);

        boolean containsNewChat = false;
        for (ChatResponse chatResponse: chatResponses) {
            if(chatResponse.getChatId() == newGroupChatId) {
                containsNewChat = true;

                break;
            }
        }

        assert getChatResponse.getResponse().getStatus() == HttpStatus.OK.value() && containsNewChat;
    }

    @Test
    @Order(3)
    void addPrivateChat() throws Exception {
        MvcResult addChatResponse =  mockMvc.perform(post("/api/chat/userId/{userId}", user2Id)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        ChatAddedResponse chatAddedResponse = objectMapper.readValue(addChatResponse.getResponse().getContentAsString(),
                ChatAddedResponse.class);

        newPrivateChatId = chatAddedResponse.getChatId();

        assert addChatResponse.getResponse().getStatus() == HttpStatus.OK.value() && newPrivateChatId != 0;
    }

    @Test
    @Order(4)
    void afterAddPrivateChat_getChats_ByTheOtherUser_ReturnsThisChat() throws Exception {
        MvcResult getChatResponse =  mockMvc.perform(get("/api/chat")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(""))
                .andReturn();
        ChatResponse[] chatResponses = objectMapper.readValue(getChatResponse.getResponse().getContentAsString(),
                ChatResponse[].class);

        boolean containsNewChat = false;
        for (ChatResponse chatResponse: chatResponses) {
            if(chatResponse.getChatId() == newPrivateChatId) {
                containsNewChat = true;

                break;
            }
        }

        assert getChatResponse.getResponse().getStatus() == HttpStatus.OK.value() && containsNewChat;
    }
}