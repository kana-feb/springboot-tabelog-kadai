package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "regular_holiday_restaurant") // 中間テーブル名を指定
@Data

public class RegularHolidayRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // 主キー

    // 店舗とのリレーション（多対1）
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // 定休日とのリレーション（多対1）
    @ManyToOne
    @JoinColumn(name = "regular_holiday_id", nullable = false)
    private RegularHoliday regularHoliday;

    // 作成日時（DBが自動で設定）
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Timestamp createdAt;

    // 更新日時（DBが自動で更新）
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Timestamp updatedAt;
}
