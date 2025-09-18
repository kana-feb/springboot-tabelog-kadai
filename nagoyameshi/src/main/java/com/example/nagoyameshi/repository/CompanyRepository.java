package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Company;

public interface CompanyRepository  extends JpaRepository<Company, Integer> {

	    // id が最大の店舗を1件取得
	    public Company findFirstByOrderByIdDesc();
	}

