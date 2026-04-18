package com.sofka.cotizador.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IfMatchHeaderInterceptor())
                .addPathPatterns("/api/v1/quotes/**")
                .excludePathPatterns("/api/v1/quotes/*/locations/summary");
    }
}
