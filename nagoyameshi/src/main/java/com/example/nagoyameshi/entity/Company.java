package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "companies") // companiesテーブルと対応
@Data // getter/setter自動生成（Lombok）
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "representative", nullable = false)
    private String representative;

    @Column(name = "establishment_date", nullable = false)
    private String establishmentDate;

    @Column(name = "capital", nullable = false)
    private String capital;

    @Column(name = "business", nullable = false)
    private String business;

    @Column(name = "number_of_employees", nullable = false)
    private String numberOfEmployees;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Timestamp updatedAt;
}
