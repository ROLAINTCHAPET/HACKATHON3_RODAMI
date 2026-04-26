package com.rodami.campuslink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsService minimal requis par Spring Security.
     * Pas utilisé en pratique car on utilise JwtAuthenticationFilter.
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(
            User.withUsername("dummy")
                .password("dummy")
                .roles("USER")
                .build()
        );
    }
}
