package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.service.CompanyService;

@Controller
@RequestMapping("/company")
// ==================== フィールド ====================
public class CompanyController {
   private final CompanyService companyService;
   
// // ==================== コンストラクタ ====================
//コンストラクタで依存性注入
   public CompanyController(CompanyService companyService) {
       this.companyService = companyService;
   }

   @GetMapping
   public String index(Model model) {
       Company company = companyService.findFirstCompanyByOrderByIdDesc();

       model.addAttribute("company", company);

       return "company/index";
   }
}