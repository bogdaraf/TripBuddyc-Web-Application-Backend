package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.Bill;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillControllerTest {
    /*PREREQUISITES:
     * test@gmail.com user exists*/

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
    static Integer newBillId = null;

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

        //add a group to the trip
        MvcResult addGroupResponse =  mockMvc.perform(post("/api/groups/trip/{tripId}", newTripId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        GroupAddedResponse groupAddedResponse = objectMapper.readValue(addGroupResponse.getResponse().getContentAsString(),
                GroupAddedResponse.class);

        newGroupId = groupAddedResponse.getGroupId();
    }

    @Test
    @Order(1)
    void addBillByGroup() throws Exception {
        Bill bill = new Bill();
        bill.setChatId(newGroupId);
        bill.setTitle("gas");
        bill.setValue(500);

        MvcResult addBillResponse =  mockMvc.perform(post("/api/groups/bills")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(bill)))
                .andReturn();
        BillResponse billResponse = objectMapper.readValue(addBillResponse.getResponse().getContentAsString(),
                BillResponse.class);

        newBillId = billResponse.getId();

        assert addBillResponse.getResponse().getStatus() == HttpStatus.OK.value() && newBillId != 0;
    }

    @Test
    @Order(2)
    void getBillsByGroup_containsBill() throws Exception {
        MvcResult getBillResponse =  mockMvc.perform(get("/api/groups/{groupId}/bills", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        Bill[] bills = objectMapper.readValue(getBillResponse.getResponse().getContentAsString(),
                Bill[].class);

        boolean containsNewBill = false;
        for (Bill bill: bills) {
            if(bill.getId() == newBillId) {
                containsNewBill = true;

                break;
            }
        }

        assert getBillResponse.getResponse().getStatus() == HttpStatus.OK.value() && containsNewBill;
    }

    @Test
    @Order(3)
    void getSumOfBillValuesByGroup() throws Exception {
        MvcResult getBillResponse =  mockMvc.perform(get("/api/groups/{groupId}/bills/sum", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        BillResponse billResponse = objectMapper.readValue(getBillResponse.getResponse().getContentAsString(),
                BillResponse.class);

        assert getBillResponse.getResponse().getStatus() == HttpStatus.OK.value() && billResponse.getSum() == 500;
    }

    @Test
    @Order(4)
    void deleteBill() throws Exception {
        mockMvc.perform(post("/api/groups/bills/delete/{billId}", newBillId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void getBillsByGroup_doesNotContainBill() throws Exception {
        MvcResult getBillResponse =  mockMvc.perform(get("/api/groups/{groupId}/bills", newGroupId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        BillResponse[] billResponses = objectMapper.readValue(getBillResponse.getResponse().getContentAsString(),
                BillResponse[].class);

        boolean containsNewBill = false;
        for (BillResponse billResponse: billResponses) {
            if(billResponse.getId() == newBillId) {
                containsNewBill = true;

                break;
            }
        }

        assert getBillResponse.getResponse().getStatus() == HttpStatus.OK.value() && !containsNewBill;
    }
}