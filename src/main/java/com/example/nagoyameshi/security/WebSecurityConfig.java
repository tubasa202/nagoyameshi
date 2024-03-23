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
	                    .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/categorystorage/**", "/", "/signup/**", "/forgot-password/**","/restaurants/**","companies","terms").permitAll()
	                    .requestMatchers("/users/{userId}/favorites", "/favorites/**", "/restaurants/{restaurantid}/reviews/**", "/restaurants/{restaurantid}/reservations/**", "/subscription/**").hasAnyRole("FREE_MEMBER", "PAID_MEMBER")       
	                    .requestMatchers("/restaurants/**", "/companies/**", "/terms/**").hasAnyRole("ANONYMOUS", "FREE_MEMBER", "PAID_MEMBER")     
	                    .requestMatchers("/subscription/register", "/subscription/create").hasRole("FREE_MEMBER") 
	                    .requestMatchers("/subscription/edit", "/subscription/update", "/subscription/cancel", "/subscription/delete").hasRole("PAID_MEMBER") 
	                    .requestMatchers("/admin/**", "/categories/**").hasRole("ADMIN")
	                    .anyRequest().authenticated()
	            )
	            .formLogin((form) -> form
	                    .loginPage("/login")
	                    .loginProcessingUrl("/login")
//	                    .successHandler(new CustomAuthenticationSuccessHandler())
	                    .defaultSuccessUrl("/",true)  
	                    .failureUrl("/login?error")
	                    .permitAll())
	            .logout((logout) -> logout
	                    .logoutSuccessUrl("/?loggedOut")
	                    .permitAll());

	    return http.build();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}