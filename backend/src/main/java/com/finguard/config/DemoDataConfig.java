package com.finguard.config;

import com.finguard.entity.User;
import com.finguard.entity.UserRole;
import com.finguard.repository.UserRepository;
import com.finguard.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner demoUsers(UserRepository users, UserService userService,
            @Value("${finguard.demo-seed:false}") boolean enabled,
            @Value("${finguard.demo-password:}") String password,
            @Value("${finguard.demo-accounts-enabled:true}") boolean demoAccountsEnabled,
            @Value("${finguard.demo-admin-email:2315049@nec.edu.in}") String adminEmail,
            @Value("${finguard.demo-admin-password:Ganesh@123}") String adminPassword,
            @Value("${finguard.demo-analyst-email:ganeshbavana26@gmail.com}") String analystEmail,
            @Value("${finguard.demo-analyst-password:Ganesh@23}") String analystPassword) {
        return args -> {
            if (enabled && password != null && password.length() >= 8) {
                create(users, userService, "Demo Customer", "demo.customer@finguard.local", UserRole.CUSTOMER, password);
                create(users, userService, "Demo Analyst", "demo.analyst@finguard.local", UserRole.FRAUD_ANALYST, password);
            }
            if (demoAccountsEnabled && validCredentials(adminEmail, adminPassword)) {
                userService.ensureDemoAdmin(adminEmail, adminPassword);
            }
            if (demoAccountsEnabled && validCredentials(analystEmail, analystPassword)) {
                userService.ensureDemoAnalyst(analystEmail, analystPassword);
            }
        };
    }
    private boolean validCredentials(String email, String password) {
        return email != null && !email.isBlank() && password != null && password.length() >= 8;
    }
    private void create(UserRepository users, UserService userService, String name, String email, UserRole role, String password) {
        if (users.existsByEmailIgnoreCase(email)) return;
        User user = userService.register(name, email, password);
        user.setRole(role);
        users.save(user);
    }
}
