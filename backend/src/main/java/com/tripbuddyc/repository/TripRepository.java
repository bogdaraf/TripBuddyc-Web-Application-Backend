package com.tripbuddyc.repository;

import com.tripbuddyc.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findById(Integer id);

    Trip findByUserIdAndGroupId(Integer userId, Integer groupId);

    List<Trip> findAllByUserId(Integer userId);

    @Query(value = "SELECT * FROM trips WHERE "
            + "trips.date_to >= ?1 "
            + "AND trips.date_from <= ?2 "
            + "AND trips.location LIKE ?3 "
            + "AND trips.group_type LIKE ?4 "
            + "AND trips.group_size_to >= ?5 "
            + "AND trips.group_size_from <= ?6",
            nativeQuery = true)
    List<Trip> findAllByRequest(LocalDate dateFrom, LocalDate dateTo, String location, String groupType,
                                Integer groupSizeFrom, Integer groupSizeTo);
}
