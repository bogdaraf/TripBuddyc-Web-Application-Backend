package com.tripbuddyc.controller;

import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.model.Location;
import com.tripbuddyc.repository.LocationRepository;
import com.tripbuddyc.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/locations")
public class LocationController {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LocationService locationService;

    boolean countriesLoaded = false;

    @GetMapping(path = "/{continent}")
    public ResponseEntity<?> getCountriesByContinent(@PathVariable("continent") String continent) {
        if(locationRepository.count() == 0) {
            locationService.fillDatabaseWithCountries();
        }

        try {
            Location location = locationService.loadLocationByContinent(continent);

            return new ResponseEntity<>(location, HttpStatus.OK);
        } catch(Exception e) {

            return new ResponseEntity<>(new MessageResponse("Location does not exist!"), HttpStatus.NOT_FOUND);
        }
    }
}
