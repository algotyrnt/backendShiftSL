package com.shiftsl.shiftslbackend.config;

import com.shiftsl.shiftslbackend.filter.FirebaseAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FirebaseAuthFilter> loggingFilter() {
        FilterRegistrationBean<FirebaseAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseAuthFilter());
        registrationBean.addUrlPatterns("/api/*"); // Apply the filter to specific URL patterns
        return registrationBean;
    }
}
