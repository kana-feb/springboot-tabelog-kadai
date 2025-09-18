package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.Restaurant;


public interface CategoryRestaurantRepository extends JpaRepository<CategoryRestaurant, Integer> {
	  //特定の Restaurant に紐づく Category のIDだけをリストで取得　小さい順で並べる
	  @Query("SELECT cr.category.id FROM CategoryRestaurant cr WHERE cr.restaurant = :restaurant ORDER BY cr.id ASC")
	   public List<Integer> findCategoryIdsByRestaurantOrderByIdAsc(@Param("restaurant") Restaurant restaurant);
	  //指定した Category と Restaurant の組み合わせに対応する CategoryRestaurant を1件探す。
	   public Optional<CategoryRestaurant> findByCategoryAndRestaurant(Category category, Restaurant restaurant);
	  //指定した Restaurant に紐づくすべての CategoryRestaurant を中間テーブルのID順で取得
	   public List<CategoryRestaurant> findByRestaurantOrderByIdAsc(Restaurant restaurant);
}
