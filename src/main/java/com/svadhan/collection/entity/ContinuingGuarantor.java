package com.svadhan.collection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "continuing_guarantor")
@SequenceGenerator(name = "continuing_guarantor_generator", sequenceName = "continuing_guarantor_seq", allocationSize = 1)
public class ContinuingGuarantor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "continuing_guarantor_generator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "village")
    private String village;

    @Column(name = "pin")
    private String pin;

    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "voter_id")
    private String voterId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "joining_date_of_customer")
    private LocalDateTime joiningDateOfCustomer;

    @Column(name = "continuing_guarantor_death_indicator_support_id")
    private Long continuingGuarantorDeathIndicatorSupportId;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "relation_ship")
    private String relationShip;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

}