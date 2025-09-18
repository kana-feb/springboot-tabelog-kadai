package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Term;

public interface TermRepository extends JpaRepository<Term, Integer> {

    // id が最大の店舗を1件取得
	public Term findFirstByOrderByIdDesc();
	
}
