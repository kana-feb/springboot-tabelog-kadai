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
@Table(name = "categories") // テーブル名
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // 主キー

    @Column(name = "name", nullable = false)//NULLを禁止する
    private String name; // カテゴリ名
    
    //エンティティではアノテーションを付けることでリレーションを表現
    //mappedBy:カテゴリ（Category）エンティティが、CategoryRestaurant（中間テーブル）との関係を持っていることを表しています。
    //FetchType.EAGER :関連エンティティを即座にロードします
    
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<CategoryRestaurant> categoriesRestaurants; //List<相手のエンティティのクラス型>型でフィールドを定義
  
}