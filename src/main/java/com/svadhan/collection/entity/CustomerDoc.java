package com.svadhan.collection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_doc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "customer_doc_generator", sequenceName = "customer_doc_seq", allocationSize = 1)
public class CustomerDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_doc_generator")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doc_type_id", nullable = false)
    private DocTypeMaster docType;

    @Column(name = "doc_url")
    private String docUrl;

    @Column(name = "uploaded_on")
    @CreationTimestamp
    private LocalDateTime uploadedOn;

    @Column(name = "modified_on")
    @UpdateTimestamp
    private LocalDateTime modifiedOn;

    @Column(name = "is_active")
    private Boolean isActive;
}