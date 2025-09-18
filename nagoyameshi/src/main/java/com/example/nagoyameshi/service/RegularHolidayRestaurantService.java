package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.RegularHolidayRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RegularHolidayRestaurantRepository;

@Service
public class RegularHolidayRestaurantService {
// ==================== フィールド ====================
	private final RegularHolidayRestaurantRepository regularHolidayRestaurantRepository;
	private final RegularHolidayService regularHolidayService;

//==================== コンストラクタ ====================
	   
//コンストラクタインジェクション（依存性の注入）
   public RegularHolidayRestaurantService(RegularHolidayRestaurantRepository regularHolidayRestaurantRepository, RegularHolidayService regularHolidayService) {
       this.regularHolidayRestaurantRepository = regularHolidayRestaurantRepository;
       this.regularHolidayService = regularHolidayService;
   }

//==================== メソッド ====================
   // 指定した店舗の定休日のid（RegularHolidayエンティティのid）をリスト形式で取得する。
   public List<Integer> findRegularHolidayIdsByRestaurant(Restaurant restaurant) {
       return regularHolidayRestaurantRepository.findRegularHolidayIdsByRestaurant(restaurant);
   
   }
   
   // 店舗登録時に定休日を登録
   @Transactional
   public void createRegularHolidaysRestaurants(List<Integer> regularHolidayIds, Restaurant restaurant) {
       for (Integer regularHolidayId : regularHolidayIds) {
           if (regularHolidayId != null) {
               Optional<RegularHoliday> optionalRegularHoliday = regularHolidayService.findRegularHolidayById(regularHolidayId);

               // idに一致する定休日を取得
               if (optionalRegularHoliday.isPresent()) {
                   RegularHoliday regularHoliday = optionalRegularHoliday.get();

                   Optional<RegularHolidayRestaurant> optionalCurrentRegularHolidayRestaurant = regularHolidayRestaurantRepository.findByRegularHolidayAndRestaurant(regularHoliday, restaurant);

                   // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                   if (optionalCurrentRegularHolidayRestaurant.isEmpty()) {
                       RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
                       regularHolidayRestaurant.setRestaurant(restaurant);
                       regularHolidayRestaurant.setRegularHoliday(regularHoliday);

                       regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
                   }
               }
           }
       }
   }
   //店舗情報更新時に定休日を更新
   @Transactional
   public void syncRegularHolidaysRestaurants(List<Integer> newRegularHolidayIds, Restaurant restaurant) {
       List<RegularHolidayRestaurant> currentRegularHolidaysRestaurants = regularHolidayRestaurantRepository.findByRestaurant(restaurant);

       if (newRegularHolidayIds == null) {
           // newRegularHolidayIdsがnullの場合はすべてのエンティティを削除する
           for (RegularHolidayRestaurant currentRegularHolidayRestaurant : currentRegularHolidaysRestaurants) {
               regularHolidayRestaurantRepository.delete(currentRegularHolidayRestaurant);
           }
       } else {
           // 既存のエンティティが新しいリストに存在しない場合は削除する
           for (RegularHolidayRestaurant currentRegularHolidayRestaurant : currentRegularHolidaysRestaurants) {
               if (!newRegularHolidayIds.contains(currentRegularHolidayRestaurant.getRegularHoliday().getId())) {
                   regularHolidayRestaurantRepository.delete(currentRegularHolidayRestaurant);
               }
           }

           for (Integer newRegularHolidayId : newRegularHolidayIds) {
               if (newRegularHolidayId != null) {
                   Optional<RegularHoliday> optionalRegularHoliday = regularHolidayService.findRegularHolidayById(newRegularHolidayId);

                   if (optionalRegularHoliday.isPresent()) {
                       RegularHoliday regularHoliday = optionalRegularHoliday.get();

                       Optional<RegularHolidayRestaurant> optionalCurrentRegularHolidayRestaurant = regularHolidayRestaurantRepository.findByRegularHolidayAndRestaurant(regularHoliday, restaurant);

                       // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                       if (optionalCurrentRegularHolidayRestaurant.isEmpty()) {
                           RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
                           regularHolidayRestaurant.setRestaurant(restaurant);
                           regularHolidayRestaurant.setRegularHoliday(regularHoliday);

                           regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
                       }
                   }
               }
           }
       }
   }
}