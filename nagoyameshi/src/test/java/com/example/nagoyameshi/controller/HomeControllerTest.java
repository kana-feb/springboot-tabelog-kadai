package com.example.nagoyameshi.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RestaurantService;

@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
public class HomeControllerTest {
   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private RestaurantService restaurantService;

   @MockBean
   private CategoryService categoryService;

   @BeforeEach
   public void setup() {
       Page<Restaurant> emptyPage = Page.empty();
       when(restaurantService.findAllRestaurants(any(Pageable.class))).thenReturn(emptyPage);
       when(restaurantService.findAllRestaurantsByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(emptyPage);
       when(categoryService.findFirstCategoryByName(anyString())).thenReturn(new Category());
       when(categoryService.findAllCategories()).thenReturn(List.of());
   }

   @Test
   public void 未ログインの場合は会員用のトップページが正しく表示される() throws Exception {
       mockMvc.perform(get("/"))
              .andExpect(status().isOk())
              .andExpect(view().name("index"));
   }

   @Test
   @WithMockUser(username = "taro.samurai@example.com")
   public void 一般ユーザーとしてログイン済みの場合は会員用のトップページが正しく表示される() throws Exception {
       mockMvc.perform(get("/"))
              .andExpect(status().isOk())
              .andExpect(view().name("index"));
   }
}