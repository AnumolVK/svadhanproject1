package com.svadhan.collection.entity;


import com.svadhan.collection.constants.DleStatus;
import com.svadhan.collection.constants.TrustCircleMemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "trust_circle_customer_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrustCircleCustomerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;
    @Column(name = "trust_circle_id")
    Long trustCircleId;
    @Column(name = "customer_id")
    Long customerId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TrustCircleMemberStatus status;
    @Column(name = "updated_time")
    LocalDateTime updatedTime;
    @Column(name = "dle_status")
    @Enumerated(EnumType.STRING)
    DleStatus dleStatus;
}
