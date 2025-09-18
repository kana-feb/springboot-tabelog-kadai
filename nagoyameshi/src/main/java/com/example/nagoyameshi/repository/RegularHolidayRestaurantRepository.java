package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.RegularHolidayRestaurant;
import com.example.nagoyameshi.entity.Restaurant;

public interface RegularHolidayRestaurantRepository extends JpaRepository<RegularHolidayRestaurant, Integer> {

    // 1. 指定した店舗に紐づく定休日のID一覧を取得
    @Query("SELECT rhr.regularHoliday.id FROM RegularHolidayRestaurant rhr WHERE rhr.restaurant = :restaurant ORDER BY rhr.id ASC")
    public List<Integer> findRegularHolidayIdsByRestaurant(@Param("restaurant") Restaurant restaurant);
    
    // 2. 指定した店舗と定休日に紐づくレコードを1件取得
    public Optional<RegularHolidayRestaurant> findByRegularHolidayAndRestaurant(RegularHoliday regularHoliday, Restaurant restaurant);

    // 3. 指定した店舗に紐づくレコードをすべて取得
    public List<RegularHolidayRestaurant> findByRestaurant(Restaurant restaurant);
}
