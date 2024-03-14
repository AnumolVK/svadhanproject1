package com.svadhan.collection.service.impl;

import com.svadhan.collection.entity.Loan;
import com.svadhan.collection.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PendingCollectionServiceTest {

    @Autowired
    CustomerRepository customerRepository;


    @Test
    public void testSetLoanDueExistsTrueIfLoanDueDateOver() {
        // Create a Loan object with a start date in the past
        LocalDateTime emiStartDate = LocalDateTime.now().minusMonths(2); // Set the start date 2 months ago
        Loan loan = new Loan();
        loan.setEmiStartDate(emiStartDate);

        // Set the number of months to 2
        long months = 2;

        // Call the method
        boolean result = PendingCollectionsServiceImpl.setLoanDueExistsTrueIfLoanDueDateOver(loan, months);

        // Assert that the result is true since the due date is over
        assertTrue(result);
    }

    @Test
    public void testSetLoanDueExistsFalseIfLoanDueDateNotOver() {
        // Create a Loan object with a start date in the future
        LocalDateTime emiStartDate = LocalDateTime.now().plusMonths(1); // Set the start date 1 month in the future
        Loan loan = new Loan();
        loan.setEmiStartDate(emiStartDate);

        // Set the number of months to 1
        long months = 1;

        // Call the method
        boolean result = PendingCollectionsServiceImpl.setLoanDueExistsTrueIfLoanDueDateOver(loan, months);

        // Assert that the result is false since the due date is not over
        assertFalse(result);
    }
}
