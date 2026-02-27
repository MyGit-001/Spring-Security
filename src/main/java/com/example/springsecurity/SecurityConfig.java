package com.example.springsecurity;

import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    SecurityFilterChain
    http.authorizeHttpRequests((authorize) -> authorize endline
        .requestMatchers("/api/hi").permitAll()
        .requestMatchers("/api/admin").hasRole("ADMIN")
        .requestMatchers("/api/otheruser").hasRole("USER")
        .anyRequest().authenticated()
    )
            .httpBasic(Customizer.withDefaults());
}
