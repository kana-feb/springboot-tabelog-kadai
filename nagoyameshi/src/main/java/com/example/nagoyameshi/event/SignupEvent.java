package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.User;

import lombok.Getter;

//イベントに関する情報を取得できるようにgetterを定義する
@Getter
//ApplicationEventクラスを継承する★
public class SignupEvent extends ApplicationEvent {
	//会員登録したユーザーの情報（Userオブジェクト）とリクエストを受けたURL（https://ドメイン名/signup）を保持します。
    private User user;
    private String requestUrl;

    public SignupEvent(Object source, User user, String requestUrl) {
    	 //superでスーパークラス（親クラス）のコンストラクタを呼び出し、イベントのソース（発生源） を渡します。
    	super(source);

        this.user = user;
        this.requestUrl = requestUrl;
    }
}