package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RestaurantService;



@Controller
public class HomeController {
// ==================== フィールド ====================
private final RestaurantService  restaurantService ; // 依存性注入用のサービス
private final CategoryService categoryService; // 依存性注入用のサービス

// ==================== コンストラクタ ====================
// コンストラクタで依存性注入
public HomeController(RestaurantService restaurantService, CategoryService categoryService) {
    this.restaurantService = restaurantService;
    this.categoryService = categoryService;
}

@GetMapping("/")
public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    if (userDetailsImpl != null && userDetailsImpl.getUser().getRole().getName().equals("ROLE_ADMIN")) {
        return "redirect:/admin";
    }  	
    // Page<Restaurant> は「ページングされた Restaurant のリスト」を表す
    //評価が高い順番に6件取得
    Page<Restaurant> highlyRatedRestaurants = restaurantService.findAllRestaurantsByOrderByAverageScoreDesc(PageRequest.of(0, 6));

    // 新しい順に並べた店舗を、最初の6件だけ取得　★作成日時が新しい順に並べられた6件の店舗
    Page<Restaurant> newRestaurants = restaurantService.findAllRestaurantsByOrderByCreatedAtDesc(PageRequest.of(0, 6));

    // 各カテゴリを名前で検索して1件取得
    Category washoku = categoryService.findFirstCategoryByName("和食");
    Category udon = categoryService.findFirstCategoryByName("うどん");
    Category don = categoryService.findFirstCategoryByName("丼物");
    Category ramen = categoryService.findFirstCategoryByName("ラーメン");
    Category oden = categoryService.findFirstCategoryByName("おでん");
    Category fried = categoryService.findFirstCategoryByName("揚げ物");

    // 全てのカテゴリを取得してリストに入れる
    List<Category> categories = categoryService.findAllCategories();

    // 取得したデータを「model」にセットして、画面（ビュー）で使えるようにする
    model.addAttribute("highlyRatedRestaurants", highlyRatedRestaurants); // 高評価の店舗
    model.addAttribute("newRestaurants", newRestaurants);                 // 新しい店舗
    model.addAttribute("washoku", washoku);                               // 和食カテゴリ
    model.addAttribute("udon", udon);                                     // うどんカテゴリ
    model.addAttribute("don", don);                                       // 丼物カテゴリ
    model.addAttribute("ramen", ramen);                                   // ラーメンカテゴリ
    model.addAttribute("oden", oden);                                     // おでんカテゴリ
    model.addAttribute("fried", fried);                                   // 揚げ物カテゴリ
    model.addAttribute("categories", categories);                         // 全カテゴリ

    // 最後に「index.html」を表示する
    return "index";
   }
}