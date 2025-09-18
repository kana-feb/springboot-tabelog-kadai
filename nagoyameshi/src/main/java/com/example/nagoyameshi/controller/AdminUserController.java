package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.UserService;


@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
   private final UserService userService;

   public AdminUserController(UserService userService) {
       this.userService = userService;
   }

    //index()メソッド
    @GetMapping        
    //@RequestParam：フォームから送られたキーワードを受け取る
    public String index(@RequestParam(name = "keyword", required = false) String keyword, 
    	//デフォルトの設定 
    	//ページ番号：0 サイズ（1ページあたりの表示数）：15 並べ替える対象：id 	並べ替える順番：Direction.ASC    		
        @PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC) Pageable pageable,Model model) 
                        
  {
        Page<User> userPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userService.findUsersByNameLikeOrFuriganaLikeOrEmailLike(keyword, keyword, keyword, pageable);
        } else {
            userPage = userService.findAllUsers(pageable);
        }  
                
        model.addAttribute("userPage", userPage);   
        model.addAttribute("keyword", keyword);
        
        return "admin/users/index";
    }
    
    // show()メソッド 会員詳細ページを表示する
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
    	//受け取った id でユーザーを取得する
        Optional<User> optionalUser  = userService.findUserById(id);
        
        //ユーザーが存在しない場合の処理
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
            return "redirect:/admin/users";
        }
        //ユーザーが存在する場合の処理
        User user = optionalUser.get();//optional<User>型を変換してからビューに渡す
        //ビューに渡す画面に渡すデータを model に詰める。キー名は "user"、中身はさっき取得した user。
        model.addAttribute("user", user);        
        
        return "admin/users/show";
    }

}

    

