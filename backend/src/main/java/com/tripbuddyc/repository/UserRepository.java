package com.tripbuddyc.repository;

import com.tripbuddyc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
