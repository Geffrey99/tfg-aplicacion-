package com.webG.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    //TODO //Permitir por roles ADMIN O USER PERO MUCHAS RUTAS...Y ME DA MUCHO PROBLEMA
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Desactiva CSRF
//                .cors(cors -> {}) // Configura CORS (debe estar definido con un CorsFilter si es necesario)
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Desactiva sesiones
//                )
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "register", "/encriptar-contra",
//                                "/api/usuario/**").permitAll() // Permitir acceso a rutas sin autenticación
//                        // Rutas habilitadas solo para usuarios con el rol ADMIN
//                        .requestMatchers("/api/products/**", "/api/products/create").hasAuthority("ROLE_Admin")
//
//
//                        // Rutas habilitadas solo para usuarios con el rol USER
//                        .requestMatchers("/app-user/**", "/api/usuario/**", "/api/categories/**", "/api/products/**","/api/categories").hasRole("User") // USER y ADMIN
//
//
//                        .anyRequest().authenticated() // El resto requiere autenticación
//
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // ← ¡AQUÍ!
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF si no es necesario
                .cors(cors -> {}) // Configura CORS (si es necesario)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin estado para las conexiones
                )
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll() // Permite todas las solicitudes sin autenticación
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Si necesitas JWT (podrías quitarlo).

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Dirección del frontend
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

