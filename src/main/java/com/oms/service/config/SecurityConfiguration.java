
package com.oms.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;

    private static final String[] Lists = {
            "/api/v1/users",
            "/api/v1/super_admin/**",
            "/api/v1/users/**",
            "/api/v1/orders/**",
            "/api/v1/filters/**",
            "/api/v1/users/login",
            "/api/v1/products/**",
            "/api/v1/brands/**",
            "/api/v1/image/**",
            "/api/v1/users/refresh-token",
            "/api/v1/permission/**",
            "/api/v1/admins/login",
            "/api/v1/category/**",
            "/api/v1/discounts/**",
            "/login/oauth2/code/google",
            "/user/refreshToken",
            "/login", "/oauth2/**",
            "/product/getProduct/**",
            "/product/getAllProduct",
            "/admin/Login",
            "/product/test-redis",
            "/product/testPro/**",
            "/product/**",
            "/cart/clear",
            "/payment/**",
            "/order/getOrder1/**",
            "/order/getOrder1Item/**",
            "/shipping/getShippingById/**",
            "/admin/getInfoAdmin"

    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.antMatchers(Lists).permitAll()
                                .antMatchers(HttpMethod.POST, "/api/v1/role").hasRole("SUPER_ADMIN")
                                .antMatchers(HttpMethod.POST, "/api/v1/admins").hasRole("SUPER_ADMIN")
                                .anyRequest().authenticated()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }
}
