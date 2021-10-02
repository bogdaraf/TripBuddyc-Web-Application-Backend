package com.tripbuddyc.schema.response;

public class JwtTokenResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;

    public JwtTokenResponse(String accessToken, Integer id, String email) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
