<img width="1740" height="859" alt="image" src="https://github.com/user-attachments/assets/a04b783c-1247-4495-b964-6d0f0905c171" />

This diagram maps out exactly what happens under the hood when a user tries to log into a Spring Security application.

To make it as simple as possible, think of this process like a guest trying to enter a highly secure VIP club. Here is the step-by-step breakdown matching the numbers in your diagram:

## The Login Request
Step 1: The Knock on the Door
The user submits their username and password. This request is intercepted by the _**Authentication Filter**_, which acts like the bouncer at the front door.

Step 2: The Temporary ID Slip
The Filter takes those raw credentials and packages them into an unverified _**Authentication objectr**_. Think of this as a temporary, unapproved visitor slip that just has the name and password scribbled on it.

Step 3: Calling the Supervisor
The Filter passes this temporary slip to the _**Authentication Managerr**_. The Manager is like the shift supervisor—it doesn't verify the ID itself, but it knows exactly which specialist to give it to.

## The Verification Process
Step 4: Delegating to the Specialist
The Manager hands the slip to the _**Authentication Providerr**_. The Provider is the actual specialist who knows how to process this specific type of login (e.g., verifying a standard username/password versus verifying a Google OAuth token).

Step 5: Checking the Guest List
The Provider contacts the _**User Details Servicer**_. This service acts as the bridge to your database. It looks up the username to see if the person actually exists in the system and fetches their official record (including their stored, encrypted password and their roles).

Step 6: Checking the Secret Code
The Provider now has the password the user just typed, and the encrypted password from the database. It uses the _**Password Encoderr**_ to safely compare them. The Encoder checks if the typed password, when scrambled, matches the database's scrambled password.

## The Aftermath
Step 7: The VIP Badge
If everything matches, the Provider creates a new, fully verified Authentication object. This is the official "VIP Badge" that includes the user's roles (like USER or ADMIN). It hands this back to the Manager.

Step 8: Returning to the Front Door
The Manager gives this official VIP Badge back to the front-door bouncer (the Authentication Filter).

Step 9: Stamping the Hand (Security Context)
The Filter takes this official badge and stores it in the _**Security Contextr**_. This is a secure area in the application's memory. By placing it here, the app effectively "stamps the user's hand." For every subsequent click or page load, the app will check the Security Context, see the badge, and know the user is already logged in.

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
The server checks the token and trusts it without needing to look up a session.\

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
* You log in → server creates a session with a session ID and stores your info in its memory \
* Server gives your browser a cookie with a session ID. \
* ***Every request you make includes that cookie and is sent back to Server***. \
* The server looks up the session ID in its memory and says, “Oh, that’s you, you’re authenticated.”

Pros: Simple, widely used, works well for traditional web apps. \
Cons: Server must keep track of every session → can be heavy for large-scale apps. \

👉 Example: You log into your bank website. The server remembers your account in a session, and your browser sends the session ID each time you click around.

## 🎟️ Token-Based Authentication
Where info is stored: In the token itself (usually on the client side).

How it works: 
* You log in → server gives you a token (like a JWT). \
* The token contains encoded info (like your user ID, roles, expiry time). It itself contains the proof of who you are. It gets stored in the client side (your browser). \
* On every request, you send the token back (usually in the Authorization header). \
* ***The server checks the token’s validity (signature, expiry, etc.) and trusts it***

Pros: Stateless (server doesn’t need to remember sessions), great for APIs and mobile apps, easy to scale. \
Cons: Tokens can get large, must be carefully secured, harder to revoke before expiry. \

👉 Example: You log into a mobile app. The app stores a JWT token. Every time it calls the backend API, it sends the token. The server trusts the token without looking up a session.

## ✅ Key Difference
Sessions: Server keeps track of you in memory. Each request uses the session ID to look up your info. \
Tokens: Server doesn’t remember you. Each request carries all the proof inside the token itself. \

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
        .antMatchers("/**").permitAll();
}
```


