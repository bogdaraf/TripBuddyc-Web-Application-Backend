package com.tripbuddyc.service;

import com.tripbuddyc.controller.TripController;
import com.tripbuddyc.model.Group;
import com.tripbuddyc.model.Trip;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TripController tripController;

    @Transactional
    public Group loadGroupById(Integer id) {
        Group group = groupRepository.findById(id);

        return group;
    }

    @Transactional
    public List<Integer> loadAllGroupsIdsByUser(User loggedUser) {
        ResponseEntity<?> tripResponse = tripController.getAllUsersTrips(loggedUser, loggedUser.getId());
        List<Trip> trips = (List<Trip>) tripResponse.getBody();

        List<Integer> groupsIds = new ArrayList<>();

        for(int i=0; i<trips.size(); i++) {
            if(trips.get(i).getGroupId() != null) {
                groupsIds.add(trips.get(i).getGroupId());
            }
        }

        return groupsIds;
    }
}
