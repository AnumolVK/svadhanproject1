package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.constants.TransactionType;
import com.svadhan.collection.entity.Loan;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.repository.LoanRepository;
import com.svadhan.collection.constants.CollectionSortOptions;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.exception.customexception.RequiredEntityNotFoundException;
import com.svadhan.collection.model.request.PendingCollectionRequest;
import com.svadhan.collection.model.response.PendingCollectionResponse;
import com.svadhan.collection.model.response.PendingCustomerCollectionResponse;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.PendingCollectionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingCollectionsServiceImpl implements PendingCollectionsService {


    private final EmployeeRepository employeeRepository;
    private final CustomerOcrDataRepository customerOcrDataRepository;
    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;
    private final VillagePinCodeRepository villagePinCodeRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionStatusMasterRepository transactionStatusMasterRepository;

    @Override
    public PendingCollectionResponse getPendingCollections(PendingCollectionRequest pendingCollectionRequest) {
        PendingCollectionResponse pendingCollectionResponse = new PendingCollectionResponse();
        Employee employee = employeeRepository.findById(pendingCollectionRequest.getAgentId())
                .orElseThrow(() -> {
                    throw new RequiredEntityNotFoundException(String.format("Entity 'Employee' is not found with ID '%d'", pendingCollectionRequest.getAgentId()));
                });

        List<CustomerOcrData> customerOcrData = customerOcrDataRepository.findAllByPinCodeIn(new ArrayList<>(employee.getAssignedPins()));
        if (customerOcrData.isEmpty()) {
            throw new RequiredEntityNotFoundException(String.format("No customer details found for the assigned pin code '%s'", employee.getAssignedPins()));
        }

        List<Customer> customers = customerRepository.findAllByAssociatedAgentIdAndHasRegisterProcessCompleted(employee.getId(), true);
        pendingCollectionResponse.setCustomersDue(customers.size());
        pendingCollectionResponse.setLoansDue(customers.size());
        double amountDue = 0.0;
        List<PendingCustomerCollectionResponse> customerCategoryOne = new ArrayList<>();
        List<PendingCustomerCollectionResponse> customerCategoryTwo = new ArrayList<>();
        List<PendingCustomerCollectionResponse> customerCategoryThree = new ArrayList<>();
        List<PendingCustomerCollectionResponse> customerCategoryFour = new ArrayList<>();
        List<PendingCustomerCollectionResponse> pendingCustomerCollectionResponses = new ArrayList<>();
        for (Customer customer : customers) {
            PendingCustomerCollectionResponse pendingCustomerCollectionResponse = new PendingCustomerCollectionResponse();
            List<Loan> loans = loanRepository.findAllByCustomer(customer);
            if (CollectionUtils.isEmpty(loans))
                continue;
            pendingCustomerCollectionResponse.setId(customer.getId());
            pendingCustomerCollectionResponse.setName(customer.getName());
            pendingCustomerCollectionResponse.setMobileNumber(customer.getMobile());
            pendingCustomerCollectionResponse.setDpd(0);
            //Post office details
            log.info("The customer Post office : {}",customer.getCustomerBasicDetail().getPostOffice());
            if (customer.getCustomerBasicDetail().getPostOffice().isEmpty()){
                pendingCustomerCollectionResponse.setPostOffice("NA");
            }
            else {
                pendingCustomerCollectionResponse.setPostOffice(customer.getCustomerBasicDetail().getPostOffice());
            }
            //TODO Mutiple pinCode for a single Agent/Employee
            if (customer.getCustomerOcrData() == null) {
                log.info("Customer OCR data is missing for the customer :" + customer.getId());
            } else {
                pendingCustomerCollectionResponse.setPinCode(customer.getCustomerOcrData().getPinCode());
            }
            //List<VillagePinCodeList> village = villagePinCodeRepository.findByPinCode(customer.getCustomerOcrData().getPinCode());
            //pendingCustomerCollectionResponse.setVillage(!CollectionUtils.isEmpty(village) ? village.get(0).getVillageName() : "");
            pendingCustomerCollectionResponse.setVillage(customer.getCustomerBasicDetail() != null ? customer.getCustomerBasicDetail().getVillage():null);
            boolean categoryOne = true;
            boolean categoryTwo = false;
            boolean categoryThree = false;
            boolean categoryFour = false;
            double emiDue = 0.0;
            for (Loan loan : loans) {
                List<Emi> emis = emiRepository.findAllByLoanIdOrderByCreatedOnDesc(loan.getId());
                for (Emi emi : emis) {
                    if (!StringUtils.equalsIgnoreCase(emi.getStatus(), "PAYMENT_DUE"))
                        continue;
                    emiDue = emiDue + emi.getDueAmount();
                    amountDue = amountDue + emi.getDueAmount();
                    long months = ChronoUnit.MONTHS.between(emi.getCreatedOn(), LocalDateTime.now());
                    boolean isInLast3Months = months <= 3;
                    pendingCustomerCollectionResponse.setDpd(emi.getDpd());
                    if (emi.getDpd() > 30)
                        categoryFour = true;
                    if (emi.getDpd() < 30 && emi.getDpd() > 0)
                        categoryThree = true;
                    if (emi.getDpd() > 0 && isInLast3Months)
                        categoryTwo = true;
                    if (emi.getDpd() == 0 && isInLast3Months)
                        categoryOne = true;
                }
            }
            pendingCustomerCollectionResponse.setDueAmount(emiDue);
            if (categoryFour) {
                customerCategoryFour.add(pendingCustomerCollectionResponse);
                continue;
            }
            if (categoryThree) {
                customerCategoryThree.add(pendingCustomerCollectionResponse);
                continue;
            }
            if (categoryTwo) {
                customerCategoryTwo.add(pendingCustomerCollectionResponse);
                continue;
            }
            if (categoryOne) {
                customerCategoryOne.add(pendingCustomerCollectionResponse);
                continue;
            }
        }
        pendingCollectionResponse.setAmountsDue(amountDue);
        pendingCustomerCollectionResponses.addAll(customerCategoryOne);
        pendingCustomerCollectionResponses.addAll(customerCategoryTwo);
        pendingCustomerCollectionResponses.addAll(customerCategoryThree);
        pendingCustomerCollectionResponses.addAll(customerCategoryFour);
        List<PendingCustomerCollectionResponse> sortedPendingCollections = new ArrayList<>();
        Comparator<PendingCustomerCollectionResponse> byScoreAce = Comparator.comparingInt(PendingCustomerCollectionResponse::getDpd);
        sortedPendingCollections = pendingCustomerCollectionResponses.stream().sorted(byScoreAce).toList();
        if (StringUtils.equalsIgnoreCase(pendingCollectionRequest.getSortOrder(), CollectionSortOptions.DPD_HIGHEST_TO_LOWEST.name())) {
            Comparator<PendingCustomerCollectionResponse> byDpdDesc = Comparator.comparingInt(PendingCustomerCollectionResponse::getDpd).reversed();
            sortedPendingCollections = pendingCustomerCollectionResponses.stream().sorted(byDpdDesc).toList();
        }
        if (StringUtils.equalsIgnoreCase(pendingCollectionRequest.getSortOrder(), CollectionSortOptions.DPD_LOWEST_TO_HIGHEST.name())) {
            Comparator<PendingCustomerCollectionResponse> byDpdAsc = Comparator.comparingInt(PendingCustomerCollectionResponse::getDpd);
            sortedPendingCollections = pendingCustomerCollectionResponses.stream().sorted(byDpdAsc).toList();
        }
        if (StringUtils.equalsIgnoreCase(pendingCollectionRequest.getSortOrder(), CollectionSortOptions.AMOUNT_HIGHEST_TO_LOWEST.name())) {
            Comparator<PendingCustomerCollectionResponse> byAmountDesc = Comparator.comparingDouble(PendingCustomerCollectionResponse::getDueAmount).reversed();
            sortedPendingCollections = pendingCustomerCollectionResponses.stream().sorted(byAmountDesc).toList();
        }
        if (StringUtils.equalsIgnoreCase(pendingCollectionRequest.getSortOrder(), CollectionSortOptions.AMOUNT_LOWEST_TO_HIGHEST.name())) {
            Comparator<PendingCustomerCollectionResponse> byAmountAce = Comparator.comparingDouble(PendingCustomerCollectionResponse::getDueAmount);
            sortedPendingCollections = pendingCustomerCollectionResponses.stream().sorted(byAmountAce).toList();
        }

        Map<String, List<PendingCustomerCollectionResponse>> pendingCollectionCustomerResponse = sortedPendingCollections.stream()
                .collect(Collectors.groupingBy(PendingCustomerCollectionResponse::getVillage));
        List<Map<String, Object>> pendingCollections = new ArrayList<>();
        for (Map.Entry<String, List<PendingCustomerCollectionResponse>> entry : pendingCollectionCustomerResponse.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("village", entry.getKey());
            map.put("data", entry.getValue());
            pendingCollections.add(map);
        }
        pendingCollectionResponse.setPendingCustomerCollectionResponses(pendingCollections);
        Long numberOfCustomers = pendingCustomerCollectionResponses.stream().map(e -> e.getId()).distinct().count();
        Long numberOfloans = pendingCustomerCollectionResponses.stream().map(e -> e.getId()).count();
        pendingCollectionResponse.setCustomersDue(Math.toIntExact(numberOfCustomers));
        pendingCollectionResponse.setLoansDue(Math.toIntExact(numberOfloans));
        return pendingCollectionResponse;
    }
}
