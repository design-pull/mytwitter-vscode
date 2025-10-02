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
                                .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/mypage/**").authenticated() // ← /mypage配下すべて
                ).formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/mypage", true)
                                .permitAll()).logout(logout -> logout.permitAll());

                // 開発中は CSRF 無効化
                http.csrf(csrf -> csrf.disable());

                return http.build();
        }


}
