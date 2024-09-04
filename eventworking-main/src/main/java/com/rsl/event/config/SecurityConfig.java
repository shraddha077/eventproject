package com.rsl.event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rsl.event.security.JwtAuthenticationEntryPoint;
import com.rsl.event.security.JwtAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // Disable CSRF protection for APIs
                .cors(withDefaults())  // Enable CORS with default settings
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/create_user").permitAll()  // Public access
                        .requestMatchers("/quotation", "/invoice", "/allinvoices", "/allquotations", "/vendor", "/allvendors").permitAll()  // Public access
                        .requestMatchers("/home/**").authenticated()  // Secure '/home/**' endpoints
                        .anyRequest().authenticated()  // All other endpoints require authentication
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))  // Handle unauthorized access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // Stateless session management

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before the default authentication filter
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // Set custom UserDetailsService
        provider.setPasswordEncoder(passwordEncoder);  // Set password encoder
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();  // Provide authentication manager bean
    }

}
