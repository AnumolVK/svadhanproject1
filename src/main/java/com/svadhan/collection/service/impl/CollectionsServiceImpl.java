package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.exception.customexception.RequiredEntityNotFoundException;
import com.svadhan.collection.model.response.CollectionDetailsResponse;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.CollectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class CollectionsServiceImpl implements CollectionsService {

    private final EmployeeRepository employeeRepository;
    private final CustomerOcrDataRepository customerOcrDataRepository;
    private final LoanRepository loanRepository;
    private final VillagePinCodeRepository villagePinCodeRepository;
    private final EmiRepository emiRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    @Value("${svadhan.collection.agent.deposit-time}")
    private Integer agentPaymentPeriod;

    public CollectionsServiceImpl(EmployeeRepository employeeRepository, CustomerOcrDataRepository customerOcrDataRepository, LoanRepository loanRepository, VillagePinCodeRepository villagePinCodeRepository, EmiRepository emiRepository, TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.employeeRepository = employeeRepository;
        this.customerOcrDataRepository = customerOcrDataRepository;
        this.loanRepository = loanRepository;
        this.villagePinCodeRepository = villagePinCodeRepository;
        this.emiRepository = emiRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void confirmCollection() {
        //TODO call lender API 8
    }

    @Override
    public CollectionDetailsResponse getCompletedCollections(Long agentId) {
        CollectionDetailsResponse collectionDetailsResponse = new CollectionDetailsResponse();

        Employee employee = employeeRepository.findById(agentId).orElseThrow(() -> {
            throw new RequiredEntityNotFoundException(String.format("Entity 'Employee' is not found with ID '%d'", agentId));
        });
        List<Loan> loans = this.getAllCustomerAssignedToAgent(employee).stream()
                .map(this::getAllLoans)
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
        List<Emi> totalAgentCollectedEMIs = loans.stream()
            .map(this::getAllEMIsPaidToAgent)
            .reduce(new ArrayList<>(), (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            });
        Double totalCollectedAmount = totalAgentCollectedEMIs.stream()
                .map(Emi::getTransactionId)
                .map(transactionId -> {
                    Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
                    if (transactionOptional.isEmpty()) {
                        log.info("Couldn't find a transaction with the Id: " + transactionId);
                        return null;
                    }
                    return transactionOptional.get();
                }).filter(Objects::nonNull)
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);
        //Long totalCollectedAmount = totalAgentCollectedEMIs.stream().map(Emi::getDueAmount).reduce(0L, Long::sum);
        Double totalDepositedAmount = 0.0; // TODO: Implement this after implementing collection repayment using Airtel payment bank
        Double depositPendingAmount = totalCollectedAmount - totalDepositedAmount;
        collectionDetailsResponse.setTotalAmountCollected(totalCollectedAmount);
        collectionDetailsResponse.setDepositedSum(totalDepositedAmount);
        collectionDetailsResponse.setDepositPendingSum(depositPendingAmount);
        //TODO: Change the timeLeftToDeposit after implementing collection repayment using Airtel payment bank
        LocalDateTime firstCollectionTime = null;
        for (Emi emi: totalAgentCollectedEMIs) {
            Optional<Transaction> transactionOptional = transactionRepository.findById(emi.getTransactionId());
            if (transactionOptional.isEmpty()) continue;
            Transaction transaction = transactionOptional.get();
            if (transaction.getCreatedOn() == null) continue;
            if (firstCollectionTime == null) {
                firstCollectionTime = transaction.getCreatedOn();
            }
            else if (firstCollectionTime.isAfter(transaction.getCreatedOn())) {
                firstCollectionTime = transaction.getCreatedOn();
            }
        }
        if (firstCollectionTime != null) {
            collectionDetailsResponse.setTimeLeftToDeposit(ChronoUnit.HOURS.between(firstCollectionTime.plusHours(agentPaymentPeriod), LocalDateTime.now()));
        }
        //TODO : Implement
        collectionDetailsResponse.setDepositedDetailsDTOS(new ArrayList<>());
        collectionDetailsResponse.setDepositPendingDetailsDTOS(new ArrayList<>());
        return collectionDetailsResponse;
    }

    private List<Customer> getAllCustomerAssignedToAgent(Employee employee) {
        Set<String> agentAssignedPinCodes = employee.getAssignedPins();
        if (agentAssignedPinCodes == null || agentAssignedPinCodes.isEmpty()) {
            log.info("There are no pin codes assigned to the agent: " + employee.getId());
            return new ArrayList<>();
        }
        return customerOcrDataRepository.findAllByPinCodeIn(agentAssignedPinCodes.stream().toList()).stream()
            .map(CustomerOcrData::getCustomer).toList();
    }

    private List<Loan> getAllLoans(Customer customer) {
        List<Loan> loans = loanRepository.findAllByCustomer(customer);
        if (loans.isEmpty()) {
            log.info("There are no loans found for the customer: " + customer.getId());
        }
        return loans;
    }

    private List<Emi> getAllEMIsPaidToAgent(Loan loan) {
        return emiRepository.findAllByLoanIdAndStatus(loan.getId(), "PAID").stream()
                .filter(this::isPaidToAgent).toList();
    }

    private boolean isPaidToAgent(Emi emi) {
        if (emi == null) return false;
        Transaction transaction = transactionRepository.findById(emi.getTransactionId()).orElse(null);
        if (transaction == null) return false;
        return transaction.getPgw() == null;
    }
}
