package com.example.nagoyameshi.controller;

import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	   private final UserService userService;

	   public UserController(UserService userService) {
	       this.userService = userService;
	   }
	   
	    //index()メソッド 会員用の会員情報ページ（user/index.htmlファイル）を表示する
	    @GetMapping        
	    //@RequestParam：フォームから送られたキーワードを受け取る
	    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        
	        //ログイン中の会員情報を取り出している
            User user = userDetailsImpl.getUser();//optional<User>型を変換してからビューに渡す
	        //ビューに渡す画面に渡すデータを model に詰める。キー名は "user"、中身はさっき取得した user。
	        model.addAttribute("user", user);        
	        
	        return "user/index";
	    }
	    
	   //edit()メソッド　会員用の会員情報編集ページ（user/edit.htmlファイル）を表示する。
	    @GetMapping("/edit")
	    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
	        User user = userDetailsImpl.getUser(); //ログインユーザーの User を取得
	        String birthday = null;

	        //フォームは文字列を扱うので、LocalDate を一度フォーマットして文字列に変換している
	        if (user.getBirthday() != null) {
	            birthday = user.getBirthday().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	        }

	        UserEditForm userEditForm = new UserEditForm(user.getName(),
	                                                     user.getFurigana(),
	                                                     user.getPostalCode(),
	                                                     user.getAddress(),
	                                                     user.getPhoneNumber(),
	                                                     birthday,
	                                                     user.getOccupation(),
	                                                     user.getEmail());
	        model.addAttribute("userEditForm", userEditForm);

	        return "user/edit";
	    }
	    //update()メソッド フォームから送信された会員情報でデータベースを更新する。
	    @PostMapping("/update")
	    public String update(@ModelAttribute @Validated UserEditForm userEditForm,
	                         BindingResult bindingResult,
	                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                         RedirectAttributes redirectAttributes,
	                         Model model)
	    {
	        User user = userDetailsImpl.getUser();

	        // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
	        if (userService.isEmailChanged(userEditForm, user) && userService.isEmailRegistered(userEditForm.getEmail())) {
	            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
	            bindingResult.addError(fieldError);
	        }

	        if (bindingResult.hasErrors()) {
	            model.addAttribute("userEditForm", userEditForm);

	            return "user/edit";
	        }

	        userService.updateUser(userEditForm, user);
	        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

	        return "redirect:/user";
	    }

}
	 
