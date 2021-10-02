package com.tripbuddyc.repository;

import com.tripbuddyc.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Bill findById(Integer id);

    List<Bill> findAllByGroupId(Integer groupId);
}
