package com.finguard.controller;

import com.finguard.entity.User;
import com.finguard.security.JwtService;
import com.finguard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserService users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthController(UserService users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }
    public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@Size(min=8) String password){}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
    @PostMapping("/register") public Object register(@Valid @RequestBody RegisterRequest r){User u=users.register(r.name(),r.email(),r.password()); return response(u);}
    @PostMapping("/login") public Object login(@Valid @RequestBody LoginRequest r){
        User u;
        u = users.byEmail(r.email());
        if(!u.isEnabled()||!encoder.matches(r.password(),u.getPassword())) throw new IllegalArgumentException("Invalid email or password");
        return response(u);
    }
    private Object response(User u){return java.util.Map.of("token",jwt.create(u.getId(),u.getEmail(),u.getRole().name()),"user",java.util.Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole().name()));}
}
