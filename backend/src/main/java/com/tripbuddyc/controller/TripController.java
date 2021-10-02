package com.tripbuddyc.controller;

import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.TripRepository;
import com.tripbuddyc.schema.response.TripAddedResponse;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.GroupService;
import com.tripbuddyc.service.TripService;
import com.tripbuddyc.service.UserService;
import com.tripbuddyc.tripbrowser.BrowsingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/trips")
public class TripController {

    @Autowired
    UserService userService;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TripService tripService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupController groupController;

    @Autowired
    ChatService chatService;

    @GetMapping(path = "/userId/{id}")
    public ResponseEntity<?> getAllUsersTrips(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer userId) {
        if(loggedUser.getId() == userId) {
            List<Trip> trips = tripService.loadTripsByUserId(userId);

            return new ResponseEntity<>(trips, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<?> addTrip(@AuthenticationPrincipal User loggedUser, @RequestBody Trip trip) {
        trip.setUserId(loggedUser.getId());
        trip.setLocation(trip.joinedLocation());

        tripRepository.save(trip);

        TripAddedResponse tripAddedResponse = new TripAddedResponse();
        tripAddedResponse.setMessage("Trip added successfully!");
        tripAddedResponse.setTripId(trip.getId());

        return new ResponseEntity<>(tripAddedResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteTrip(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer tripId) {
        Trip trip = tripService.loadTripById(tripId);

        if(trip == null) {
            return new ResponseEntity<>(new MessageResponse("Trip does not exist!"), HttpStatus.BAD_REQUEST);
        }
        if(loggedUser.getId() != trip.getUserId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the trip!"), HttpStatus.FORBIDDEN);
        }

        if(trip.getGroupId() != null) {
            groupController.leaveGroup(loggedUser, trip.getGroupId());
        }

        tripRepository.delete(trip);


        return new ResponseEntity<>(new MessageResponse("The trip removed successfully!"), HttpStatus.OK);
    }

    @PostMapping(path = "/browse")
    public ResponseEntity<?> getBrowsingResults(@AuthenticationPrincipal User loggedUser, @RequestBody Trip trip) throws Exception {
        trip.setLocation(trip.joinedLocation());

        if(trip.getDateFrom() == null || trip.getDateTo() == null || trip.getLocation() == null
                || trip.getGroupType() == null || trip.getGroupSizeFrom() == null || trip.getGroupSizeTo() == null) {
            return new ResponseEntity<>(new MessageResponse("Some of trip values are missing!"), HttpStatus.BAD_REQUEST);
        }

        List<Trip> trips = tripService.loadTripsByRequest(loggedUser, trip);

        BrowsingResults results = new BrowsingResults().getResults(loggedUser, userService, tripService, groupService,
                chatService, trips);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
