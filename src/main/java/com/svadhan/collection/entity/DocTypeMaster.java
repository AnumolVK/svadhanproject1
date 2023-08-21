package com.svadhan.collection.entity;

import com.svadhan.collection.constants.DocType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doc_type_master")
@Getter
@Setter
public class DocTypeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DocType type;

    @Column(name = "is_active")
    private Boolean isActive;
}