package com.tripbuddyc.schema.response;

public class TripAddedResponse {
    private String message;

    private Integer tripId;

    public TripAddedResponse() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }
}
