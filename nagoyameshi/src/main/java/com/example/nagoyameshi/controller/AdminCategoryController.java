package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.service.CategoryService;


@Controller // クラスの頭に付ける
@RequestMapping("/admin/categories")
public class AdminCategoryController {

	// ==================== フィールド ====================
    //private final CategoryRepository categoryRepository;
    private final CategoryService categoryService; // 依存性注入用のサービス

    // ==================== コンストラクタ ====================
    // コンストラクタで依存性注入
    public AdminCategoryController(//CategoryRepository categoryRepository,
                                     CategoryService categoryService) {
       // this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    // ==================== カテゴリ一覧ページ ====================
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
    					@PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC) Pageable pageable,
    					Model model) 
    {        
        // カテゴリ一覧をページングして格納する 変数名categoryPage 型Page<category>    
        Page<Category> categoryPage;

        // キーワード検索があれば検索結果をページング
        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = categoryService.findCategoriesByNameLike( keyword , pageable); // 部分一致検索
        } else {
            categoryPage = categoryService.findAllCategories(pageable);
        }

        // 結果をモデルに追加
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryRegisterForm", new CategoryRegisterForm()); // 登録用フォーム
        model.addAttribute("categoryEditForm", new CategoryEditForm());         // 編集用フォーム


        return "admin/categories/index"; // ビューのパス
    }

    // ==================== 詳細ページ ====================
    @GetMapping("/{id}")  
    public String show(@PathVariable(name = "id") Integer id,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        // Categoryをidで取得（Optionalでラップ）
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

        // 存在しない場合はエラーメッセージを表示して一覧ページにリダイレクト
        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            
            return "redirect:/admin/categories";
        }

        // 存在する場合は詳細画面に渡す
        Category category = optionalCategory.get();
        model.addAttribute("category", category);

        return "admin/categories/show"; // 詳細画面
    }
    
    // ==================== 登録処理 ====================
    @PostMapping("/create")
    public String create(
            @ModelAttribute //フォーム入力（HTMLの入力値）をJavaのオブジェクトに変換して受け取る
            @Validated //入力された値のチェック（バリデーション）を自動で行う
            CategoryRegisterForm categoryRegisterForm,
            // BindingResult：バリデーションの結果（エラーがあったかどうか）を受け取る
            BindingResult bindingResult,
            // RedirectAttributes：登録完了メッセージなどを、画面を切り替えた後に表示させるために使う
            RedirectAttributes redirectAttributes,
            // Model：画面にデータを渡すための入れ物（HTMLに渡すデータを詰める）
            Model model) 
    
    {  	
        // --- 入力にエラーがある場合 ---
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");
            // もう一度categories一覧の画面を表示する
            return "admin/categories";
        }

        // --- 入力が正しい場合 ---
        // サービスクラスに処理を依頼して、新しいカテゴリをDBに登録する
        categoryService.createCategory(categoryRegisterForm);
        // 成功メッセージを一時的に保存（画面切り替え後に1回だけ表示される）
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");
        // 登録完了後、「カテゴリ一覧画面」にリダイレクト（画面を切り替える）
        return "redirect:/admin/categories";
    }    
    
    // ==================== 更新処理 ====================
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm,
                         BindingResult bindingResult,
                         @PathVariable(name = "id") Integer id, // URLの {id} を受け取る
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        // idでDBを検索（Optionalは「あるかもしれない、ないかもしれない」データを扱う型）
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");
            return "redirect:/admin/categories";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");
            return "redirect:/admin/categories";
        }

        // Optional から中身を取り出す（get()）
        Category category = optionalCategory.get();
        categoryService.updateCategory(categoryEditForm, category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");

        return "redirect:/admin/categories";
    }

    // ==================== カテゴリ削除 ====================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");
            return "redirect:/admin/categories";
        }

        Category category = optionalCategory.get();
        categoryService.deleteCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");

        return "redirect:/admin/categories";
    }
 }