package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.model.response.CustomerTrustCircleResponse;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.TrustCircleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrustCircleServiceImpl implements TrustCircleService {

    private final TrustCircleRepository trustCircleRepository;
    private final TrustCircleCustomerMappingRepository trustCircleCustomerMappingRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOcrDataRepository customerOcrDataRepository;
    private final VillagePinCodeRepository villagePinCodeRepository;
    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;

    public TrustCircleServiceImpl(TrustCircleRepository trustCircleRepository, TrustCircleCustomerMappingRepository trustCircleCustomerMappingRepository, CustomerRepository customerRepository, CustomerOcrDataRepository customerOcrDataRepository, VillagePinCodeRepository villagePinCodeRepository, LoanRepository loanRepository, EmiRepository emiRepository) {
        this.trustCircleRepository = trustCircleRepository;
        this.trustCircleCustomerMappingRepository = trustCircleCustomerMappingRepository;
        this.customerRepository = customerRepository;
        this.customerOcrDataRepository = customerOcrDataRepository;
        this.villagePinCodeRepository = villagePinCodeRepository;
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
    }

    @Override
    public List<CustomerTrustCircleResponse> getTrustCircleDetails(Long customerId) {
        List<CustomerTrustCircleResponse> customerTrustCircleResponses = new ArrayList<>();
        Optional<TrustCircle> trustCircle = trustCircleRepository.findByCreatedFor(customerId);
        if (trustCircle.isPresent()) {
            List<TrustCircleCustomerMapping> trustCircleCustomerMappings = trustCircleCustomerMappingRepository.findAllByTrustCircleId(trustCircle.get().getId());
            customerTrustCircleResponses = trustCircleCustomerMappings.stream().map(tr -> {
                CustomerTrustCircleResponse customerTrustCircleResponse = new CustomerTrustCircleResponse();
                Optional<Customer> customers = customerRepository.findById(tr.getCustomerId());
                if (customers.isPresent()) {
                    Customer customer = customers.get();
                    if (customer.getCustomerOcrData() != null) {
                        customerTrustCircleResponse.setPinCode(customer.getCustomerOcrData().getPinCode());
                    } else {
                        log.info("Customer OCR data is missing for the customer: " + customer.getId());
                    }
                    customerTrustCircleResponse.setName(customers.get().getName());
                    customerTrustCircleResponse.setPhoneNumber(customers.get().getMobile());
                    customerTrustCircleResponse.setCustomerId(customer.getId());
                    Optional<CustomerOcrData> customerOcrData = customerOcrDataRepository.findByCustomerId(customerId);
                    String customerPinCode = customerOcrData.isPresent() ? customerOcrData.get().getPinCode() : "";
//                    List<VillagePinCodeList> villagePinCodeList = villagePinCodeRepository.findByPinCode(customerPinCode);
//                    customerTrustCircleResponse.setVillage(!CollectionUtils.isEmpty(villagePinCodeList) ? villagePinCodeList.get(0).getVillageName() : "");
                    customerTrustCircleResponse.setVillage(customer.getCustomerBasicDetail() != null ? customer.getCustomerBasicDetail().getVillage():null);
                    List<Loan> loans = loanRepository.findAllByCustomer(customers.get());
                    double emiDue = 0.0;
                    boolean isDueDatePassed = false;
                    for (Loan loan : loans) {
                        List<Emi> emis = emiRepository.findAllByLoanIdOrderByCreatedOnDesc(loan.getId());
                        for (Emi emi : emis) {
                            emiDue = emiDue + emi.getDueAmount();
                            if (emi.getDueDate().isBefore(LocalDate.now())) {
                                isDueDatePassed = true;
                            }
                        }
                    }
                    customerTrustCircleResponse.setDueDatePassed(isDueDatePassed);
                    customerTrustCircleResponse.setPaymentDue(emiDue);
                }
                return customerTrustCircleResponse;
            }).toList();
        }

        /*Removing current customerId from customerTrustCircleResponses list.*/
        customerTrustCircleResponses=customerTrustCircleResponses.stream().filter(e -> !e.getCustomerId().equals(customerId)).collect(Collectors.toList());

        /*Getting list of active(customer) TC Members of current customers*/
        List<Long> activeCustomerIdList=trustCircleRepository.getActiveTcMembersOfCustomer(customerId);

        /*In response, collecting only active TC Members.*/
        customerTrustCircleResponses=customerTrustCircleResponses.stream().filter(e -> activeCustomerIdList.contains(e.getCustomerId())).collect(Collectors.toList());

        return customerTrustCircleResponses;
    }
}
