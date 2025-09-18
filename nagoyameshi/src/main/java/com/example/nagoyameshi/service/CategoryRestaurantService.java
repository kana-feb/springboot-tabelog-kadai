package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRestaurantRepository;

@Service // このクラスが「サービス層」のクラスであることをSpringに知らせる
public class CategoryRestaurantService {

	// ==================== フィールド ====================
   private final CategoryRestaurantRepository categoryRestaurantRepository; // 中間テーブルを操作するリポジトリ
private final CategoryService categoryService; // Category関連のサービスを利用するための依存性

   // ==================== コンストラクタ ====================
   // コンストラクタインジェクション（依存性の注入）
   public CategoryRestaurantService(CategoryRestaurantRepository categoryRestaurantRepository, CategoryService categoryService) {
       this.categoryRestaurantRepository = categoryRestaurantRepository;
       this.categoryService = categoryService;
   }

   // ==================== メソッド ====================

   // 指定した店舗に紐づくカテゴリIDの一覧を、id昇順で取得する
   public List<Integer> findCategoryIdsByRestaurantOrderByIdAsc(Restaurant restaurant) {
       return categoryRestaurantRepository.findCategoryIdsByRestaurantOrderByIdAsc(restaurant);
   }

   
   // 店舗とカテゴリの関連データをDBに登録する処理（店舗登録時に使う）
   @Transactional 
   public void createCategoriesRestaurants(List<Integer> categoryIds, Restaurant restaurant) {
       // 渡されたカテゴリIDごとに処理
       for (Integer categoryId : categoryIds) {
           if (categoryId != null) { // nullチェック
               Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId); // idからCategoryを取得

               if (optionalCategory.isPresent()) { // Categoryが存在する場合のみ処理
                   Category category = optionalCategory.get();

                   // すでにそのカテゴリと店舗の関連が存在するか確認
                   Optional<CategoryRestaurant> optionalCurrentCategoryRestaurant = categoryRestaurantRepository.findByCategoryAndRestaurant(category, restaurant);

                   // カテゴリ-店舗の組み合わせが存在しない場合のみ新規作成する
                   if (optionalCurrentCategoryRestaurant.isEmpty()) {
                       CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
                       categoryRestaurant.setRestaurant(restaurant); // 店舗をセット
                       categoryRestaurant.setCategory(category);     // カテゴリをセット

                       categoryRestaurantRepository.save(categoryRestaurant); // DBに保存
                   }
               }
           }
       }
   }

   
   // 店舗とカテゴリの関連データを最新の状態に同期する処理（店舗更新時に使う）
   @Transactional
   public void syncCategoriesRestaurants(List<Integer> newCategoryIds, Restaurant restaurant) {
       // 現在DBに登録されているカテゴリ関連の一覧を取得
       List<CategoryRestaurant> currentCategoriesRestaurants = categoryRestaurantRepository.findByRestaurantOrderByIdAsc(restaurant);

       if (newCategoryIds == null) {
           // もし新しいカテゴリリストがnullなら、全て削除
           for (CategoryRestaurant currentCategoryRestaurant : currentCategoriesRestaurants) {
               categoryRestaurantRepository.delete(currentCategoryRestaurant);
           }
       } else {
           // 既存データのうち、新しいリストに含まれていないものを削除
           for (CategoryRestaurant currentCategoryRestaurant : currentCategoriesRestaurants) {
               if (!newCategoryIds.contains(currentCategoryRestaurant.getCategory().getId())) {
                   categoryRestaurantRepository.delete(currentCategoryRestaurant);
               }
           }

           // 新しいリストに含まれるカテゴリを確認し、まだ存在しなければ追加
           for (Integer newCategoryId : newCategoryIds) {
               if (newCategoryId != null) {
                   Optional<Category> optionalCategory = categoryService.findCategoryById(newCategoryId);

                   if (optionalCategory.isPresent()) {
                       Category category = optionalCategory.get();

                       // 店舗とカテゴリの組み合わせがまだ存在しなければ作成
                       Optional<CategoryRestaurant> optionalCurrentCategoryRestaurant = categoryRestaurantRepository.findByCategoryAndRestaurant(category, restaurant);

                       if (optionalCurrentCategoryRestaurant.isEmpty()) {
                           CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
                           categoryRestaurant.setRestaurant(restaurant);
                           categoryRestaurant.setCategory(category);

                           categoryRestaurantRepository.save(categoryRestaurant);
                       }
                   }
               }
           }
       }
   }
}
