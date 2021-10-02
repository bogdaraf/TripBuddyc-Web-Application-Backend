package com.tripbuddyc.controller;

import com.tripbuddyc.model.Bill;
import com.tripbuddyc.model.Chat;
import com.tripbuddyc.model.Group;
import com.tripbuddyc.model.User;
import com.tripbuddyc.repository.BillRepository;
import com.tripbuddyc.schema.response.BillResponse;
import com.tripbuddyc.schema.response.MessageResponse;
import com.tripbuddyc.service.BillService;
import com.tripbuddyc.service.ChatService;
import com.tripbuddyc.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/groups")
public class BillController {

    @Autowired
    GroupService groupService;

    @Autowired
    BillRepository billRepository;

    @Autowired
    BillService billService;

    @GetMapping(path = "/{id}/bills")
    public ResponseEntity<?> getBillsByGroup(@AuthenticationPrincipal User loggedUser,
                                               @PathVariable("id") Integer groupId) throws Exception {
        Group group = groupService.loadGroupById(groupId);

        if(!group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not the member of the group!"), HttpStatus.BAD_REQUEST);
        }

        List<Bill> bills = billService.loadBillsByChatId(groupId);

        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @PostMapping(path = "/bills")
    public ResponseEntity<?> addBillByGroup(@AuthenticationPrincipal User loggedUser,
                                           @RequestBody Bill bill) throws Exception {
        Integer groupId = bill.getGroupId();

        if(groupId == null || bill.getTitle() == null || bill.getValue() == null) {
            return new ResponseEntity<>(new MessageResponse("Bill values are not correct!"), HttpStatus.BAD_REQUEST);
        }

        Group group = groupService.loadGroupById(groupId);

        if(group != null && !group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not the member of the group!"), HttpStatus.BAD_REQUEST);
        }

        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.BAD_REQUEST);
        }

        Integer billId = billService.addBill(bill.getGroupId(), bill.getTitle(), bill.getValue(), bill.getKey());

        BillResponse billResponse = new BillResponse();
        billResponse.setId(billId);
        billResponse.setSum(billService.getSumOfBillValuesByGroup(groupId));

        return new ResponseEntity<>(billResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/bills/delete/{id}")
    public ResponseEntity<?> deleteBill(@AuthenticationPrincipal User loggedUser, @PathVariable("id") Integer billId) {
        Bill bill = billService.loadBillById(billId);

        if(bill == null) {
            return new ResponseEntity<>(new MessageResponse("Bill does not exist!"), HttpStatus.BAD_REQUEST);
        }

        Group group = groupService.loadGroupById(bill.getGroupId());

        if(group != null && !group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not the member of the group!"), HttpStatus.BAD_REQUEST);
        }

        if(loggedUser.getId() != group.getOwnerId()) {
            return new ResponseEntity<>(new MessageResponse("You are not the owner of the group!"), HttpStatus.BAD_REQUEST);
        }

        billRepository.delete(bill);

        BillResponse billResponse = new BillResponse();
        billResponse.setId(null);
        billResponse.setSum(billService.getSumOfBillValuesByGroup(group.getId()));

        return new ResponseEntity<>(billResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/bills/sum")
    public ResponseEntity<?> getSumOfBillValuesByGroup(@AuthenticationPrincipal User loggedUser,
                                             @PathVariable("id") Integer groupId) throws Exception {
        Group group = groupService.loadGroupById(groupId);

        if(!group.getMembersIds().contains(loggedUser.getId())) {
            return new ResponseEntity<>(new MessageResponse("You are not the member of the group!"), HttpStatus.BAD_REQUEST);
        }

        BillResponse billResponse = new BillResponse();
        billResponse.setId(null);
        billResponse.setSum(billService.getSumOfBillValuesByGroup(groupId));

        return new ResponseEntity<>(billResponse, HttpStatus.OK);
    }
}
