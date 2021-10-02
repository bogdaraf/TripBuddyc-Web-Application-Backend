package com.tripbuddyc.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.controller.AuthController;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtUtilsTest {
    /*PREREQUISITES:
     * test@gmail.com user exists*/

    @Autowired
    JwtUtils jwtUtils;

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
        ResponseEntity<?> response =  authController.authenticateUser(jwtSignInRequest);
        JwtTokenResponse jwtTokenResponse = (JwtTokenResponse) response.getBody();
        token = jwtTokenResponse.getAccessToken();
    }

    @Test
    void getEmailFromJwtToken() {
        String emailFromToken = jwtUtils.getEmailFromJwtToken(token);

        assertEquals(emailFromToken, email);
    }

    @Test
    void validateJwtToken_WhenValid() {
        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_WhenInvalid() {
        boolean isValid = jwtUtils.validateJwtToken(token.substring(2));

        assertFalse(isValid);
    }
}