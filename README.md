<img width="1740" height="859" alt="image" src="https://github.com/user-attachments/assets/a04b783c-1247-4495-b964-6d0f0905c171" />

This diagram maps out exactly what happens under the hood when a user tries to log into a Spring Security application.

To make it as simple as possible, think of this process like a guest trying to enter a highly secure VIP club. Here is the step-by-step breakdown matching the numbers in your diagram:

## The Login Request
Step 1: The Knock on the Door
The user submits their username and password. This request is intercepted by the **`Authentication Filter`**, which acts like the bouncer at the front door.

Step 2: The Temporary ID Slip
The Filter takes those raw credentials and packages them into an `unverified Authentication objectr`. Think of this as a temporary, unapproved visitor slip that just has the name and password scribbled on it.

Step 3: Calling the Supervisor
The Filter passes this temporary slip to the **`Authentication Managerr`**. The Manager is like the shift supervisor—it doesn't verify the ID itself, but it knows exactly which specialist to give it to.

## The Verification Process
Step 4: Delegating to the Specialist
The Manager hands the slip to the **`Authentication Provider`**. The Provider is the actual specialist who knows how to process this specific type of login (e.g., verifying a standard username/password versus verifying a Google OAuth token).

Step 5: Checking the Guest List
The Provider contacts the **`User Details Servicer`**. This service acts as the bridge to your database. It looks up the username to see if the person actually exists in the system and fetches their official record (including their stored, encrypted password and their roles).

Step 6: Checking the Secret Code
The Provider now has the password the user just typed, and the encrypted password from the database. It uses the **`Password Encoderr`** to safely compare them. The Encoder checks if the typed password, when scrambled, matches the database's scrambled password.

## The Aftermath
Step 7: The VIP Badge
If everything matches, the Provider creates a new, `fully verified Authentication object`. This is the official "VIP Badge" that includes the user's roles (like USER or ADMIN). It hands this back to the Manager.

Step 8: Returning to the Front Door
The Manager gives this official VIP Badge back to the front-door bouncer (the Authentication Filter).

Step 9: Stamping the Hand (Security Context)
The Filter takes this official badge and stores it in the **`Security Contextr`**. This is a secure area in the application's memory. By placing it here, the app effectively "stamps the user's hand." For every subsequent click or page load, the app will check the Security Context, see the badge, and know the user is already logged in.

Step 10: Welcome In
Finally, the Filter responds to the user, typically by redirecting them to the dashboard or sending a successful response back to the browser.

## Some Basics Before diving into Security aspect
### 🍪 Cookies 
What they are: Small pieces of data stored in your browser by a website. \
Purpose: They help websites remember things about you. 

>Example:
You visit an online store and add items to your cart. \
The site saves a cookie in your browser with your cart ID. \
When you come back later, the site reads the cookie and shows your cart again. 

### 🧩 Sessions 
What they are: A way for the server to keep track of your activity while you’re logged in. \
Purpose: To maintain state (like “this user is logged in” or “this is their shopping cart”).

>Example:
You log into your bank account. \
The server creates a session and stores your user ID in memory.\
It gives your browser a cookie with a session ID (like a ticket number). \
Every time you click around, your browser sends that session ID back, and the server knows it’s you.

### 🔑 Tokens 
What they are: A piece of data (often a string of letters/numbers) that proves your identity. \
Purpose: Used in modern apps (especially APIs) to authenticate users without storing state on the server.

>Example (JWT – JSON Web Token):
You log into a website. \
The server gives you a token that says “this user is John, valid for 1 hour.” \
Your browser/app stores it (often in local storage).\
Every time you call the API, you send the token.\
The server checks the token and trusts it without needing to look up a session.

🏷️ What “State” Means
State = memory of what happened before.
If a system is stateful, it remembers things about you between requests (like “you’re logged in” or “your cart has 3 items”).
If a system is stateless, it treats every request as brand new, with no memory of past interactions.

### 📦 Stateful Example (Sessions)
Imagine you go to a library:
You check in at the front desk → they give you a locker key (session ID).
The library keeps your bag in a locker (server stores your info).
Every time you come back, you show the key → they know it’s your locker.

👉 The library (server) remembers your state because it keeps track of your locker.

### 🎟️ Stateless Example (Tokens)
Now imagine a concert:
At the entrance, you get a wristband (token).
The wristband itself says “VIP, valid until midnight.”
Every time you go to a section, security just looks at the wristband.
The concert doesn’t keep a list of who’s inside — the wristband itself carries the info.

👉 The system doesn’t remember you — it just checks the token each time. That’s stateless.

✅ Why Stateless Matters
1. Scalability: Servers don’t need to store millions of sessions in memory. Each request carries its own proof (token).
2. Flexibility: Works well with APIs and microservices, where different servers may handle different requests.
3. Simplicity: No need to “look up” who you are — the token itself contains the info.

## 🔑 Session-Based Authentication
Where info is stored: On the server.

How it works: 
* You log in → server creates a session with a session ID and stores your info in its memory 
* Server gives your browser a cookie with a session ID. 
* ***Every request you make includes that cookie and is sent back to Server***. 
* The server looks up the session ID in its memory and says, “Oh, that’s you, you’re authenticated.”

Pros: Simple, widely used, works well for traditional web apps. \
Cons: Server must keep track of every session → can be heavy for large-scale apps. 

👉 Example: You log into your bank website. The server remembers your account in a session, and your browser sends the session ID each time you click around.

## 🎟️ Token-Based Authentication
Where info is stored: In the token itself (usually on the client side).

How it works: 
* You log in → server gives you a token (like a JWT).
* The token contains encoded info (like your user ID, roles, expiry time). It itself contains the proof of who you are. It gets stored in the client side (your browser). 
* On every request, you send the token back (usually in the Authorization header). 
* ***The server checks the token’s validity (signature, expiry, etc.) and trusts it***

Pros: Stateless (server doesn’t need to remember sessions), great for APIs and mobile apps, easy to scale. \
Cons: Tokens can get large, must be carefully secured, harder to revoke before expiry. 

👉 Example: You log into a mobile app. The app stores a JWT token. Every time it calls the backend API, it sends the token. The server trusts the token without looking up a session.

## ✅ Key Difference
Sessions: Server keeps track of you in memory. Each request uses the session ID to look up your info. \
Tokens: Server doesn’t remember you. Each request carries all the proof inside the token itself. 

So both approaches authenticate on every request — ***the difference is where the “memory” lives:***

Sessions → on the server. \
Tokens → in the token itself (client side).

## Coming back to Spring Security 
Spring Security is a powerful framework that focuses on providing both authentication and authorization to Java applications, also addressing common security vulnerabilities like CSRF (cross-site request forgery) and CORS (Cross-origin resource sharing).

Whenever we expose REST endpoints in Spring Boot, all the incoming requests are first received by the DispatcherServlet. The DispatcherServlet is the front and responsible to dispatch incoming HttpRequests to the correct handlers.

Adding spring security, enables us with the security filter chain to process requests and perform security-related tasks. We can customize this filter chain by adding or modifying filters based on our requirements.

### How can you add Spring Security in the your Project

### Add Dependency 
```Java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security-test</artifactId>
            <scope>test</scope>
        </dependency>
```
Spring Security provides a default ***form-based authentication mechanism*** that is enabled by default. However, it is important to note that this mechanism must be explicitly configured in the Spring Security configuration. The default configuration includes a default login page and a default success URL, it will also provide the username (user) and password (Changes everytime the applcation restarts)

<img width="280" height="170" alt="image" src="https://github.com/user-attachments/assets/41908c2f-7229-42f5-8bbf-74880ae0a081" /> <br>
<img width="280" height="170" alt="image" src="https://github.com/user-attachments/assets/7a9924d9-af76-466f-bbd7-47c7ca6909a9" />


We could set a static Username and provide by making changes in Application properties 
```Java
spring.application.name=springsecurity
spring.security.user.password = Rishi@123
spring.security.user.name = admin
```



## What if i want to create a implementing our very own Basic authentication for my endpoints ? 
1. If you wish to create your very own autentication then for that Create a Config file and declare that as a @Configuration
2. Create an method of FilterChain and set it as @Bean

```Java
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        //form authentication
        http.formLogin(Customizer.withDefaults());

        //Basic authentication
        http.httpBasic(Customizer.withDefaults());
        return http.build();

    }
```

## 1. The Class-Level Annotations
These annotations tell the Spring framework how to treat this Java class when your application starts up.

**`@Configuration`**: This tells Spring that this class is a source of bean definitions. When the application context loads, Spring will look inside this class, find any methods annotated with @Bean, and execute them to register those objects in the application context.

**`@EnableWebSecurity`**: This is the crucial trigger for Spring Security. It tells Spring to apply your custom security configurations to the global web security setup. It effectively says, "Don't just use the absolute default, invisible security setup; look at the rules I'm defining in this class instead."

## 2. The SecurityFilterChain Bean
In Spring Security, incoming HTTP requests don't just hit your controllers directly. They pass through a chain of security filters (the "Filter Chain") that check if the request is safe and authorized.

**`@Bean`**: By annotating this method with @Bean, you are creating a custom SecurityFilterChain object and handing it over to Spring to manage.

**`HttpSecurity http`**: This is the builder object provided by Spring. You use it to configure all your web-based security rules (like which URLs are protected, how users log in, etc.).

## 3. The Security Rules (Line-by-Line)
Inside the method, you are defining the exact rules for how the application should handle incoming traffic. This specific setup is quite strict.

_`http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());`_

* This is the authorization rule. It uses a lambda expression to tell Spring Security: "For any HTTP request (anyRequest()) that comes into the application, require the user to be fully logged in (authenticated())."
* In a practical scenario like a contact management system (where users are storing private directories like in EzManager), this ensures that absolutely no one can view, add, or delete contacts without proving who they are first.

_`http.formLogin(Customizer.withDefaults());`_

* This enables ⭐**form-based authentication**. If an unauthenticated user tries to access a protected URL in their web browser, Spring Security will automatically intercept the request and redirect them to a default, auto-generated HTML login page. Customizer.withDefaults() simply applies the standard, out-of-the-box settings for this.

_`http.httpBasic(Customizer.withDefaults());`_

* This enables ⭐**HTTP Basic authentication**. Instead of showing an HTML login page, it allows clients to send their credentials (username and password) directly in the HTTP headers of the request.
* This is essential for REST APIs. If you are testing your endpoints using Postman, or if a separate frontend framework (like React or Angular) is making API calls to your backend, they will use this method to authenticate rather than filling out an HTML form.

_`return http.build();`_

* Finally, this takes all the configurations you just attached to the HttpSecurity builder, constructs the final SecurityFilterChain, and returns it to the Spring context.

``` JAVA
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
```  
⭐**In-memory authentication** is exactly what it sounds like: instead of checking a database to see if a user exists, Spring Security checks a hardcoded list of users stored temporarily in your application's RAM (memory).

```JAVA
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. The Filter Chain (From our previous step)
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // 2. The In-Memory User Details Service
    @Bean
    public UserDetailsService userDetailsService() {
        // Creating a standard user
        UserDetails regularUser = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password123"))
                .roles("USER")
                .build();

        // Creating an admin user
        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        // Saving them to memory
        return new InMemoryUserDetailsManager(regularUser, adminUser);
    }

    // 3. The Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 1. How This Works
* `UserDetailsService Bean` By creating a bean of this type, you are telling Spring Security, "Don't bother looking for a default user anymore; use this custom service to find users." \
* `InMemoryUserDetailsManager` This is the actual implementation of the UserDetailsService. You pass your created users into its constructor, and it holds them in a local collection in the RAM. \
* `PasswordEncoder Bean` (Step 6 from your diagram): Spring Security strictly enforces that passwords must not be stored in plain text. Even though we are hardcoding these users in memory, we must encrypt their passwords using  BCryptPasswordEncoder. When someone tries to log in, Spring will hash the password they typed into the form and compare it against these hashed memory values.

## 2. How are we returning InMemoryUserDetailsManager to it?
This works because of standard Java polymorphism. \
UserDetailsService is the interface (the generic requirement). \
InMemoryUserDetailsManager is a concrete class that implements that interface (the specific tool). \
Because InMemoryUserDetailsManager implements the UserDetailsService interface, it officially is a UserDetailsService. Therefore, returning it perfectly satisfies the method's signature.

Think of it like a method structured like this: public List getNames(). You are allowed to return a new ArrayList() inside that method because an ArrayList implements the List interface. Spring Security asks for a general "user lookup tool," and you are handing it a specific "in-memory user lookup tool." 

## 3. What does new InMemoryUserDetailsManager(...) actually do?
When you instantiate the InMemoryUserDetailsManager and pass regularUser and adminUser into its constructor, it takes those user objects and stores them internally inside a standard Java HashMap. 

Because this HashMap exists only in the application's RAM, it is incredibly fast but entirely temporary. When a user tries to log in, Spring Security asks this manager to loadUserByUsername("admin"), and the manager simply searches its HashMap for that key and returns the matching user record.


## Role Based Authentication

Adding two more endpoints in the controller \
* Requests with '/admin'  should be having ADMIN role, if the inputted cred are of ADMIN, then the user can access admin role. \
* Requests with '/User' should be having USER role, if the inputted cred are of USER, then the user can access user role. 

```Java
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String userEndpoint(){
        return "Hello, Admin";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String adminEndpoint(){
        return "Hello, User";
    }
```

### Admin role
<img width="795" height="464" alt="image" src="https://github.com/user-attachments/assets/db3f7388-4db8-4228-b75b-e14ac69dd224" />


### User Role
<img width="795" height="464" alt="image" src="https://github.com/user-attachments/assets/8451e0a0-0e45-4289-874c-2b7e4281f2d1" />



