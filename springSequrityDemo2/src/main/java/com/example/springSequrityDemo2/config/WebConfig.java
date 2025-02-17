package com.example.springSequrityDemo2.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Apply to all endpoints
                .allowedOrigins("http://localhost:3000")  // Frontend URL (Next.js)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")  // HTTP methods
                .allowedHeaders("Authorization", "Content-Type")  // Headers to allow
                .allowCredentials(true);  // Allow cookies or JWT tokens if necessary
    }
}

