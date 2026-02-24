<img width="1740" height="859" alt="image" src="https://github.com/user-attachments/assets/a04b783c-1247-4495-b964-6d0f0905c171" />

## **How can you add Spring Security in the your Project**_

Add Dependency 
<img width="838" height="154" alt="image" src="https://github.com/user-attachments/assets/ce103e03-9730-415d-a90e-2e0c56d8fd15" />

Spring Security provides a default form-based authentication mechanism that is enabled by default. However, it is important to note that this mechanism must be explicitly configured in the Spring Security configuration. The default configuration includes a default login page and a default success URL, it will also provide the username (user) and password (Changes everytime the applcation restarts)


We could set a static Username and provide by making changes in Application properties

<img width="475" height="139" alt="image" src="https://github.com/user-attachments/assets/75443839-24cd-4e6f-890e-423ef5d1fb89" />

##What if i want to create a implementing our very own Basic authentication for my endpoints ? 
1. If you wish to create your very own autentication then for that Create a Config file and declare that as a @Configuration
2. Create an method of FilterChain and set it as @Bean

<img width="869" height="280" alt="image" src="https://github.com/user-attachments/assets/f85db1a4-443a-4196-a9ac-a8b4ec2296d5" />

This above code can be written in 2 ways, 
Either using Lambda DSL introduced in Spring 5,5+ 

```Java
http.authorizeHttpRequests((authorize) -> authorize endline
        .requestMatchers("/api/hi").permitAll()
        .requestMatchers("/api/admin").hasRole("ADMIN")
        .requestMatchers("/api/otheruser").hasRole("USER")
        .anyRequest().authenticated()
    )
    .httpBasic(Customizer.withDefaults());
```

Or by using the prev Spring version 3 and 4
```JAVA
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/user/**").hasRole("USER")
        .antMatchers("/**").permitAll()
        .and().formLogin()
            .loginPage("/signin")
            .loginProcessingUrl("/dologin")
            .defaultSuccessUrl("/user/index")
        .and().csrf().disable();
}
```


