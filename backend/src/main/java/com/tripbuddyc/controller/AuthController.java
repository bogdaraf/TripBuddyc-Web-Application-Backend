package com.tripbuddyc.controller;

import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.config.jwt.*;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.UserRepository;
import com.tripbuddyc.schema.request.JwtChangePasswordRequest;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.schema.request.JwtSignUpRequest;
import com.tripbuddyc.schema.response.JwtTokenResponse;
import com.tripbuddyc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody JwtSignUpRequest jwtSignUpRequest) {
        if(userRepository.existsByEmail(jwtSignUpRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Email is already in use!"), HttpStatus.BAD_REQUEST);
        }
        if(!jwtSignUpRequest.getEmail().contains("@")) {
            return new ResponseEntity<>(new MessageResponse("Email address is invalid!"), HttpStatus.BAD_REQUEST);
        }
        if(jwtSignUpRequest.getPassword().isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("The password is empty!"), HttpStatus.BAD_REQUEST);
        }
        if(jwtSignUpRequest.getBirthDate().isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("The birthdate is empty!"), HttpStatus.BAD_REQUEST);
        }

        // Create new user's account
        User user = new User(jwtSignUpRequest.getEmail(), encoder.encode(jwtSignUpRequest.getPassword()),
                jwtSignUpRequest.getFirstName(), jwtSignUpRequest.getLastName(), jwtSignUpRequest.getBirthDate(),
                jwtSignUpRequest.getGender(), jwtSignUpRequest.getCountry(), jwtSignUpRequest.getLanguages(),
                jwtSignUpRequest.getDescription());

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtSignUpRequest.getEmail(), jwtSignUpRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        return new ResponseEntity<>(new JwtTokenResponse(jwt, user.getId(), user.getEmail()), HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody JwtSignInRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch(Exception e) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials!"), HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        User user = (User) authentication.getPrincipal();

        return new ResponseEntity<>(new JwtTokenResponse(jwt, user.getId(), user.getEmail()), HttpStatus.OK);
    }

    @PostMapping(path = "/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User loggedUser,
                                            @Valid @RequestBody JwtChangePasswordRequest jwtChangePasswordRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loggedUser.getEmail(), jwtChangePasswordRequest.getOldPassword()));

            if(authentication.isAuthenticated()) {
                User user = userService.loadUserById(loggedUser.getId());

                user.setPassword(encoder.encode(jwtChangePasswordRequest.getNewPassword()));

                userRepository.save(user);

                return new ResponseEntity<>(new MessageResponse("Password changed successfully!"), HttpStatus.OK);
            }
        } catch(AuthenticationException e) {
            return new ResponseEntity<>(new MessageResponse("Bad credentials!"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Could not change the password!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageResponse("Could not change the password!"), HttpStatus.BAD_REQUEST);
    }


    public boolean isPasswordCorrect(User user, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password));

            if(authentication.isAuthenticated()) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }
}
