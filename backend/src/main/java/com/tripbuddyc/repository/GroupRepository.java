package com.tripbuddyc.repository;

import com.tripbuddyc.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findById(Integer id);
}
