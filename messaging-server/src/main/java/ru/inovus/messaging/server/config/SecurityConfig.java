package ru.inovus.messaging.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableResourceServer
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "security", value = "enabled", havingValue = "true")
public class SecurityConfig extends ResourceServerConfigurerAdapter {

    private final ResourceServerProperties resourceServerProperties;

    public SecurityConfig(ResourceServerProperties resourceServerProperties) {
        this.resourceServerProperties = resourceServerProperties;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceServerProperties.getResourceId());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin.html", "/actuator/**")
                .authenticated()
                .antMatchers("/api/**")
                .authenticated()
                .anyRequest().permitAll();
//        http.logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login");
//        http.formLogin();
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOrigin(CorsConfiguration.ALL);
        cors.addAllowedHeader(CorsConfiguration.ALL);
        cors.setAllowedMethods(Arrays.asList("GET", "POST", "OPTION"));
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter);
    }

//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi54+TgAUWz3rUrAqdjBcmw6lueJdDcDh5ymU3uuNJsBOTz4mI3lFle505/xta6mpaMZynoS9Nbs6HJswWScCNilc97GFpFpn1KMYV0Ct8Mk0R2gEBk3ky+ASjhMXdd0UIhFywHivU0eaVQVXLfFFg68b/MK4NyJTR33pgUy7VZTtup+h8UFWzVuKu1tfrI6rVe6o2biKM+z258uWXxbEI08DyBTyvAL+GKb0HY1G59BQ/6rziYCVDSO3EQgb+rL4ZNmlVb13W0ePfyWUKnBPcmk9TRejaYmYSZiwQjDgx9+1yBCEeDPPt/LyVAac2Bd5Vq2VVJfz7bUf2TTwT9hBdQIDAQAB");
//        return converter;
//    }

//    @Bean
//    public JwtAccessTokenCustomizer jwtAccessTokenCustomizer(ObjectMapper mapper) {
//        return new JwtAccessTokenCustomizer(mapper);
//    }
}
