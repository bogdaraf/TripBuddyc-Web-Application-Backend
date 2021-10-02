package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.User;
import com.tripbuddyc.schema.request.JwtChangePasswordRequest;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.request.JwtSignUpRequest;
import com.tripbuddyc.schema.response.GroupResponse;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {
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

    static String newUserEmail = "test" + ThreadLocalRandom.current().nextInt(10000, 100000) + "@gmail.com";
    static String newUserPassword = "password";
    static String newUserToken = null;

    @BeforeAll
    public void init() {
        //log in as user test
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(email);
        jwtSignInRequest.setPassword(password);
        ResponseEntity<?> response =  authController.authenticateUser(jwtSignInRequest);
        JwtTokenResponse jwtTokenResponse = (JwtTokenResponse) response.getBody();
        token = jwtTokenResponse.getAccessToken();
    }

    @Test
    @Order(1)
    public void registerUser_Returns200() throws Exception {
        JwtSignUpRequest jwtSignUpRequest = new JwtSignUpRequest();
        jwtSignUpRequest.setEmail(newUserEmail);
        jwtSignUpRequest.setPassword(newUserPassword);
        jwtSignUpRequest.setFirstName("Krzysztof");
        jwtSignUpRequest.setLastName("Krawczyk");
        jwtSignUpRequest.setBirthDate("08.09.1946");
        jwtSignUpRequest.setGender("Male");
        jwtSignUpRequest.setCountry("Poland");

        List<String> languages = new ArrayList<>();
        languages.add("Polish");
        languages.add("Russian");

        jwtSignUpRequest.setLanguages(languages);
        jwtSignUpRequest.setDescription("Polish baritone pop singer, guitarist and composer");

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignUpRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void registerUserWhenExists_ReturnsBadRequest() throws Exception {
        JwtSignUpRequest jwtSignUpRequest = new JwtSignUpRequest();
        jwtSignUpRequest.setEmail(newUserEmail);
        jwtSignUpRequest.setPassword(newUserPassword);
        jwtSignUpRequest.setFirstName("Krzysztof");
        jwtSignUpRequest.setLastName("Krawczyk");
        jwtSignUpRequest.setBirthDate("08.09.1946");
        jwtSignUpRequest.setGender("Male");
        jwtSignUpRequest.setCountry("Poland");

        List<String> languages = new ArrayList<>();
        languages.add("Polish");
        languages.add("Russian");

        jwtSignUpRequest.setLanguages(languages);
        jwtSignUpRequest.setDescription("Polish baritone pop singer, guitarist and composer");

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void afterRegisterUser_authenticateNewUser_Returns200() throws Exception {
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(newUserEmail);
        jwtSignInRequest.setPassword(newUserPassword);

        MvcResult authenticateUserResponse =  mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignInRequest)))
                .andReturn();
        JwtTokenResponse jwtTokenResponse = objectMapper.readValue(authenticateUserResponse.getResponse().getContentAsString(),
                JwtTokenResponse.class);

        newUserToken = jwtTokenResponse.getAccessToken();
    }

    @Test
    @Order(4)
    void afterRegisterUser_authenticateUser_BadCredentials_ReturnsBadRequest() throws Exception {
        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail(newUserEmail);
        jwtSignInRequest.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignInRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void changePassword() throws Exception {
        JwtChangePasswordRequest jwtChangePasswordRequest = new JwtChangePasswordRequest();
        jwtChangePasswordRequest.setOldPassword(newUserPassword);
        jwtChangePasswordRequest.setNewPassword("newPassword");

        mockMvc.perform(post("/api/auth/password")
                .contentType("application/json")
                .header("Authorization", "Bearer " + newUserToken)
                .content(objectMapper.writeValueAsString(jwtChangePasswordRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void changePasswordWhenBadOldPassword() throws Exception {
        JwtChangePasswordRequest jwtChangePasswordRequest = new JwtChangePasswordRequest();
        jwtChangePasswordRequest.setOldPassword(newUserPassword);
        jwtChangePasswordRequest.setNewPassword("newPassword");

        mockMvc.perform(post("/api/auth/password")
                .contentType("application/json")
                .header("Authorization", "Bearer " + newUserToken)
                .content(objectMapper.writeValueAsString(jwtChangePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void isPasswordCorrect() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        boolean result = authController.isPasswordCorrect(user, "password");

        assertTrue(result);
    }

    @Test
    @Order(8)
    void isPasswordCorrectWhenNotCorrect() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        boolean result = authController.isPasswordCorrect(user, "wrongPassword");

        assertFalse(result);
    }
}