package com.finguard.config;

import com.finguard.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;

@Configuration @RequiredArgsConstructor
public class SecurityConfig {
    @Value("${finguard.frontend-url}") private String frontendUrl;
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter){ try { return http.csrf(c->c.disable()).cors(c->c.configurationSource(request->{var config=new CorsConfiguration(); config.setAllowedOrigins(List.of(frontendUrl)); config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); config.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key")); config.setAllowCredentials(true); return config;})).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/swagger-ui/**","/v3/api-docs/**").permitAll().requestMatchers("/api/customer/**").hasRole("CUSTOMER").requestMatchers("/api/analyst/**").hasRole("FRAUD_ANALYST").requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build(); } catch(Exception e){throw new IllegalStateException(e);} }
}
