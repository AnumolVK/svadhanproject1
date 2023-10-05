package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.constants.*;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.exception.customexception.RequiredEntityNotFoundException;
import com.svadhan.collection.model.api8.UpdateRepaymentRequest;
import com.svadhan.collection.model.api8.UpdateRepaymentResponse;
import com.svadhan.collection.model.lender.Api6Response;
import com.svadhan.collection.model.request.CollectionConfirmationRequest;
import com.svadhan.collection.model.request.LoanTrendRequest;
import com.svadhan.collection.model.response.*;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.CustomerDetailsService;
import com.svadhan.collection.service.LoansService;
import com.svadhan.collection.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoansServiceImpl implements LoansService {
    private final CustomerRepository customerRepository;
    private final CustomerDetailsService customerDetailsService;
    private final LoanStatusMasterRepository loanStatusMasterRepository;
    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;
    private final TransactionRepository transactionRepository;
    private final TrustCircleCustomerMappingRepository trustCircleCustomerMappingRepository;
    private final TrustCircleRepository trustCircleRepository;
    private final TransactionStatusMasterRepository transactionStatusMasterRepository;
    private final LenderAPIService lenderAPIService;

    @Override
    public LoanDetailsResponse getActiveLoans(Long customerId) {
        LoanDetailsResponse loanDetailsResponse = new LoanDetailsResponse();

        Customer customer = customerDetailsService.getCustomerById(customerId);
        LoanStatusMaster loanStatusMaster = loanStatusMasterRepository.findByStatus(LoanStatus.ACTIVE).orElseThrow(() -> new RequiredEntityNotFoundException(String.format("Entity 'LoanStatusMaster' is not found with LoanStatus '%s'", LoanStatus.ACTIVE)));
        List<Loan> loans = loanRepository.findAllByCustomerAndLoanStatus(customer, loanStatusMaster);
        if (CollectionUtils.isEmpty(loans)) {
            throw new RequiredEntityNotFoundException(String.format("Entity 'Loan' is not found with customerId '%d'", customerId));
        }

        List<LoanDetailsDTO> loanDetailsDTOS = loans.stream().map(l -> {
            LoanDetailsDTO loanDetails = new LoanDetailsDTO();
            List<Emi> emis = emiRepository.findAllByLoanIdOrderByCreatedOnDesc(l.getId());
            loanDetails.setLoanId(l.getId());
            loanDetails.setLenderName(l.getLender().getName());
            loanDetails.setLenderLogo(l.getLender().getLogo());
            Double emiDue = emis.stream().filter(emi -> emi.getStatus().equalsIgnoreCase("PAYMENT_DUE") && emi.getDpd() > 0).mapToDouble(Emi::getDueAmount).sum();
            loanDetails.setPaymentDue(emiDue);
            loanDetails.setDueDatePassed(false);
            if (!emis.isEmpty() && emis.get(0).getDueDate().isBefore(LocalDate.now()))
                loanDetails.setDueDatePassed(true);
            Double paidAmount = emis.stream().filter(em -> em.getTransactionId() != null).mapToDouble(Emi::getDueAmount).sum();
            loanDetails.setLoanOutstanding(l.getLoanAmount() - paidAmount);
            loanDetails.setLoanAmount(l.getLoanAmount());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a");
            Api6Response loanRepaymentDetails = lenderAPIService.getLoanRepaymentDetails(l.getLenderLoanId());
            Double emi = loanRepaymentDetails.getNextEmiDue() == 0 ? 0.0 : loanRepaymentDetails.getNextEmiDue();
            LocalDateTime emiDateTime = LocalDateTime.parse(loanRepaymentDetails.getNextEmiDate(), dateTimeFormatter);
            loanDetails.setEmiDueDate(emiDateTime);
            loanDetails.setEmi(emi);
            return loanDetails;
        }).toList();
        loanDetailsResponse.setLoanDetailsDTOS(loanDetailsDTOS);
        Double loansDue = loanDetailsDTOS.stream().mapToDouble(LoanDetailsDTO::getPaymentDue).sum();
        loanDetailsResponse.setAmountDue(loansDue);
        long loanDues = loanDetailsDTOS.stream().filter(ld -> ld.getPaymentDue() > 0).count();
        loanDetailsResponse.setLoansDue((int) loanDues);
        //Collected loans and amounts
        List<TCDetailsDTO> tcDetailsDTOS = new ArrayList<>();
        Optional<TrustCircle> trustCircle = trustCircleRepository.findByCreatedFor(customerId);
        if (trustCircle.isPresent()) {
            List<TrustCircleCustomerMapping> trustCircleCustomerMappings = trustCircleCustomerMappingRepository.findAllByTrustCircleId(customerId);
            tcDetailsDTOS = trustCircleCustomerMappings.stream().map(tr -> {
                TCDetailsDTO tcDetailsDTO = new TCDetailsDTO();
                Optional<Customer> customers = customerRepository.findById(tr.getCustomerId());

                if (customers.isPresent()) {
                    tcDetailsDTO.setName(customers.get().getName());
                    tcDetailsDTO.setCustomerId(customers.get().getId());
                }
                return tcDetailsDTO;
            }).toList();
        }
        loanDetailsResponse.setTcDetailsDTOS(tcDetailsDTOS);
        loanDetailsResponse.setCollectedLoans(this.getTotalCollectedLoan(customer));
        loanDetailsResponse.setCollectedAmounts(this.getTotalCollectedAmount(customer));
        return loanDetailsResponse;
    }

    @Override
    public LoanDetailsDTO getLoanPaymentHistory(Long loanId) {
        LoanDetailsDTO loanDetailsDTO = new LoanDetailsDTO();

        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RequiredEntityNotFoundException(String.format("Entity 'Loan' not found with loan id type '%d'", loanId)));
        List<Emi> emis = emiRepository.findAllByLoanIdOrderByCreatedOnDesc(loanId);
        if (emis.isEmpty()) {
            throw new RequiredEntityNotFoundException(String.format("No EMIs found with given loan id '%d'", loanId));
        }
        String loanLenderId = StringUtils.overlay(loan.getLenderLoanId(), StringUtils.repeat("X", loan.getLenderLoanId().length() - 6), 2, loan.getLenderLoanId().length() - 4);
        loanDetailsDTO.setLoanId(loan.getId());
        loanDetailsDTO.setLoanAmount(loan.getLoanAmount());
        loanDetailsDTO.setEmi(loan.getEmi());
        loanDetailsDTO.setEmiDueDate(emis.get(0).getDueDate().atStartOfDay());
        Double paidAmount = emis.stream().filter(em -> em.getTransactionId() != null).mapToDouble(Emi::getDueAmount).sum();
        loanDetailsDTO.setLoanOutstanding(loan.getLoanAmount() - paidAmount);
        Double emiDue = emis.stream().filter(emi -> emi.getStatus().equalsIgnoreCase("PAYMENT_DUE") && emi.getDpd() > 0).mapToDouble(Emi::getDueAmount).sum();
        loanDetailsDTO.setPaymentDue(emiDue);
        if (emis.get(0).getDueDate().isBefore(LocalDate.now())) loanDetailsDTO.setDueDatePassed(true);
        List<Transaction> transactions = transactionRepository.findAllByLoan(loan);
        List<LoanPaymentHistoryDetailsDTO> loanPaymentHistoryDetailsDTOS = transactions.stream().map(t -> {
            LoanPaymentHistoryDetailsDTO loanPaymentHistoryDetailsDTO = new LoanPaymentHistoryDetailsDTO();
            loanPaymentHistoryDetailsDTO.setAmount(t.getAmount());
            loanPaymentHistoryDetailsDTO.setDate(t.getCreatedOn());
            loanPaymentHistoryDetailsDTO.setIsDueAmount(t.getIsDueAmount());
            loanPaymentHistoryDetailsDTO.setPaidBy(t.getPaidBy() != null ?t.getPaidBy().name():null);
            loanPaymentHistoryDetailsDTO.setPaymentMode(t.getPgw() != null? PaymentMode.UPI.name() : PaymentMode.AGENT.name());
            return loanPaymentHistoryDetailsDTO;
        }).toList();
        loanDetailsDTO.setLoanPaymentHistoryDetailsDTOS(loanPaymentHistoryDetailsDTOS);
        loanDetailsDTO.setMaskedLenderLoanId(loanLenderId);
        return loanDetailsDTO;
    }

    @Override
    public LoanTrendDetailsDTO getLoanTrend(LoanTrendRequest loanTrendRequest) {
        LoanTrendDetailsDTO loanTrendDetailsDTO = new LoanTrendDetailsDTO();
        Loan loan = loanRepository.findById(loanTrendRequest.getLoanId()).orElseThrow(() -> new RequiredEntityNotFoundException(String.format("Entity 'Loan' not found with given doc type '%d'", loanTrendRequest.getLoanId())));
        LocalDateTime rangeDate = LocalDateTime.now();
        if (StringUtils.equalsIgnoreCase(loanTrendRequest.getRangeType(), TrendRangeType.MONTH.name())) {
            rangeDate = LocalDateTime.now().minusMonths(loanTrendRequest.getRange());
        }
        if (StringUtils.equalsIgnoreCase(loanTrendRequest.getRangeType(), TrendRangeType.YEAR.name())) {
            rangeDate = LocalDateTime.now().minusYears(loanTrendRequest.getRange());
        }
        List<Emi> emis = emiRepository.findAllByLoanIdAndCreatedOnGreaterThanEqual(loanTrendRequest.getLoanId(), rangeDate);

        List<LoanMonthlyTrendDetailsDTO> loanMonthlyTrendDetailsDTOList = emis.stream().map(e -> {
            LoanMonthlyTrendDetailsDTO loanMonthlyTrendDetailsDTO = new LoanMonthlyTrendDetailsDTO();
            Month month = e.getCreatedOn().getMonth();
            loanMonthlyTrendDetailsDTO.setMonth(month.name());
            loanMonthlyTrendDetailsDTO.setDueDate(e.getDueDate().atStartOfDay());
            loanMonthlyTrendDetailsDTO.setPaymentDate(e.getModifiedOn());
            loanMonthlyTrendDetailsDTO.setPaymentOnTime(e.getModifiedOn() != null && e.getDueDate().isBefore(e.getModifiedOn().toLocalDate()));
            loanMonthlyTrendDetailsDTO.setDpd(e.getDpd());
            loanMonthlyTrendDetailsDTO.setDueDays(e.getDueDate().getDayOfMonth());
            return loanMonthlyTrendDetailsDTO;
        }).toList();

        Map<String, List<LoanMonthlyTrendDetailsDTO>> monthlyDetails = loanMonthlyTrendDetailsDTOList.stream().collect(Collectors.groupingBy(LoanMonthlyTrendDetailsDTO::getMonth));
        List<Map<String, Object>> monthlyLoanDetails = new ArrayList<>();
        for (Map.Entry<String, List<LoanMonthlyTrendDetailsDTO>> entry : monthlyDetails.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", entry.getKey());
            map.put("data", entry.getValue());
            monthlyLoanDetails.add(map);
        }
        loanTrendDetailsDTO.setLoanMonthlyTrendDetailsDTOList(monthlyLoanDetails);
        //TODO paidBy
        //        loanTrendDetailsDTO.setCalls();
        //        loanTrendDetailsDTO.setVisits();
        //        loanTrendDetailsDTO.setDpd();

        loanTrendDetailsDTO.setPaidBySelf(this.getTotalAmountPaidBy(loan, LoanPaidBy.SELF));
        loanTrendDetailsDTO.setPaidByTCMember(this.getTotalAmountPaidBy(loan, LoanPaidBy.TC_MEMBER));
        loanTrendDetailsDTO.setPaidByCash(this.getTotalAmountPaidBy(loan, false));
        loanTrendDetailsDTO.setPaidByDigital(this.getTotalAmountPaidBy(loan, true));
        return loanTrendDetailsDTO;
    }

    @Override
    public Transaction acknowledgeCollection(CollectionConfirmationRequest confirmationRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findById(confirmationRequest.getCustomerId());
        if (optionalCustomer.isEmpty())
            throw new RequiredEntityNotFoundException("Loan not found");
        Customer customer = optionalCustomer.get();
        List<Loan> optionalLoan = loanRepository.findAllByCustomerAndLoanStatus(customer, loanStatusMasterRepository.findByStatus(LoanStatus.ACTIVE).get());
        if (CollectionUtils.isEmpty(optionalLoan))
            throw new RequiredEntityNotFoundException("Loan not found");
        Loan loan = optionalLoan.get(0);
        Transaction transaction = getNewTransaction(confirmationRequest.getPaidAmount(), loan, "EMI");
        transaction.setPaidBy(confirmationRequest.getPaidBy());
        transaction.setIsDueAmount(this.isDueAmount(loan));
        transactionRepository.save(transaction);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        UpdateRepaymentRequest request = new UpdateRepaymentRequest(loan.getLenderLoanId(), transaction.getAmount(), "AGENT_APP", date, transaction.getId());
        String requestBody = JsonUtil.toJson(request);
        log.info("Lender API 8 Request For Loan id :{} is :{}", loan.getLenderLoanId(), requestBody);
        /**
         * API8 is called here
         */
        UpdateRepaymentResponse updateRepaymentResponse = lenderAPIService.lenderApi8UpdateRepayment(requestBody, 0);
        if (updateRepaymentResponse.getStatus() == 1) {
            loan.setLoanStatus(loanStatusMasterRepository.findByStatus(LoanStatus.ACTIVE).get());
            loanRepository.save(loan);
            TransactionStatusMaster transactionStatusMaster = transactionStatusMasterRepository.findByPgw_IdAndStatus(1l, "SUCCESS").get();
            transaction.setTransactionStatus(transactionStatusMaster);
            transactionRepository.save(transaction);
            Double paidAmount = transaction.getAmount();
            List<Emi> emiList = emiRepository.findAllByLoanIdOrderByCreatedOnAsc(loan.getId());
            for (Emi emi : emiList) {
                if (!StringUtils.equalsIgnoreCase(emi.getStatus(), "PAYMENT_DUE"))
                    continue;
                if (emi.getDueAmount() == paidAmount.longValue()) {
                    emi.setStatus("PAID");
                    emi.setTransactionId(transaction.getId());
                    emi.setDpd(0);
                    emiRepository.save(emi);
                    break;
                } else if (emi.getDueAmount() < paidAmount) {
                    emi.setStatus("PAID");
                    emi.setTransactionId(transaction.getId());
                    emi.setDpd(0);
                    emiRepository.save(emi);
                    continue;
                } else {
                    Double remainingAmount = emi.getDueAmount() - paidAmount;
                    emi.setDueAmount(remainingAmount.longValue());
                    emiRepository.save(emi);
                    Emi newEmi = new Emi();
                    newEmi.setDueAmount(paidAmount.longValue());
                    newEmi.setTransactionId(transaction.getId());
                    newEmi.setLoanId(loan.getId());
                    newEmi.setDpd(0);
                    newEmi.setCreatedOn(LocalDateTime.now());
                    newEmi.setDueDate(LocalDate.now());
                    newEmi.setStatus("PAID");
                    emiRepository.save(newEmi);
                    break;
                }
            }
        }
        if (updateRepaymentResponse.getStatus() != 1) {
            log.info("Loan Repayment for Loan {} with transcation id {} is filed for Lender Loan id {} due to :{}", loan.getId(), transaction.getId(), loan.getLenderLoanId(), updateRepaymentResponse.getMessage());
            log.info("Loan Repayment Request Body was :{}", requestBody);
        }
        return transaction;
    }

    public Transaction getNewTransaction(Double amount, Loan loan, String narration) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setLoan(loan);
        transaction.setType(TransactionType.DEBIT);
        transaction.setNarration(narration);
        TransactionStatusMaster transactionStatusMaster = transactionStatusMasterRepository.findByStatus("INITIATED").get();
        transaction.setTransactionStatus(transactionStatusMaster);
        return transaction;
    }

    public Double getTotalCollectedAmount(Customer customer){

        if (customer == null) {
            log.info("Customer can not be null for calculating total collected amount");
            return null;
        }
        List<Loan> allLoans = loanRepository.findAllByCustomer(customer);
        if (allLoans.size() == 0) {
            log.info("There are no loans found for the customer: " + customer.getId());
            return null;
        }
        return allLoans.stream().map(loan -> {
            List<Transaction> transactions = transactionRepository.findAllByLoan(loan);
            if (transactions.size() == 0) {
                log.info("There are no transactions found for the loan: " + loan.getId());
                return null;
            }
            return transactions.stream().filter(transaction -> {
                Optional<Transaction> transactionOptional = transactionRepository.findTransactionById(transaction.getId());
                return transactionOptional.isPresent();
            }).map(Transaction::getAmount).reduce(0.0, Double::sum);
        }).filter(Objects::nonNull).reduce(0.0, Double::sum);
    }
    public Integer getTotalCollectedLoan(Customer customer){

        if (customer == null) {
            log.info("Customer can not be null for calculating total collected loan");
            return null;
        }
        return loanRepository.findAllByCustomer(customer).size();
    }

    private boolean isDueAmount(Loan loan){

        return !emiRepository.findAllByLoanIdAndStatusNotInAndDueDateBefore(loan.getId(), List.of("PAID"), LocalDate.now()).isEmpty();
    }

    // NOTE: This method will return the total amount paid by (Using LoanPaidBy enum)
    private Double getTotalAmountPaidBy(Loan loan, LoanPaidBy paidBy){

        List<Transaction> transactions = transactionRepository.findAllByLoanAndTransactionStatusAndPaidBy(
                loan,
                this.getSuccessTransactionStatusEntity(),
                paidBy
        );
        return this.getTotalTransactionAmount(transactions);
    }
    // NOTE: This method will return the total amount paid by (digital[UPI] or cash)
    private Double getTotalAmountPaidBy(Loan loan, boolean isDigital){
        List<Transaction> transactions;
        if (isDigital) {
            transactions = transactionRepository.findAllByLoanAndTransactionStatusAndPgwNotNull(
                    loan,
                    this.getSuccessTransactionStatusEntity()
            );
        } else {
            transactions = transactionRepository.findAllByLoanAndTransactionStatusAndPgwNull(
                    loan,
                    this.getSuccessTransactionStatusEntity()
                    );
        }
        return this.getTotalTransactionAmount(transactions);
    }

    private TransactionStatusMaster getSuccessTransactionStatusEntity() {

        String status = "SUCCESS";
        return transactionStatusMasterRepository.findByStatus(status)
                .orElseThrow(() -> new RequiredEntityNotFoundException("Database Error: Transaction status " + status + " is missing in the status master table."));
    }

    private Double getTotalTransactionAmount(List<Transaction> transactions) {
        return transactions.stream().map(Transaction::getAmount).reduce(0.0, Double::sum);
    }
}
