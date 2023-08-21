package com.svadhan.collection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "trust_circle")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrustCircle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;
    @Column(name = "created_date")
    LocalDateTime createdDate;
    @Column(name = "created_for")
    Long createdFor;
    @Column(name = "updated_date")
    LocalDateTime updatedDate;
    @Column(name = "updated_for")
    Long updatedFor;
}
