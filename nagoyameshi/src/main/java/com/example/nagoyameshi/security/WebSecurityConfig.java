package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity 
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests  
                    // アクセス許可に関する設定★
                .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**", "/passwordreset/**").permitAll()  // すべてのユーザーにアクセスを許可するURL
                .requestMatchers("/restaurants/**", "/company", "/terms").hasAnyRole("ANONYMOUS", "FREE_MEMBER", "PAID_MEMBER")  // 未ログインのユーザー、無料会員、有料会員にアクセスを許可するURL
                .requestMatchers("/restaurants/{restaurantId}/reviews/**", "/reservations/**", "/restaurants/{restaurantId}/reservations/**", "/favorites/**", "/restaurants/{restaurantId}/favorites/**").hasAnyRole("FREE_MEMBER", "PAID_MEMBER") // 無料会員と有料会員にアクセスを許可するURL
                .requestMatchers("/subscription/register","/subscription/create").hasAnyRole("FREE_MEMBER") // 無料会s員（ROLE_FREE_MEMBER）のみ
                .requestMatchers("/subscription/edit","/subscription/update","/subscription/cancel","/subscription/delete").hasAnyRole("PAID_MEMBER") // 有料会員（ROLE_PAID_MEMBER）のみ

                .requestMatchers("/admin/**").hasRole("ADMIN")  // 管理者にのみアクセスを許可するURL
                .anyRequest().authenticated()                   // 上記以外のURLはログインが必要（会員または管理者のどちらでもOK）
            )
            .formLogin((form) -> form
                    // ログインに関する設定★

                .loginPage("/login")              // ログインページのURL
                .loginProcessingUrl("/login")     // ログインフォームの送信先URL
                .defaultSuccessUrl("/?loggedIn")  // ログイン成功時のリダイレクト先URL
                .failureUrl("/login?error")       // ログイン失敗時のリダイレクト先URL
                .permitAll()
            )
            .logout((logout) -> logout
                    // ログアウトに関する設定★

                .logoutSuccessUrl("/?loggedOut")  // ログアウト時のリダイレクト先URL
                .permitAll()
                        )
        .csrf(csrf -> csrf.ignoringRequestMatchers("/stripe/webhook"));
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}