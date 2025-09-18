package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.service.CompanyService;

@Controller
//このクラス内のメソッドは /admin/company/... から始まるURLで呼び出される
@RequestMapping("/admin/company")
public class AdminCompanyController {
	//依存性の注入（DI）
    private final CompanyService companyService;

    public AdminCompanyController(CompanyService companyService) {
       this.companyService = companyService;
   }

   //GET /admin/company でアクセスされた時にこのメソッドが呼ばれる
   @GetMapping
   //Modelインターフェイスを引数に指定する
   public String index(Model model) {
       Company company = companyService.findFirstCompanyByOrderByIdDesc();
       //画面に company という名前でデータを渡す
       model.addAttribute("company", company);
       //templates/admin/company/index.html を表示する
       return "admin/company/index";
   }
   
   //GET /admin/edit でアクセスされた時にこのメソッドが呼ばれる
   @GetMapping("/edit")
   public String edit(Model model) {
       Company company = companyService.findFirstCompanyByOrderByIdDesc();
       //フォームクラスをエンティティから変換して生成。「画面の入力フォームに初期値を表示するため」に使う
       CompanyEditForm companyEditForm = new CompanyEditForm(company.getName(),
                                                             company.getPostalCode(),
                                                             company.getAddress(),
                                                             company.getRepresentative(),
                                                             company.getEstablishmentDate(),
                                                             company.getCapital(),
                                                             company.getBusiness(),
                                                             company.getNumberOfEmployees());
       //画面側にフォームデータを渡す
       model.addAttribute("companyEditForm", companyEditForm);

       return "admin/company/edit";
   }

   //フォームから送信された利用規約の内容でデータベースを更新する
   @PostMapping("/update")
   public String update(@ModelAttribute @Validated CompanyEditForm companyEditForm,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model)
   {
	   //バリデーションエラーチェック
       if (bindingResult.hasErrors()) {
           model.addAttribute("companyEditForm", companyEditForm);

           return "admin/company/edit";
       }
       //更新処理
       Company company = companyService.findFirstCompanyByOrderByIdDesc();
       companyService.updateCompany(companyEditForm, company);
       redirectAttributes.addFlashAttribute("successMessage", "会社概要を編集しました。");

       return "redirect:/admin/company";
   }
}