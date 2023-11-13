package org.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    // SecurityFilterChain allows configuring
    // web based security for specific http requests.
    private final JwtConverter converter;

    public SecurityConfig(JwtConverter converter) {
        this.converter = converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        http.csrf().disable();
        http.cors();

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,
                        "/api/authenticate",
                        "/api/create_account").permitAll()
                .antMatchers(HttpMethod.GET, // include all get endpoints here
                        "/api/users",
                        "/comments",
                        "/comments/*",
                        "/library_items",
                        "/library_items/*",
                        "/quotes",
                        "/quotes/*",
                        "/books").permitAll()
                .antMatchers(HttpMethod.POST,
                        "/comments", "/library_items", "/quotes", "/books").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.PUT,
                        "/comments/*", "/library_items/*", "/quotes/*", "/books/*").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE,
                        "/comments/*", "/library_items/*", "/quotes/*", "/books/*").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/**").denyAll()
                .and()
                .addFilter(new JwtRequestFilter(authenticationManager(authConfig), converter))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
