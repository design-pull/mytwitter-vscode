package com.example.mytwitter_vscode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 開発用の固定ユーザー（user/password）
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/home", "/login",
                    "/css/**", "/js/**", "/images/**",
                    "/h2-console/**"   // H2 Console を許可
                ).permitAll()
                .requestMatchers("/mypage/**").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/mypage", true)
                .permitAll()
        );

        http.logout(logout -> logout.permitAll());

        // H2 Console 用に追加設定
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")); // CSRF 無効化
        http.headers(headers -> headers.frameOptions().disable());         // フレーム許可

        return http.build();
    }
}
