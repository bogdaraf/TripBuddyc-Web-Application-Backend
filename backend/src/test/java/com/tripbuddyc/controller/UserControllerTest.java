package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.model.User;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.request.JwtSignUpRequest;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import com.tripbuddyc.schema.response.MessageResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
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
    }

    @Test
    @Order(1)
    void getUserById_Returns200() throws Exception {
        mockMvc.perform(get("/api/users/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void getUserById_ReturnsUser() throws Exception {
        MvcResult response = mockMvc.perform(get("/api/users/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        User user = objectMapper.readValue(response.getResponse().getContentAsString(), User.class);

        assert user.getFirstName() != null && user.getFirstName() != ""
                && user.getLastName() != null && user.getLastName() != "";
    }

    @Test
    @Order(3)
    void getUserById_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 107486)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void getGenderById_Returns200() throws Exception {
        mockMvc.perform(get("/api/users/{id}/gender", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void getGenderById_ReturnsGender() throws Exception {
        MvcResult response = mockMvc.perform(get("/api/users/{id}/gender", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        String gender = objectMapper.readValue(response.getResponse().getContentAsString(), MessageResponse.class)
                .getMessage();

        assert gender.equals("Male") || gender.equals("Female");
    }

    @Test
    @Order(6)
    void editUser_Returns200() throws Exception {
        User user = new User();
        user.setFirstName("Maryla");
        user.setLastName("Rodowicz");
        user.setGender("Female");

        mockMvc.perform(post("/api/users/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void editUser_EditsUser() throws Exception {
        User user = new User();
        user.setFirstName("Krzysztof");
        user.setLastName("Krawczyk");
        user.setGender("Male");

        mockMvc.perform(post("/api/users/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(user)))
                .andReturn();

        MvcResult response = mockMvc.perform(get("/api/users/{id}", userId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(""))
                .andReturn();
        User editedUser = objectMapper.readValue(response.getResponse().getContentAsString(), User.class);

        assert editedUser.getFirstName().equals("Krzysztof") && editedUser.getLastName().equals("Krawczyk")
                && editedUser.getGender().equals("Male");
    }

    @Test
    @Order(8)
    void deleteUser_Returns200() throws Exception {
        JwtSignUpRequest jwtSignUpRequest = new JwtSignUpRequest();
        jwtSignUpRequest.setEmail("test107886@gmail.com");
        jwtSignUpRequest.setPassword("password");
        jwtSignUpRequest.setFirstName("Krzysztof");
        jwtSignUpRequest.setLastName("Krawczyk");
        jwtSignUpRequest.setBirthDate("08.09.1946");
        jwtSignUpRequest.setGender("Male");

        MvcResult registerResponse = mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignUpRequest)))
                .andReturn();
        JwtTokenResponse jwtTokenResponse = objectMapper.readValue(registerResponse.getResponse().getContentAsString(),
                JwtTokenResponse.class);
        Integer idToDelete = jwtTokenResponse.getId();
        String tokenToDelete = jwtTokenResponse.getAccessToken();

        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail("test107886@gmail.com");
        jwtSignInRequest.setPassword("password");

        mockMvc.perform(post("/api/users/delete/{id}", idToDelete)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenToDelete)
                .content(objectMapper.writeValueAsString(jwtSignInRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    void deleteUser_DeletesUser() throws Exception {
        JwtSignUpRequest jwtSignUpRequest = new JwtSignUpRequest();
        jwtSignUpRequest.setEmail("test107886@gmail.com");
        jwtSignUpRequest.setPassword("password");
        jwtSignUpRequest.setFirstName("Krzysztof");
        jwtSignUpRequest.setLastName("Krawczyk");
        jwtSignUpRequest.setBirthDate("08.09.1946");
        jwtSignUpRequest.setGender("Male");

        MvcResult registerResponse = mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(jwtSignUpRequest)))
                .andReturn();

        JwtTokenResponse jwtTokenResponse = objectMapper.readValue(registerResponse.getResponse().getContentAsString(),
                JwtTokenResponse.class);
        Integer idToDelete = jwtTokenResponse.getId();
        String tokenToDelete = jwtTokenResponse.getAccessToken();

        JwtSignInRequest jwtSignInRequest = new JwtSignInRequest();
        jwtSignInRequest.setEmail("test107886@gmail.com");
        jwtSignInRequest.setPassword("password");

        mockMvc.perform(post("/api/users/delete/{id}", idToDelete)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenToDelete)
                .content(objectMapper.writeValueAsString(jwtSignInRequest)))
                .andReturn();

        mockMvc.perform(get("/api/users/{id}", idToDelete)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenToDelete)
                .content(""))
                .andExpect(status().isUnauthorized());
    }
}