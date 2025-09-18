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
@Table(name = "category_restaurant") // テーブル名 中間テーブルのエンティティ
@Data

public class CategoryRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // 主キー

    @ManyToOne // 多対1（複数の中間データが1つの店舗を持つ）
    @JoinColumn(name = "restaurant_id", nullable = false) 
    private Restaurant restaurant;  // Restaurantエンティティとつながる

    @ManyToOne // 多対1（複数の中間データが1つのカテゴリを持つ）
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // Categoryエンティティとつながる
    
    // 作成日時（DBが自動で設定）
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    // 更新日時（DBが自動で更新）
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
}