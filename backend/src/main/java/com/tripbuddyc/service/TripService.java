package com.tripbuddyc.service;

import com.tripbuddyc.model.Trip;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TripService {

    @Autowired
    UserService userService;

    @Autowired
    TripRepository tripRepository;

    @Transactional
    public Trip loadTripById(Integer id) {
        Trip trip = tripRepository.findById(id);

        return trip;
    }

    @Transactional
    public Trip loadTripByUserIdAndGroupId(Integer userId, Integer groupId) {
        Trip trip = tripRepository.findByUserIdAndGroupId(userId, groupId);

        return trip;
    }

    @Transactional
    public List<Trip> loadTripsByUserId(Integer userId) {
        List<Trip> trips = tripRepository.findAllByUserId(userId);

        return trips;
    }

    @Transactional
    public List<Trip> loadTripsByRequest(User loggedUser, Trip trip) throws Exception {
        List<Trip> trips = tripRepository.findAllByRequest(trip.getDateFrom(), trip.getDateTo(), trip.getLocation(),
                trip.getGroupType(), trip.getGroupSizeFrom(), trip.getGroupSizeTo());

        Set<Trip> tripsToRemove = new HashSet<>();

        User userA = loggedUser;

        for(int i=0; i<trips.size(); i++) {

            //Check whether they have at least one common activity
            List<String> commonActivities = new ArrayList<>(trip.getActivities());
            commonActivities.retainAll(trips.get(i).getActivities());

            if(commonActivities.isEmpty()) {
                tripsToRemove.add(trips.get(i));
            }

            //Check whether two users are in the age of each other interest
            if(loggedUser.getId() == trips.get(i).getUserId()) {
                tripsToRemove.add(trips.get(i));

                continue;
            }

            User userB = userService.loadUserById(trips.get(i).getUserId());

            if(userB.getAge() < trip.getAgeRangeFrom() || userB.getAge() > trip.getAgeRangeTo()
                || userA.getAge() < trips.get(i).getAgeRangeFrom() || userA.getAge() > trips.get(i).getAgeRangeTo()) {
                tripsToRemove.add(trips.get(i));

                continue;
            }
        }

        trips.removeAll(tripsToRemove);

        return trips;
    }
}
