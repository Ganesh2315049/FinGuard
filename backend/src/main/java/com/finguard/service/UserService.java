package com.finguard.service;

import com.finguard.entity.*;
import com.finguard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository users; private final AccountRepository accounts; private final PasswordEncoder encoder;
    public UserService(UserRepository users, AccountRepository accounts, PasswordEncoder encoder) { this.users = users; this.accounts = accounts; this.encoder = encoder; }
    @Transactional public User register(String name, String email, String password) {
        if (users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email is already registered");
        User user=new User(); user.setName(name); user.setEmail(email.trim().toLowerCase()); user.setPassword(encoder.encode(password)); user=users.save(user);
        Account account=new Account(); account.setUser(user); account.setAccountNumber("FG"+System.currentTimeMillis()+String.format("%04d", (int)(Math.random()*10000))); accounts.save(account); return user;
    }
    @Transactional public User ensureDemoAdmin(String email, String password) {
        return ensureDemoUser("FinGuard Administrator", email, password, UserRole.ADMIN);
    }
    @Transactional public User ensureDemoAnalyst(String email, String password) {
        return ensureDemoUser("FinGuard Fraud Analyst", email, password, UserRole.FRAUD_ANALYST);
    }
    @Transactional public User ensureDemoUser(String name, String email, String password, UserRole role) {
        User user = users.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            user = register(name, email, password);
        }
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        return users.save(user);
    }
    public User byEmail(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password")); }
    public User byId(UUID id) { return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")); }
}
