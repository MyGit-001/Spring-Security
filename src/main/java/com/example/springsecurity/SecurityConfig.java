package com.example.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
//This class provides configuration to the application
@Configuration
//It enables te security for application
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        //form authentication
        http.formLogin(Customizer.withDefaults());

        //Basic authentication
        http.httpBasic(Customizer.withDefaults());
        return http.build();

    }

    //In-Memory Authentication
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails User1 = User.withUsername("User1")
                                .password("{noop}UserPass")
                                .roles("USER").build();

        UserDetails Admin = User.withUsername("Admin")
                                .password("{noop}AdminPass")
                                .roles("ADMIN").build();

        return new InMemoryUserDetailsManager(User1 , Admin);
    }
}