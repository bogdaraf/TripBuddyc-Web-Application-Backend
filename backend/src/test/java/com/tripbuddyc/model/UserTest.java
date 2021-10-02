package com.tripbuddyc.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void calculateAge_Test1() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(28);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(28, result);
    }

    @Test
    void calculateAge_Test2() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(24).plusMonths(4);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(23, result);
    }

    @Test
    void calculateAge_Test3() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(32).minusMonths(2);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(32, result);
    }

    @Test
    void calculateAge_Test4() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(28).plusMonths(6).plusDays(12);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(27, result);
    }

    @Test
    void calculateAge_Test5() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(20).plusMonths(6).minusDays(4);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(19, result);
    }

    @Test
    void calculateAge_Test6() {
        User user = new User();

        LocalDate date = LocalDate.now().minusYears(18).plusMonths(11).plusDays(30);
        Integer year = date.getYear();
        String dateString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + year;
        Integer result = user.calculateAge(dateString);

        assertEquals(17, result);
    }
}