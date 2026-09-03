package com.finguard.controller;

import com.finguard.entity.User;
import com.finguard.security.JwtService;
import com.finguard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UserService users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthController(UserService users, PasswordEncoder encoder, JwtService jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }
    public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@Size(min=8) String password){}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
    @PostMapping("/register") public Object register(@Valid @RequestBody RegisterRequest r){User u=users.register(r.name(),r.email(),r.password()); return java.util.Map.of("token",jwt.create(u.getId(),u.getEmail(),u.getRole().name()),"user",java.util.Map.of("name",u.getName(),"email",u.getEmail(),"role",u.getRole()));}
    @PostMapping("/login") public Object login(@Valid @RequestBody LoginRequest r){User u=users.byEmail(r.email()); if(!encoder.matches(r.password(),u.getPassword())) throw new IllegalArgumentException("Invalid email or password"); return java.util.Map.of("token",jwt.create(u.getId(),u.getEmail(),u.getRole().name()),"user",java.util.Map.of("name",u.getName(),"email",u.getEmail(),"role",u.getRole()));}
}
