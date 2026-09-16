package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.infrastructure.security.RoleAuthorizationInterceptor;
import com.pragma.powerup.infrastructure.security.InternalApiKeyInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final RoleAuthorizationInterceptor roleAuthorizationInterceptor;
    private final InternalApiKeyInterceptor internalApiKeyInterceptor;

    public WebConfiguration(RoleAuthorizationInterceptor roleAuthorizationInterceptor,
            @Value("${internal.api-key}") String apiKey) {
        this.roleAuthorizationInterceptor = roleAuthorizationInterceptor;
        this.internalApiKeyInterceptor = new InternalApiKeyInterceptor(apiKey);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleAuthorizationInterceptor);
        registry.addInterceptor(internalApiKeyInterceptor)
                .addPathPatterns("/users/*/contact");
    }
}
