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
            @Value("${finguard.demo-admin-enabled:false}") boolean adminEnabled,
            @Value("${finguard.demo-admin-email:}") String adminEmail,
            @Value("${finguard.demo-admin-password:}") String adminPassword) {
        return args -> {
            if (enabled && password != null && password.length() >= 8) {
                create(users, userService, "Demo Customer", "demo.customer@finguard.local", UserRole.CUSTOMER, password);
                create(users, userService, "Demo Analyst", "demo.analyst@finguard.local", UserRole.FRAUD_ANALYST, password);
            }
            if (adminEnabled && adminEmail != null && !adminEmail.isBlank() && adminPassword != null && adminPassword.length() >= 8) {
                userService.ensureDemoAdmin(adminEmail, adminPassword);
            }
        };
    }
    private void create(UserRepository users, UserService userService, String name, String email, UserRole role, String password) {
        if (users.existsByEmailIgnoreCase(email)) return;
        User user = userService.register(name, email, password);
        user.setRole(role);
        users.save(user);
    }
}
