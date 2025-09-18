//UserDetailsServiceImpl 認証・認可のビジネスロジックを追加
package com.example.nagoyameshi.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	//コンストラクタ　依存性の注入（DI）　userテーブルを使えるようにする
	private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //データベースからユーザー情報を取得し、UserDetailsオブジェクトを生成
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // 送信されたユーザー名と一致するユーザー情報をテーブルから取得
        	 //「ユーザー名は一意であるため、取得されるのは常に1件のみ。従って、get(0)で認証対象のユーザー情報を取得している。
            User user = userRepository.findByEmail(email);
            String userRoleName = user.getRole().getName();

            // ロールのリスト用オブジェクトを生成
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // ユーザーのロール名をリストに追加
            authorities.add(new SimpleGrantedAuthority(userRoleName));

            // ユーザー情報とロールリストをもとにUserDetailsImplを生成
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }
}