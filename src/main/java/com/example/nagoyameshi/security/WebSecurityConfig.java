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
						.requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/categorystorage/**", "/",
								"/signup/**", "/restaurants/**", "/forgot-password/**", "/companies/**", "/terms/**","/primes/**")
						.permitAll() // すべてのユーザーにアクセスを許可するURL           
						.requestMatchers("/users/{userId}/favorites","/favorites/**","/restaurants/{restaurantid}/reviews/**","/restaurants/{restaurantid}/reservations/**").authenticated()						
						.requestMatchers("/admin/**", "/categories/**").hasRole("ADMIN") // 管理者にのみアクセスを許可するURL
						.anyRequest().authenticated() // 上記以外のURLはログインが必要（会員または管理者のどちらでもOK）
				) 
				
//				"/restaurants/{restaurantid}/reviews/**" ログインユーザーのみ
				.formLogin((form) -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.successHandler(new CustomAuthenticationSuccessHandler()) // カスタム成功ハンドラをセット
						.failureUrl("/login?error")
						.permitAll())
				.logout((logout) -> logout
						.logoutSuccessUrl("/?loggedOut") // ログアウト時のリダイレクト先URL
						.permitAll());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}