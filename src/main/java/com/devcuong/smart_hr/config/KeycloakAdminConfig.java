package com.devcuong.smart_hr.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.username}")
    private String username;

    @Value("${keycloak.credentials.password}")
    private String password;

    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder
                .builder()
                .grantType(OAuth2Constants.PASSWORD)
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                //            .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }
}
