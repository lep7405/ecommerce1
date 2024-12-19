package com.oms.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Áp dụng cho tất cả các endpoint `/api/v1/...`
                .allowedOrigins("http://localhost:5173", "http://localhost:5174") // Cho phép từ origin này
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")// Các phương thức được phép
                .allowCredentials(true); // Cho phép gửi thông tin xác thực (nếu cần)
      }
    };
  }
}
