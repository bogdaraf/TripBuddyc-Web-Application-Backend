package com.tripbuddyc.controller;

import com.tripbuddyc.model.Chat;
import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.schema.request.JwtSignInRequest;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.UserRepository;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    AuthController authController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserById(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer id) {
        try {
            User user = userService.loadUserById(id);

            Integer userCalculatedAge = user.calculateAge(user.getBirthDate());
            if(user.getAge() != userCalculatedAge) {
                user.setAge(userCalculatedAge);

                userRepository.save(user);
            }

            List<Chat> chats = chatService.loadAllByUserId(loggedUser.getId());

            Integer connectedChatId = null;

            for(int i=0; i<chats.size(); i++) {
                if(chats.get(i).getName() == null && chats.get(i).getUsersIds().contains(id)) {
                    connectedChatId = chats.get(i).getId();

                    break;
                }
            }

            if(loggedUser.getId() != id) {
                user.setConnectedChatId(connectedChatId);
            } else {
                user.setConnectedChatId(null);
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{id}/gender")
    public ResponseEntity<?> getGenderById(@PathVariable("id") Integer id) {
        try {
            User user = userService.loadUserById(id);

            return new ResponseEntity<>(new MessageResponse(user.getGender()), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(new MessageResponse("User does not exist!"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<?> editUser(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer id,
                                        @Valid @RequestBody User newInfo) {
        if(loggedUser.getId() == id) {
            try {
                User user = userService.loadUserById(id);

                if(newInfo.getFirstName() != null && newInfo.getFirstName() != "") {
                    user.setFirstName(newInfo.getFirstName());
                }
                if(newInfo.getLastName() != null && newInfo.getLastName() != "") {
                    user.setLastName(newInfo.getLastName());
                }
                if(newInfo.getBirthDate() != null && newInfo.getBirthDate() != "" && newInfo.getBirthDate().length() <= 10) {
                    user.setBirthDate(newInfo.getBirthDate());
                }
                if(newInfo.getGender() != null && newInfo.getGender() != "") {
                    user.setGender(newInfo.getGender());
                }
                if(newInfo.getCountry() != null && newInfo.getCountry() != "") {
                    user.setCountry(newInfo.getCountry());
                }
                if(newInfo.getLanguages() != null && newInfo.getLanguages().size() != 0) {
                    user.setLanguages(newInfo.getLanguages());
                }
                if(newInfo.getDescription() != null && newInfo.getDescription() != "") {
                    user.setDescription(newInfo.getDescription());
                }

                userRepository.save(user);

                return new ResponseEntity<>(new MessageResponse("Profile updated successfully!"), HttpStatus.OK);
            } catch(Exception e) {
                return new ResponseEntity<>(new MessageResponse("Profile could not be updated!"), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/{id}/image")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal User loggedUser,
                                         @PathVariable("id") Integer id,
                                         @RequestParam("file") MultipartFile file) throws IOException {
        if(loggedUser.getId() == id) {
            try {
                User user = userService.loadUserById(id);

                user.setPicType(file.getContentType());
                user.setPicByte(file.getBytes());

                userRepository.save(user);

                return new ResponseEntity<>(new MessageResponse("Image uploaded successfully!"), HttpStatus.OK);
            } catch(Exception e) {
                return new ResponseEntity<>(new MessageResponse("Image could not be uploaded!"), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer id,
                                        @Valid @RequestBody JwtSignInRequest jwtSignInRequest) throws Exception {
        if(loggedUser.getId() == id) {
            User user = userService.loadUserById(id);

            if(authController.isPasswordCorrect(user, jwtSignInRequest.getPassword())) {
                userRepository.delete(user);

                return new ResponseEntity<>(new MessageResponse("Profile deleted successfully!"), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new MessageResponse("Incorrect password!"), HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

}
