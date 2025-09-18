package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;

	public interface CategoryRepository extends JpaRepository<Category, Integer> {

		    // 部分一致検索（大文字小文字無視＋ページング対応）
		    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

		    // id が最大のカテゴリを1件取得
		    Category findFirstByOrderByIdDesc();

		    // 指定したカテゴリ名を持つ最初のカテゴリを取得するメソッド
		    Category findFirstByName(String name);
		}

	