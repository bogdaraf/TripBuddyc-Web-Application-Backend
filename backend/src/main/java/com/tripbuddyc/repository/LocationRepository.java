package com.tripbuddyc.repository;

import com.tripbuddyc.model.Location;
import com.tripbuddyc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByContinent(String continent);
}
