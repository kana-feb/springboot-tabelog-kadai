package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.service.CategoryRestaurantService;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RegularHolidayRestaurantService;
import com.example.nagoyameshi.service.RegularHolidayService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller // クラスの頭に付ける
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	
    // ==================== フィールド ====================
    //private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService; // 依存性注入用のサービス
    private final CategoryService categoryService;
    private final CategoryRestaurantService categoryRestaurantService;    
    private final RegularHolidayService regularHolidayService;
    private final RegularHolidayRestaurantService regularHolidayRestaurantService;    


    // ==================== コンストラクタ ====================
    // コンストラクタで依存性注入
    
    public AdminRestaurantController(RestaurantService restaurantService,
            CategoryService categoryService,
            CategoryRestaurantService categoryRestaurantService,
            RegularHolidayService regularHolidayService,
            RegularHolidayRestaurantService regularHolidayRestaurantService)
    {        
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
        this.categoryRestaurantService = categoryRestaurantService;
        this.regularHolidayService = regularHolidayService;
        this.regularHolidayRestaurantService = regularHolidayRestaurantService;        
    }

    // ==================== レストラン一覧ページ ====================
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
    					@PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC) Pageable pageable,
    					Model model) {
        
        // レストラン一覧をページングして格納する 変数名restaurantPage 型Page<Restaurant>    
        Page<Restaurant> restaurantPage;

        // キーワード検索があれば検索結果をページング
        if (keyword != null && !keyword.isEmpty()) {
            restaurantPage = restaurantService.findRestaurantsByNameLike(keyword, pageable);
        	//restaurantPage = restaurantService.findRestaurantsByNameLike(keyword ,pageable); // 部分一致検索
        } else {
            restaurantPage = restaurantService.findAllRestaurants(pageable);
        }

        // 結果をモデルに追加
        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);

        return "admin/restaurants/index"; // ビューのパス
    }
    
    // ==================== 店舗詳細ページ ====================
    @GetMapping("/{id}")  
    public String show(@PathVariable(name = "id") Integer id,//@PathVariable:URL内の値を取り出す
                       RedirectAttributes redirectAttributes,
                       Model model) {
        // Restaurantをidで取得（Optionalでラップ）
        Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);

        // 存在しない場合はエラーメッセージを表示して一覧ページにリダイレクト
        if (optionalRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            
            return "redirect:/admin/restaurants";
        }

        // 存在する場合は詳細画面に渡す
        Restaurant restaurant = optionalRestaurant.get();
        model.addAttribute("restaurant", restaurant);

        return "admin/restaurants/show"; // 詳細画面
    }

    // ==================== 店舗登録ページ ====================
    @GetMapping("/register")
    public String register(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();

        // 空のフォームをビューに渡す
        model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
        model.addAttribute("categories", categories);
        model.addAttribute("regularHolidays", regularHolidays);

        return "admin/restaurants/register";
    }

    // ==================== 店舗登録処理 ====================
    @PostMapping("/create")
    public String create(
            @ModelAttribute //フォーム入力（HTMLの入力値）をJavaのオブジェクトに変換して受け取る
            @Validated //入力された値のチェック（バリデーション）を自動で行う
            RestaurantRegisterForm restaurantRegisterForm,

            // BindingResult：バリデーションの結果（エラーがあったかどうか）を受け取る
            BindingResult bindingResult,
            // RedirectAttributes：登録完了メッセージなどを、画面を切り替えた後に表示させるために使う
            RedirectAttributes redirectAttributes,
            // Model：画面にデータを渡すための入れ物（HTMLに渡すデータを詰める）
            Model model
            
    ) {
    	
        // フォームから送られてきたデータを一時的に変数に取り出す
        Integer lowestPrice = restaurantRegisterForm.getLowestPrice(); // 最低価格
        Integer highestPrice = restaurantRegisterForm.getHighestPrice(); // 最高価格
        LocalTime openingTime = restaurantRegisterForm.getOpeningTime(); // 開店時間
        LocalTime closingTime = restaurantRegisterForm.getClosingTime(); // 閉店時間

        // --- 価格のチェック処理 ---
        // null でない（入力されている）かつ、最低価格が最高価格より大きい場合はエラーとする
        if (lowestPrice != null && highestPrice != null && !restaurantService.isValidPrices(lowestPrice, highestPrice)) {
                // FieldError = 入力欄ごとのエラー情報を作る
                FieldError lowestPriceError = new FieldError(bindingResult.getObjectName(), "lowestPrice", "最低価格は最高価格以下に設定してください。");
                FieldError highestPriceError = new FieldError(bindingResult.getObjectName(), "highestPrice", "最高価格は最低価格以上に設定してください。");

                // エラーを追加する（これで画面にエラーメッセージが表示される）
                bindingResult.addError(lowestPriceError);
                bindingResult.addError(highestPriceError);
        }

        // --- 営業時間のチェック処理 ---
        // null でない（入力されている）かつ、開店時間が閉店時間より遅い場合はエラーとする
        if (openingTime != null && closingTime != null && !restaurantService.isValidBusinessHours(openingTime, closingTime)) {
                FieldError openingTimeError = new FieldError(bindingResult.getObjectName(), "openingTime", "開店時間は閉店時間よりも前に設定してください。");
                FieldError closingTimeError = new FieldError(bindingResult.getObjectName(), "closingTime", "閉店時間は開店時間よりも後に設定してください。");
                bindingResult.addError(openingTimeError);
                bindingResult.addError(closingTimeError);
        }

        // --- 入力にエラーがある場合 ---
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.findAllCategories();
            List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();

            // 入力した内容をフォームに戻すため、もう一度 model に詰める
            model.addAttribute("restaurantRegisterForm", restaurantRegisterForm);
            model.addAttribute("categories", categories);
            model.addAttribute("regularHolidays", regularHolidays);


            // もう一度「店舗登録フォーム」の画面を表示する
            return "admin/restaurants/register";
        }

        // --- 入力が正しい場合 ---
        // サービスクラスに処理を依頼して、新しい店舗をDBに登録する
        restaurantService.createRestaurant(restaurantRegisterForm);
        // 成功メッセージを一時的に保存（画面切り替え後に1回だけ表示される）
        redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");
        // 登録完了後、「店舗一覧画面」にリダイレクト（画面を切り替える）
        return "redirect:/admin/restaurants";
    }    

    // ==================== 店舗編集ページ ====================
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        // IDでレストラン情報を取得
        Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);

        // 存在しない場合はエラーメッセージを表示して一覧ページにリダイレクト
        if (optionalRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "レストランが存在しません。");
            return "redirect:/admin/restaurants";
        }

        // RestaurantEditForm のフィールドに合わせて初期値を設定
        Restaurant restaurant = optionalRestaurant.get();
        List<Integer> categoryIds = categoryRestaurantService.findCategoryIdsByRestaurantOrderByIdAsc(restaurant);
        List<Integer> regularHolidayIds = regularHolidayRestaurantService.findRegularHolidayIdsByRestaurant(restaurant);
        RestaurantEditForm restaurantEditForm = new RestaurantEditForm(
            restaurant.getName(),
            null,
            restaurant.getDescription(),
            restaurant.getLowestPrice(),
            restaurant.getHighestPrice(),
            restaurant.getPostalCode(),
            restaurant.getAddress(),
            restaurant.getOpeningTime(),
            restaurant.getClosingTime(),
            restaurant.getSeatingCapacity(),
            categoryIds,regularHolidayIds);
        
        List<Category> categories = categoryService.findAllCategories();
        List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("restaurantEditForm", restaurantEditForm);
        model.addAttribute("categories", categories);
        model.addAttribute("regularHolidays", regularHolidays);


        return "admin/restaurants/edit";
    }


    // ==================== 店舗更新処理 ====================
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm,
            BindingResult bindingResult,
            @PathVariable(name = "id") Integer id,
            RedirectAttributes redirectAttributes,
            Model model) {

	Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
	if (optionalRestaurant.isEmpty()) {
	redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
	return "redirect:/admin/restaurants";
	}
	
	Restaurant restaurant = optionalRestaurant.get();
	
	Integer lowestPrice = restaurantEditForm.getLowestPrice();
	Integer highestPrice = restaurantEditForm.getHighestPrice();
	LocalTime openingTime = restaurantEditForm.getOpeningTime();
	LocalTime closingTime = restaurantEditForm.getClosingTime();
	
	// --- バリデーション再チェック ---
	if (lowestPrice != null && highestPrice != null && !restaurantService.isValidPrices(lowestPrice, highestPrice)) {
	bindingResult.addError(new FieldError(bindingResult.getObjectName(), "lowestPrice", "最低価格は最高価格以下に設定してください。"));
	bindingResult.addError(new FieldError(bindingResult.getObjectName(), "highestPrice", "最高価格は最低価格以上に設定してください。"));
	}
	
	if (openingTime != null && closingTime != null && !restaurantService.isValidBusinessHours(openingTime, closingTime)) {
	bindingResult.addError(new FieldError(bindingResult.getObjectName(), "openingTime", "開店時間は閉店時間よりも前に設定してください。"));
	bindingResult.addError(new FieldError(bindingResult.getObjectName(), "closingTime", "閉店時間は開店時間よりも後に設定してください。"));
	}
	
	if (bindingResult.hasErrors()) {
        List<Category> categories = categoryService.findAllCategories();
        List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("restaurantEditForm", restaurantEditForm);
        model.addAttribute("categories", categories);
        model.addAttribute("regularHolidays", regularHolidays);

        return "admin/restaurants/edit";
	}
	
	restaurantService.updateRestaurant(restaurantEditForm, restaurant);
	redirectAttributes.addFlashAttribute("successMessage", "店舗を編集しました。");
	return "redirect:/admin/restaurants";
	}

    // ==================== 店舗削除処理 ====================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

    	// IDでレストラン情報を取得       
    	Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);

        // 存在しない場合はエラーメッセージを表示して一覧ページにリダイレクト
        if (optionalRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "レストランが存在しません。");
            
            return "redirect:/admin/restaurants";
        }
        
        Restaurant restaurant = optionalRestaurant.get();
        restaurantService.deleteRestaurant(restaurant);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
        
        return "redirect:/admin/restaurants";
    }
}
