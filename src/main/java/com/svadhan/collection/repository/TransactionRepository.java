package com.svadhan.collection.repository;


import com.svadhan.collection.constants.LoanPaidBy;
import com.svadhan.collection.constants.TransactionType;
import com.svadhan.collection.entity.Loan;
import com.svadhan.collection.entity.Transaction;
import com.svadhan.collection.entity.TransactionStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByLoan(Loan loan);
    List<Transaction> findAllByLoanAndTypeAndTransactionStatusAndNarrationIn(Loan loan, TransactionType type, TransactionStatusMaster transactionStatus, List<String> narrations);

    List<Transaction> findAllByLoanAndTransactionStatusAndPaidBy(Loan loan, TransactionStatusMaster transactionStatus, LoanPaidBy paidBy);
    List<Transaction> findAllByLoanAndTransactionStatusAndPgwNotNull(Loan loan, TransactionStatusMaster transactionStatus);
    List<Transaction> findAllByLoanAndTransactionStatusAndPgwNull(Loan loan, TransactionStatusMaster transactionStatus);
}
