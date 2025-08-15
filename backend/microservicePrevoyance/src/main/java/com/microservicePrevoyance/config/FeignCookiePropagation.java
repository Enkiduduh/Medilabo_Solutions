package com.microservicePrevoyance.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCookiePropagation {

    private final ObjectProvider<HttpServletRequest> requestProvider;

    public FeignCookiePropagation(ObjectProvider<HttpServletRequest> requestProvider) {
        this.requestProvider = requestProvider;
    }

    @Bean
    public RequestInterceptor cookiePropagator() {
        return template -> {
            var req = requestProvider.getIfAvailable();
            if (req != null) {
                var cookie = req.getHeader("Cookie");
                if (cookie != null) {
                    template.header("Cookie", cookie);
                }
            }
        };
    }
}
