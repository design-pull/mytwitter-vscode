package com.example.mytwitter_vscode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

        // 開発用の固定ユーザー（user/password）
        @Bean
        public UserDetailsService userDetailsService() {
                // withDefaultPasswordEncoder はデモ用途向け。開発確認には十分です。
                UserDetails user = User.withDefaultPasswordEncoder().username("user")
                                .password("password").roles("USER").build();
                return new InMemoryUserDetailsManager(user);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(auth -> auth
                                // 公開ページ
                                .requestMatchers("/").permitAll()
                                // 静的リソースを許可
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                // ログイン必須ページ
                                .requestMatchers("/mypage").authenticated()
                // 静的リソースや画像/CSS/JSを使うなら、必要に応じて以下を追加
                // .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                ) // ← ここで閉じるのがポイント！
                                .formLogin(form -> form.loginPage("/login")
                                                .defaultSuccessUrl("/mypage", true) // ログイン後は必ず
                                                                                    // /mypage
                                                                                    // へ
                                                .permitAll())
                                .logout(logout -> logout.permitAll()).csrf(csrf -> csrf.disable()); // 開発中のみ無効化

                return http.build();
        }

}
