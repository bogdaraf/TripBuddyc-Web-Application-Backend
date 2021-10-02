package com.tripbuddyc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.controller.AuthController;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.response.GroupResponse;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebSecurityConfigTest {
    /*PREREQUISITES:
     * test@gmail.com user exists*/

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthController authController;

    static String email = "test@gmail.com";
    static String password = "password";
    static String token = null;

    @BeforeAll
    public void init() throws Exception {
        //log in as user test
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(email);
        jwtSignInRequest.setPassword(password);
        ResponseEntity<?> response = authController.authenticateUser(jwtSignInRequest);
        JwtTokenResponse jwtTokenResponse = (JwtTokenResponse) response.getBody();
        token = jwtTokenResponse.getAccessToken();
    }

    @Test //group id does not matter
    void ifAuthenticationNeeded_WhenAuthenticated_DoesNotReturnUnauthorized() throws Exception {
        MvcResult getGroupResponse =  mockMvc.perform(get("/api/groups/{groupId}", 1)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();

        assert getGroupResponse.getResponse().getStatus() != HttpStatus.UNAUTHORIZED.value();
    }

    @Test //group id does not matter
    void ifAuthenticationNeeded_WhenUnauthenticated_ReturnsUnauthorized() throws Exception {
        MvcResult getGroupResponse =  mockMvc.perform(get("/api/groups/{groupId}", 1)
                .contentType("application/json")
                .content(""))
                .andReturn();

        assert getGroupResponse.getResponse().getStatus() == HttpStatus.UNAUTHORIZED.value();
    }

    @Test //group id does not matter
    void ifAuthenticationNotNeeded_DoesNotReturnUnauthorized() throws Exception {
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(email);
        jwtSignInRequest.setPassword(password);

        MvcResult authenticateUserResponse =  mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignInRequest)))
                .andReturn();

        assert authenticateUserResponse.getResponse().getStatus() != HttpStatus.UNAUTHORIZED.value();
    }
}