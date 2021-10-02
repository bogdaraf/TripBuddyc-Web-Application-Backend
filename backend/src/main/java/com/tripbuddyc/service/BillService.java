package com.tripbuddyc.service;

import com.tripbuddyc.model.Bill;
import com.tripbuddyc.model.ChatMessage;
import com.tripbuddyc.repository.BillRepository;
import com.tripbuddyc.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    @Autowired
    BillRepository billRepository;

    @Transactional
    public Bill loadBillById(Integer id) {
        Bill bill = billRepository.findById(id);

        return bill;
    }

    @Transactional
    public List<Bill> loadBillsByChatId(Integer chatId) {
        List<Bill> bills = billRepository.findAllByGroupId(chatId);

        return bills;
    }

    @Transactional
    public Integer addBill(Integer chatId, String title, Integer value, String key) {
        Bill bill = new Bill(chatId, title, value, key);

        billRepository.save(bill);

        return bill.getId();
    }

    public Integer getSumOfBillValuesByGroup(Integer groupId) {
        List<Bill> bills = loadBillsByChatId(groupId);

        Integer sum = 0;

        for (Bill bill: bills) {
            sum += bill.getValue();
        }

        return sum;
    }
}
