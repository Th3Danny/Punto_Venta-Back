package com.punto_venta.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Desactivar CSRF por el JWT
                .authorizeHttpRequests(auth -> auth
                    // públicos
                    .requestMatchers(HttpMethod.POST, "/users").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/refresh-token").permitAll()

                    .requestMatchers(HttpMethod.GET, "/products", "/products/**").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyRole("ADMIN")
                    // Productos
                    .requestMatchers(HttpMethod.GET, "/products", "/products/**").hasAnyRole("ADMIN","CASHIER","MANAGER")
                    .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN","MANAGER", "GERENTE")
                    .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN","MANAGER", "GERENTE")
                    .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN")

                    // Ventas
                    .requestMatchers(HttpMethod.POST, "/sales").hasAnyRole("ADMIN","MANAGER","CASHIER")
                    .requestMatchers(HttpMethod.GET, "/sales", "/sales/**").hasAnyRole("ADMIN","MANAGER")

                    // Reportes gerenciales (under /sales/reports)
                    .requestMatchers(HttpMethod.GET,"/sales/reports/**").hasAnyRole("ADMIN","MANAGER", "GERENTE")

                    .requestMatchers(HttpMethod.GET, "/users/roles").hasAnyRole("ADMIN")

                    .anyRequest().authenticated()
                );

        // Registrar filtro JWT antes del procesamiento estándar de autenticación por formulario
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}