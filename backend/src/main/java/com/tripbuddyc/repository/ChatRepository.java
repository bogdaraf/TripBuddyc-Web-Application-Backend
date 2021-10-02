package com.tripbuddyc.repository;

import com.tripbuddyc.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat findById(Integer id);

    List<Chat> findAll();
}
