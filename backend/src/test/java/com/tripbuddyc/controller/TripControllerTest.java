package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.request.JwtSignUpRequest;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import com.tripbuddyc.schema.response.TripAddedResponse;
import com.tripbuddyc.tripbrowser.BrowsingResults;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TripControllerTest {
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

    @BeforeAll
    public void init() {
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
    }

    @Test
    @Order(1)
    void addTrip() throws Exception {
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

        assert addTripResponse.getResponse().getStatus() == HttpStatus.OK.value()
                && newTripId != null;
    }

    @Test
    @Order(2)
    void getAllUsersTrips_Returns200() throws Exception {
        mockMvc.perform(get("/api/trips/userId/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void afterAddTrip_getAllUsersTrips_ReturnsThisTrip() throws Exception {
        MvcResult response =  mockMvc.perform(get("/api/trips/userId/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        Trip[] trips = objectMapper.readValue(response.getResponse().getContentAsString(), Trip[].class);

        boolean isTrue = false;
        for (Trip trip: trips) {
            if(trip.getId() == newTripId) {
                isTrue = true;

                break;
            }
        }

        assert isTrue;
    }

    @Test
    @Order(4)
    void getBrowsingResults() throws Exception {
        LocalDate dateFrom = LocalDate.now().plusMonths(4).minusDays(2);
        LocalDate dateTo = LocalDate.now().plusMonths(4).plusDays(4);
        List<String> activities = new ArrayList<>();
        activities.add("road trip");
        activities.add("clubbing");

        Trip trip = new Trip(user2Id, "My test trip", "Europe", "Germany", "", dateFrom,
                dateTo, activities, "Mixed group", 4, 6, 18, 100);

        MvcResult browseResponse = mockMvc.perform(post("/api/trips/browse")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token2)
                .content(objectMapper.writeValueAsString(trip)))
                .andReturn();
        BrowsingResults browsingResults = objectMapper.readValue(browseResponse.getResponse().getContentAsString(),
                BrowsingResults.class);

        assert browsingResults.getUsers() != null && !browsingResults.getUsers().isEmpty();
    }

    @Test
    @Order(5)
    void deleteTrip_Returns200() throws Exception {
        mockMvc.perform(post("/api/trips/delete/{id}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void afterDeleteTrip_DoesNotExist() throws Exception {
        MvcResult response =  mockMvc.perform(get("/api/trips/userId/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        Trip[] trips = objectMapper.readValue(response.getResponse().getContentAsString(), Trip[].class);

        boolean isTrue = true;
        for (Trip trip: trips) {
            if(trip.getId() == newTripId) {
                isTrue = false;

                break;
            }
        }

        assert isTrue;
    }
}