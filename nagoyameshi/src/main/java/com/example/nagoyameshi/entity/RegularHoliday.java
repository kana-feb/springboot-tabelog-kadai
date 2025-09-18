package com.example.nagoyameshi.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "regular_holidays") // テーブル名を指定
@Data
public class RegularHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // 主キー

    @Column(name = "day", nullable = false)
    private String day; // 定休日（曜日や不定休など）

    @Column(name = "day_index")
    private Integer dayIndex; // カレンダー用のインデックス（0=日曜, 1=月曜...）
    
    // 店舗とのリレーション

    @OneToMany(mappedBy = "regularHoliday", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<RegularHolidayRestaurant> regularHolidaysRestaurants;
}

