package com.example.learning.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

@Configuration
@EnableWebSecurity
public class DynamicRealmConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static final Logger logger = LoggerFactory.getLogger(DynamicRealmConfig.class);
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/client/**").authenticated()
                        .requestMatchers("/service/**").authenticated()
                        .requestMatchers("/api").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(dynamicEntryPoint())  // ← KEY!
                )
                .sessionManagement(session -> session
                        .sessionConcurrency(concurrency -> concurrency     // ✅ CORRECT method
                                .maximumSessions(1)                            // One session per realm
                                .maxSessionsPreventsLogin(false)  )
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .authorizationRequestResolver(dynamicRealmResolver())
                        )
                );
        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();  // Tracks sessions by principal + realm
    }

    @Bean
    public AuthenticationEntryPoint dynamicEntryPoint() {
        return (request, response, authException) -> {
            HttpServletRequest req = (HttpServletRequest) request;
            String path = req.getRequestURI();
            logger.info("Path url is: {}",path);

            String redirectUrl = path.contains("/client")
                    ? "/oauth2/authorization/client-realm"
                    : "/oauth2/authorization/service-realm";


            logger.info("Redirect url is: {}",redirectUrl);
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public OAuth2AuthorizationRequestResolver dynamicRealmResolver() {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {  // ✅ FULL INTERFACE IMPLEMENTATION
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                return defaultResolver.resolve(request);  // Delegate to default
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                String path = request.getRequestURI();
                logger.info("path is : " + path);
                // ✅ Dynamic realm selection
                String registrationId = path.contains("/client") ? "client-realm" : "service-realm";

                logger.info("RegistrationID is : " + registrationId);
                return defaultResolver.resolve(request, registrationId);
            }
        };
    }
}
