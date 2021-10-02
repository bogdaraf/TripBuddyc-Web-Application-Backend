package com.tripbuddyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripbuddyc.config.SpringContext;
import com.tripbuddyc.config.jwt.JwtUtils;
import com.tripbuddyc.model.Chat;
import com.tripbuddyc.model.User;
import com.tripbuddyc.schema.request.ChatMessageRequest;
import com.tripbuddyc.schema.response.ChatMessageResponse;
import com.tripbuddyc.service.ChatMessageService;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/***
 * This class handles text message inputs, websockets sessions and text message outputs
 * for binary messages extend from BinaryWebSocketHandler instead
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    ChatService chatService = SpringContext.getBean(ChatService.class);

    ChatMessageService chatMessageService = SpringContext.getBean(ChatMessageService.class);

    UserService userService = SpringContext.getBean(UserService.class);

    JwtUtils jwtUtils = SpringContext.getBean(JwtUtils.class);

    ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);

    final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    HashMap<Integer, Set<WebSocketSession>> sessionsHashMap = new HashMap<>();

    //final boolean sendToAllSesions = true;

    public WebSocketHandler() {

    }

    /***
     * This method gets called when a message is received from a client
     * @param session The actual websocket session that triggers this method
     * @param message The actual recived message
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String inputJSON = message.getPayload();
        ChatMessageRequest request = objectMapper.readValue(inputJSON, ChatMessageRequest.class);
        String token = request.getToken().substring(7);

        if(!jwtUtils.validateJwtToken(token)) { // user authentication
            sendErrorMessage(session, "Invalid token!");
            return;
        }

        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));

        if(user == null) {
            sendErrorMessage(session, "User does not exist!");
            return;
        }

        Chat chat = chatService.loadChatById(request.getChatId());

        if(chat == null) {
            sendErrorMessage(session, "Chat does not exist!");
            return;
        }
        if(!chat.getUsersIds().contains(user.getId())) {
            sendErrorMessage(session, "You are not a member of the chat!");
            return;
        }

        if(request.getType().equals("init")) {
            Set<WebSocketSession> sessions = sessionsHashMap.get(request.getChatId());
            if(sessions == null) {
                sessions = new HashSet<>();
            }
            if(sessions != null && !sessions.contains(session)) {
                sessions.add(session);
            }
            sessionsHashMap.put(request.getChatId(), sessions);
        }
        else {
            LocalDateTime timestamp = LocalDateTime.now();

            chatMessageService.addChatMessage(request.getChatId(), user.getId(), timestamp, request.getMessage());

            ChatMessageResponse response = new ChatMessageResponse();
            response.setType("message");
            response.setSenderId(user.getId());
            response.setName(user.getFirstName() + " " + user.getLastName());
            response.setTimestamp(timestamp);
            response.setMessage(request.getMessage());

            //Send response to all connected sessions in the chat
            for (WebSocketSession webSocketSession : sessions) {
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }
        }
    }

    /***
     * This method gets called when a client establishes a connection
     * @param session The actual websocket session that triggers this method
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        //Add to the list of connected sessions.
        sessions.add(session);
    }

    /***
     * This method gets called when a client closes the connection
     * @param session The actual websocket session that triggers this method
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        sessions.remove(session);

        for(Map.Entry<Integer, Set<WebSocketSession>> entry : sessionsHashMap.entrySet()) {
            Set<WebSocketSession> sessionsFromKey = entry.getValue();

            if(sessionsFromKey != null && sessionsFromKey.contains(session)) {
                sessionsFromKey.remove(session);

                if(sessionsFromKey.isEmpty()) {
                    sessionsHashMap.remove(entry.getKey());
                }
                else {
                    sessionsHashMap.put(entry.getKey(), sessionsFromKey);
                }
            }

            break;
        }

        session.close();
    }

    private void sendErrorMessage(WebSocketSession session, String message) throws IOException {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setType("error");
        response.setMessage(message);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    public List<WebSocketSession> getSessions() {
        return sessions;
    }

    public HashMap<Integer, Set<WebSocketSession>> getSessionsHashMap() {
        return sessionsHashMap;
    }
}
