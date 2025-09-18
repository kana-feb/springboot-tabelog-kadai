package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;

@Service
public class CategoryService {
   private final CategoryRepository categoryRepository;
   // 依存性の注入（DI）を行う（コンストラクタインジェクション）
   public CategoryService(CategoryRepository categoryRepository) {
       this.categoryRepository = categoryRepository;
   }

   // すべてのカテゴリをページングされた状態で取得する
   public Page<Category> findAllCategories(Pageable pageable) {
       return categoryRepository.findAll(pageable);
   }

   // 指定されたキーワードをカテゴリ名に含むカテゴリを、ページングされた状態で取得する
   public Page<Category> findCategoriesByNameLike(String keyword, Pageable pageable) {
    return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
}
   
   // 指定したidを持つカテゴリを取得する
   public Optional<Category> findCategoryById(Integer id) {
       return categoryRepository.findById(id);
   }

   // カテゴリのレコード数を取得する
   public long countCategories() {
       return categoryRepository.count();
   }

   // idが最も大きいカテゴリを取得する
   public Category findFirstCategoryByOrderByIdDesc() {
       return categoryRepository.findFirstByOrderByIdDesc();
   }
   
   // すべてのカテゴリをリスト形式で取得する
   public List<Category> findAllCategories() {
       return categoryRepository.findAll();
   } 
   
   // 指定したカテゴリ名を持つ最初のカテゴリを取得する
   public Category findFirstCategoryByName(String name) {
       return categoryRepository.findFirstByName(name);
   } 
   
   // @Transactional：途中でエラーがあれば自動で処理が取り消される（DBを安全に守る仕組み）
   @Transactional
   public void createCategory(CategoryRegisterForm categoryRegisterForm) {
	// 登録用のエンティティを作成
       Category category = new Category();
       // フォーム（HTML入力値）から受け取った名前を、エンティティにセットする
       category.setName(categoryRegisterForm.getName());
       // 登録
       categoryRepository.save(category);
   }

   @Transactional
   public void updateCategory(CategoryEditForm categoryEditForm, Category category) {
       // 編集用フォームから受け取った名前を、既存のカテゴリに上書きする
       category.setName(categoryEditForm.getName());
   	// 登録
       categoryRepository.save(category);
   }

   @Transactional
   public void deleteCategory(Category category) {
       // 渡されたCategoryをDBから削除する
       categoryRepository.delete(category);
   }


}