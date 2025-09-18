package com.example.nagoyameshi.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.repository.CompanyRepository;

@Service
public class CompanyService {
	private final CompanyRepository companyRepository;
	
    // 依存性の注入（DI）を行う（コンストラクタインジェクション）
    public CompanyService(CompanyRepository companyRepository) {
       this.companyRepository = companyRepository;
    }
    
    //最新の会社概要を取得
    public Company findFirstCompanyByOrderByIdDesc() {
    	return companyRepository.findFirstByOrderByIdDesc();
    }
    //フォームから送信された会社概要の内容でデータベースを更新する。

    
    @Transactional
    public void updateCompany(CompanyEditForm companyEditForm, Company company) {
        company.setName(companyEditForm.getName());
        company.setPostalCode(companyEditForm.getPostalCode());
        company.setAddress(companyEditForm.getAddress());
        company.setRepresentative(companyEditForm.getRepresentative());
        company.setEstablishmentDate(companyEditForm.getEstablishmentDate());
        company.setCapital(companyEditForm.getCapital());
        company.setBusiness(companyEditForm.getBusiness());
        company.setNumberOfEmployees(companyEditForm.getNumberOfEmployees());

        companyRepository.save(company);
    }
}

