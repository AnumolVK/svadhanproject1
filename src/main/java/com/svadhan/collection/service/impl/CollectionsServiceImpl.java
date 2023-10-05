package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.exception.customexception.RequiredEntityNotFoundException;
import com.svadhan.collection.model.response.CollectionDetailsResponse;
import com.svadhan.collection.model.response.DepositDetailsDTO;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.CollectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        log.info("loans of the customers : "+loans.size());
        List<Emi> totalAgentCollectedEMIs = loans.stream()
            .map(this::getAllEMIsPaidToAgent)
            .reduce(new ArrayList<>(), (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            });
        log.info("Emi for the agent :"+totalAgentCollectedEMIs.size());
        Double totalCollectedAmount = totalAgentCollectedEMIs.stream()
                .map(Emi::getTransactionId)
                .map(transactionId -> {
                    Optional<Transaction> transactionOptional = transactionRepository.findTransactionById(transactionId);
                    if (transactionOptional.isEmpty()) {
                        log.info("Couldn't find a transaction with the Id: " + transactionId);
                        return null;
                    }
                    return transactionOptional.get();
                }).filter(Objects::nonNull)
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);
        //Long totalCollectedAmount = totalAgentCollectedEMIs.stream().map(Emi::getDueAmount).reduce(0L, Long::sum);
        Double totalDepositedAmount = this.getTotalDepositedAmount();
        if (totalDepositedAmount == null){
            totalDepositedAmount = 0.0;
        }
        Double depositPendingAmount = totalCollectedAmount - totalDepositedAmount;
        collectionDetailsResponse.setTotalAmountCollected(totalCollectedAmount);
        collectionDetailsResponse.setDepositedSum(totalDepositedAmount);
        collectionDetailsResponse.setDepositPendingSum(depositPendingAmount);
        //TODO: Change the timeLeftToDeposit after implementing collection repayment using Airtel payment bank
        LocalDateTime firstCollectionTime = null;
        for (Emi emi: totalAgentCollectedEMIs) {
//            Optional<Transaction> transactionOptional = transactionRepository.findById(emi.getTransactionId());
            //new change - need to change later
            Optional<Transaction> transactionOptional = transactionRepository.findTransactionById(emi.getTransactionId());
            if (transactionOptional.isEmpty()) continue;
            Transaction transaction = transactionOptional.get();
            log.info("first collection time first :"+transaction.getCreatedOn());
            if (transaction.getCreatedOn() == null) continue;
            if (firstCollectionTime == null) {
                firstCollectionTime = transaction.getCreatedOn();
            }
            else if (firstCollectionTime.isAfter(transaction.getCreatedOn())) {
                firstCollectionTime = transaction.getCreatedOn();
            }
        }

        if (firstCollectionTime != null) {
            LocalTime newTime = LocalTime.of(agentPaymentPeriod, 0); //5.00 pm
            LocalDateTime modifiedDateTime = firstCollectionTime.with(newTime);
            log.info("First transaction date after looping :"+modifiedDateTime);

            Duration duration = Duration.between(LocalDateTime.now(), modifiedDateTime);
            log.info("The Hours remaining :"+duration);
            collectionDetailsResponse.setTimeLeftToDeposit(duration.toHours());
            collectionDetailsResponse.setFirstCollectionTime(modifiedDateTime.toString());
        }

        List<DepositDetailsDTO> depositDetailsDTOList = new ArrayList<>();
        List<DepositDetailsDTO> depositDetailsPendingDTOList = new ArrayList<>();
        List<Customer> customersList = new ArrayList<>();
        customersList = customerRepository.findAllByAssociatedAgentIdAndHasRegisterProcessCompleted(agentId,true);
        log.info("Customer size : {}",customersList.size());
        for (Customer customer : customersList){
            DepositDetailsDTO depositDetailsDTO = new DepositDetailsDTO();
            DepositDetailsDTO depositDetailsPendingDTO = new DepositDetailsDTO();
            log.info("The customer id : "+customer.getId());
            depositDetailsPendingDTO.setCustomerId(customer.getId());
            depositDetailsPendingDTO.setCustomerName(customer.getName());
            depositDetailsPendingDTO.setMobileNumber(customer.getMobile());
            depositDetailsPendingDTO.setVillage(this.getVillage(customer.getId()));
            depositDetailsPendingDTO.setDuePending(this.getDuePending(customer.getId()));
            depositDetailsPendingDTO.setDueDatePassed(true);
            depositDetailsPendingDTO.setAmount(this.getAmount(customer.getId()));

            //Deposited
            depositDetailsDTO.setCustomerId(customer.getId());
            depositDetailsDTO.setCustomerName(customer.getName());
            depositDetailsDTO.setMobileNumber(customer.getMobile());
            depositDetailsDTO.setVillage(depositDetailsPendingDTO.getVillage());
            depositDetailsDTO.setDuePending(false); // todo
            depositDetailsDTO.setDueDatePassed(true); //todo
            depositDetailsDTO.setAmount(this.getDepositedAmount(customer.getId()));

            depositDetailsDTOList.add(depositDetailsDTO);
            depositDetailsPendingDTOList.add(depositDetailsPendingDTO);
        }
        //TODO : Implement
        collectionDetailsResponse.setDepositedDetailsDTOS(depositDetailsDTOList);
        collectionDetailsResponse.setDepositPendingDetailsDTOS(depositDetailsPendingDTOList);
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

    private boolean getDuePending(Long customerId) {
        boolean duePending = true;
        // Todo : Logic
        return duePending;
    }

    private Double getAmount(Long customerId) {
        Double amount = customerRepository.findAmountByCustomerID(customerId);
        if (amount==null || amount==0.0) {
            log.info("The amount is empty for the customer : " + customerId);
        }
        return amount;
    }

    private Double getDepositedAmount(Long customerId) {
        Double amount = customerRepository.findProcessedAmountByCustomerID(customerId);
        if (amount==null || amount==0.0) {
            log.info("The amount is empty for the customer : " + customerId);
        }
        return amount;
    }

    private Double getTotalDepositedAmount() {
        Double amount = customerRepository.findTotalProcessedAmountByCustomerID();
        if (amount==null || amount==0.0) {
            log.info("The amount is empty for all the customer "+amount);
        }
        return amount;
    }

    private String getVillage(Long customerId) {
        String villageName = customerRepository.findVillageNameByCustomerID(customerId);
        if (villageName==null) {
            log.info("The village name is not found for the customer: " + customerId);
        }
        return villageName;
    }

    private List<Emi> getAllEMIsPaidToAgent(Loan loan) {
        return emiRepository.findAllByLoanIdAndStatus(loan.getId(), "PAID").stream()
                .filter(this::isPaidToAgent).toList();
    }

    private boolean isPaidToAgent(Emi emi) {
        if (emi == null) return false;
        Transaction transaction = transactionRepository.findTransactionById(emi.getTransactionId()).orElse(null);
        if (transaction == null) return false;
        return transaction.getPgw() == null;
    }
}
