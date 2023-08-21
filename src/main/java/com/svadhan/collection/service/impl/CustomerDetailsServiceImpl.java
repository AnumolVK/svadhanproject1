package com.svadhan.collection.service.impl;

import com.svadhan.collection.constants.DocType;
import com.svadhan.collection.entity.*;
import com.svadhan.collection.exception.customexception.RequiredEntityNotFoundException;
import com.svadhan.collection.model.response.ContinuingGuarantorDTO;
import com.svadhan.collection.model.response.CustomerProfileResponse;
import com.svadhan.collection.model.response.HouseLocationDTO;
import com.svadhan.collection.repository.*;
import com.svadhan.collection.service.CustomerDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDetailsServiceImpl implements CustomerDetailsService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ContinuingGuarantorRepository continuingGuarantorRepository;
    @Autowired
    private VillagePinCodeRepository villagePinCodeRepository;
    @Autowired
    private CustomerOcrDataRepository customerOcrDataRepository;
    private final CustomerDocRepository customerDocRepository;
    private final DocTypeMasterRepository docTypeMasterRepository;

    @Override
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RequiredEntityNotFoundException(
                        String.format("Entity 'Customer' not found with given ID '%s'", customerId)
                ));
    }

    @Override
    public CustomerProfileResponse getCustomerProfileDetails(Long customerId) {
        CustomerProfileResponse customerProfileResponse = new CustomerProfileResponse();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RequiredEntityNotFoundException("Customer not found with ID: " + customerId));
        customerProfileResponse.setId(customerId);
        customerProfileResponse.setFullName(customer.getName());
        customerProfileResponse.setMobile(customer.getMobile());
        customerProfileResponse.setAddress(customer.getCustomerOcrData().getAddress());
        HouseLocationDTO houseLocationDTO = new HouseLocationDTO();
        houseLocationDTO.setLatitude(customer.getLatitude());
        houseLocationDTO.setLongitude(customer.getLongitude());
        customerProfileResponse.setHouseLocation(houseLocationDTO);
        customerProfileResponse.setAadharNumber(customer.getCustomerOcrData().getAadhaarNumber());
        customerProfileResponse.setVoterIdNumber(customer.getCustomerOcrData().getVoterIdNumber());
        Optional<CustomerOcrData> customerOcrData = customerOcrDataRepository.findByCustomerId(customerId);
        String customerPinCode = customerOcrData.isPresent() ? customerOcrData.get().getPinCode() : "";
//        List<VillagePinCodeList> village = villagePinCodeRepository.findByPinCode(customerPinCode);
//        customerProfileResponse.setVillage(!CollectionUtils.isEmpty(village) ? village.get(0).getVillageName() : "");
        CustomerBasicDetail customerBasicDetails = customer.getCustomerBasicDetail();
        customerProfileResponse.setVillage(customerBasicDetails != null ? customerBasicDetails.getVillage():null);
        customerProfileResponse.setPostOffice(customerBasicDetails != null ? customerBasicDetails.getPostOffice():null);
        customerProfileResponse.setPinCode(customerPinCode);

        Optional<ContinuingGuarantor> continuingGuarantor = continuingGuarantorRepository.findById(customer.getContinuingGuarantorId());
        ContinuingGuarantorDTO continuingGuarantorDTO = new ContinuingGuarantorDTO();
        if (continuingGuarantor.isPresent()) {
            continuingGuarantorDTO.setName(continuingGuarantor.get().getName());
            continuingGuarantorDTO.setRelation(continuingGuarantor.get().getRelationShip());
            continuingGuarantorDTO.setPhoneNumber(continuingGuarantor.get().getMobileNumber());
            //continuingGuarantorDTO.setVoterId();
        }
        customerProfileResponse.setContinuingGuarantorDTO(continuingGuarantorDTO);
        customerProfileResponse.setHousePhoto(this.getCustomerDocUrl(customer, DocType.HOUSE_PHOTO));
        customerProfileResponse.setPhoto(this.getCustomerDocUrl(customer, DocType.DLE_SELFIE_IMAGE));
        return customerProfileResponse;
    }

    private String getCustomerDocUrl(Customer customer, DocType docType) {

        if (customer == null) {
            log.info("Customer can not be null.");
            return null;
        }
        Optional<DocTypeMaster> docOptional = docTypeMasterRepository.findByType(docType);
        if (docOptional.isEmpty()) {
            log.info("Document type not found in the data base: " + docType);
            return null;
        }
        Optional<CustomerDoc> customerDocOptional = customerDocRepository.findByCustomerAndDocType(customer, docOptional.get());
        if (customerDocOptional.isEmpty()) {
            log.info("Document " + docType + " is missing for the customer " + customer.getId());
            return null;
        }
        return customerDocOptional.get().getDocUrl();
    }
}
