package com.example.nagoyameshi.service;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;

@Service
public class StripeService {
    @Value("${stripe.api-key}")
    private String stripeApiKey;
    
    // @PostConstruct:依存性の注入後に一度だけ実行するメソッド
    @PostConstruct
    private void init() {
        // Stripeのシークレットキーを設定する
        Stripe.apiKey = stripeApiKey;
    }
    
//============================================================================    
    //顧客（StripeのCustomerオブジェクト）を作成する1
    public Customer createCustomer(User user) throws StripeException {
    	
        // 顧客の作成時に渡すユーザーの情報 （Stripe の リファレンス参照）
        CustomerCreateParams customerCreateParams =
            CustomerCreateParams.builder()
                .setName(user.getName())// ユーザー名をStripeに渡す
                .setEmail(user.getEmail())// ユーザーのメールアドレスを渡す
                .build();

        return Customer.create(customerCreateParams);
    }

    
//============================================================================    
    // 支払い方法（StripeのPaymentMethodオブジェクト）を顧客（StripeのCustomerオブジェクト）に紐づける
    //引数で支払い方法のID（String型）と顧客ID（String型）を受け取る
    public void attachPaymentMethodToCustomer(String paymentMethodId, String customerId) throws StripeException {
       
    	// 支払い方法を紐づける顧客（Stripe の リファレンス参照）
        PaymentMethodAttachParams paymentMethodAttachParams =
            PaymentMethodAttachParams.builder()
            .setCustomer(customerId)// どのCustomerに紐づけるか指定
            .build();

        //  PaymentMethod（支払い方法）を取得
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        //  CustomerにPaymentMethodを紐づける
        paymentMethod.attach(paymentMethodAttachParams);
    }

    
//============================================================================        
    // 顧客（StripeのCustomerオブジェクト）のメインカード（デフォルトの支払い方法）（StripeのPaymentMethodオブジェクト）を設定する3
    public void setDefaultPaymentMethod(String paymentMethodId, String customerId) throws StripeException { //paymentMethodId:カードのID customerId :顧客のID
    	
        // 顧客の更新時に渡すデフォルトの支払い方法のID（Stripe の リファレンス参照）
        CustomerUpdateParams customerUpdateParams =
            CustomerUpdateParams.builder()
                .setInvoiceSettings(
                    CustomerUpdateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(paymentMethodId)
                        .build()
                )
                .build();
        
        //Stripeから「顧客IDに対応する顧客情報」を取ってくる。
        Customer customer = Customer.retrieve(customerId);
        //さっき作った「デフォルトカード設定」を Stripe に送って、顧客情報を更新する
        customer.update(customerUpdateParams);
    }

    
//============================================================================        
    // サブスクリプション（StripeのSubscriptionオブジェクト）を作成する4
    public Subscription createSubscription(String customerId, String priceId) throws StripeException {//priceId:Stripe の料金プランID customerId :Stripeの顧客のID
       
    	// サブスクリプションの作成時に渡す顧客IDや価格ID（Stripe の リファレンス参照）
        SubscriptionCreateParams subscriptionCreateParams =
            SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(			//このサブスクリプションは どの料金プランで課金するかを設定している
                    SubscriptionCreateParams
                      .Item.builder()
                      .setPrice(priceId) //料金プランIDをセット
                      .build()
                )
                .build();

        return Subscription.create(subscriptionCreateParams);
    }

    
//============================================================================        
    // 顧客（StripeのCustomerオブジェクト）のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）を取得する5
    public PaymentMethod getDefaultPaymentMethod(String customerId) throws StripeException {
    	
        Customer customer = Customer.retrieve(customerId);
        String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();
        
        return PaymentMethod.retrieve(defaultPaymentMethodId);
    }
     
//============================================================================        
    // 顧客（StripeのCustomerオブジェクト）のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
    public String getDefaultPaymentMethodId(String customerId) throws StripeException {
    	
        Customer customer = Customer.retrieve(customerId);
        
        //顧客の「請求書設定（InvoiceSettings）」を取り出す/その中に保存されている「デフォルト支払い方法のID（例: pm_xxx）」を返す
        return customer.getInvoiceSettings().getDefaultPaymentMethod();
    }
    
//============================================================================        
    // 支払い方法（StripeのPaymentMethodオブジェクト）と顧客（StripeのCustomerオブジェクト）の紐づけを解除する
    public void detachPaymentMethodFromCustomer(String paymentMethodId) throws StripeException {
    	
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.detach();
    }
    
//============================================================================        
    // サブスクリプション（StripeのSubscriptionオブジェクト）を取得する
    public List<Subscription> getSubscriptions(String customerId) throws StripeException {
    	
        // 契約中のサブスクリプションの取得時に渡す顧客ID
        SubscriptionListParams subscriptionListParams =
            SubscriptionListParams.builder()
                .setCustomer(customerId)
                .build();

        return Subscription.list(subscriptionListParams).getData();
    }
    
//============================================================================        
    // サブスクリプション（StripeのSubscriptionオブジェクト）をキャンセルする
    public void cancelSubscriptions(List<Subscription> subscriptions) throws StripeException {
        for (Subscription subscription : subscriptions) {
            subscription.cancel();
        }
    }
 }
