package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;




@Controller
@RequestMapping("/subscription")

public class SubscriptionController {
	  @Value("${stripe.premium-plan-price-id}")
	  private String premiumPlanPriceId;
	   
	// ==================== フィールド ====================
	private final UserService  userService ; // 依存性注入用のサービス
	private final StripeService stripeService;// 依存性注入用のサービス

	// ==================== コンストラクタ ====================
	// コンストラクタで依存性注入
	public SubscriptionController(UserService userService,StripeService stripeService) {
	    this.userService = userService;
	    this.stripeService = stripeService;

	}
	// ============================================================
	//有料プラン登録ページ（subscription/register.htmlファイル）を表示する
	@GetMapping("/register")
	public String register() { 
		
		 return "subscription/register";
	}
	
	// ============================================================
	@PostMapping("/create")
	public String create(
	    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, // ログイン中のユーザー情報
	    @RequestParam String paymentMethodId,                     // フォームから送られてきた支払い方法ID
	    RedirectAttributes redirectAttributes)                    // メッセージを次の画面に渡すための仕組み
	 {
        User user = userDetailsImpl.getUser(); //ログインユーザーの User を取得

        // ユーザーのstripeCustomerIdフィールドがnull、つまりそのユーザーが初めてサブスクリプションに加入する場合の処理
        if (user.getStripeCustomerId() == null) {
            try {
                // 顧客（StripeのCustomerオブジェクト）を作成する
                Customer customer = stripeService.createCustomer(user);

                // stripeCustomerIdフィールドに顧客IDを保存する
                userService.saveStripeCustomerId(user, customer.getId());
                
            } catch (StripeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました。再度お試しください。");

                return "redirect:/";
            }
        }
        
        String stripeCustomerId = user.getStripeCustomerId();

        try {
            // フォームから送信された支払い方法（StripeのPaymentMethodオブジェクト）を顧客に紐づける
            stripeService.attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);

            // フォームから送信された支払い方法を顧客のデフォルトの支払い方法に設定する
            stripeService.setDefaultPaymentMethod(paymentMethodId, stripeCustomerId);

            // サブスクリプション（StripeのSubscriptionオブジェクト）を作成する
            stripeService.createSubscription(stripeCustomerId, premiumPlanPriceId);

        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました。再度お試しください。");

            return "redirect:/";
        }

        // ユーザーのロールを更新する
        userService.updateRole(user, "ROLE_PAID_MEMBER");//Userテーブル「有料会員 (ROLE_PAID_MEMBER)」に更新
        userService.refreshAuthenticationByRole("ROLE_PAID_MEMBER");//今の認証情報を上書きするための処理

        //RedirectAttributesは、リダイレクト先へデータを受け渡すためのインターフェース
        //addFlashAttribute：次の画面で成功メッセージやエラーメッセージを画面に表示するために使う
        redirectAttributes.addFlashAttribute("successMessage", "有料プランへの登録が完了しました。");

        
        return "redirect:/";
    }
	
	// ============================================================	
	//お支払い方法編集ページ（subscription/edit.htmlファイル）を表示する
	
	  @GetMapping("/edit")
	   public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, Model model) {
	       User user = userDetailsImpl.getUser();//ログインユーザーの User を取得

	       try {
	           // 顧客のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）を取得する
	           PaymentMethod paymentMethod = stripeService.getDefaultPaymentMethod(user.getStripeCustomerId());

	           model.addAttribute("card", paymentMethod.getCard());
	           model.addAttribute("cardHolderName", paymentMethod.getBillingDetails().getName());
	           
	       } catch (StripeException e) {
	           redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法を取得できませんでした。再度お試しください。");

	           return "redirect:/";
	       }

	       return "subscription/edit";
	   }
	  
	  // ============================================================
	  //顧客のデフォルトの支払い方法を更新する
	  
	   @PostMapping("/update")
	   public String update(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam String paymentMethodId, RedirectAttributes redirectAttributes) {
	       User user = userDetailsImpl.getUser();//ログインユーザーの User を取得
	       String stripeCustomerId = user.getStripeCustomerId();

	       try {
	           // 現在のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
	           String currentDefaultPaymentMethodId = stripeService.getDefaultPaymentMethodId(stripeCustomerId);

	           // フォームから送信された支払い方法を顧客（StripeのCustomerオブジェクト）に紐づける
	           stripeService.attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);

	           // フォームから送信された支払い方法を顧客のデフォルトの支払い方法に設定する
	           stripeService.setDefaultPaymentMethod(paymentMethodId, stripeCustomerId);

	           // 以前のデフォルトの支払い方法と顧客の紐づけを解除する
	           stripeService.detachPaymentMethodFromCustomer(currentDefaultPaymentMethodId);
	           
	       } catch (StripeException e) {
	           redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法の変更に失敗しました。再度お試しください。");

	           return "redirect:/";
	       }

	       redirectAttributes.addFlashAttribute("successMessage", "お支払い方法を変更しました。");

	       return "redirect:/";
	   }

	   // ============================================================
	   //有料プラン解約ページ（subscription/cancel.htmlファイル）を表示する
	   
	   @GetMapping("/cancel")
	   public String cancel() {
	       return "subscription/cancel";
	   }

	   // ============================================================
	   //顧客のサブスクリプションをキャンセルし、デフォルトの支払い方法と顧客の紐づけを解除する。ロールを更新する。
	   
	   @PostMapping("/delete")
	   public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
	       User user = userDetailsImpl.getUser();

	       try {
	           // 顧客が契約中のサブスクリプション（StripeのSubscriptionオブジェクト）を取得する
	           List<Subscription> subscriptions = stripeService.getSubscriptions(user.getStripeCustomerId());

	           // 顧客が契約中のサブスクリプションをキャンセルする
	           stripeService.cancelSubscriptions(subscriptions);

	           // デフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
	           String defaultPaymentMethodId = stripeService.getDefaultPaymentMethodId(user.getStripeCustomerId());

	           // デフォルトの支払い方法と顧客の紐づけを解除する
	           stripeService.detachPaymentMethodFromCustomer(defaultPaymentMethodId);
	       } catch (StripeException e) {
	           redirectAttributes.addFlashAttribute("errorMessage", "有料プランの解約に失敗しました。再度お試しください。");

	           return "redirect:/";
	       }

	       // ユーザーのロールを更新する
	       userService.updateRole(user, "ROLE_FREE_MEMBER");
	       userService.refreshAuthenticationByRole("ROLE_FREE_MEMBER");

	       redirectAttributes.addFlashAttribute("successMessage", "有料プランを解約しました。");

	       return "redirect:/";
	   }
		
	
	
}
