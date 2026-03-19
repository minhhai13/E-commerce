package com.group2.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ đường dẫn URL /images/ vào thư mục thực tế trong project
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:src/main/resources/static/images/");
    }
}