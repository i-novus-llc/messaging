package ru.inovus.messaging.server.config;

import org.apache.cxf.rt.security.crypto.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {

    //можно взять https://keycloak.i-novus.ru/auth/realms/DOMRF/protocol/openid-connect/certs
    @Value("${novus.messaging.keycloak.modulus}")
    private String modulus;
    @Value("${novus.messaging.keycloak.exponent}")
    private String exponent;
    @Value("${novus.messaging.keycloak.resourceId}")
    private String resourceId;

    @Value("${novus.messaging.check_token_expired}")
    private Boolean checkTokenExpired;

    @Value("${novus.messaging.username.alias}")
    private String usernameAlias;

    @Autowired
    private ResourceServerTokenServices tokenServices;

    @Bean
    @Primary
    public ResourceServerTokenServices tokenServices() {
        final TokenStore tokenStore = tokenStore();
        DefaultTokenServices defaultTokenServices;
        if (Boolean.FALSE.equals(checkTokenExpired)) { //Отключена проверка accessToken.isExpired
            defaultTokenServices = new DefaultTokenServices() {
                @Override
                public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
                    OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
                    if (accessToken == null) {
                        throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
                    }
                    OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
                    if (result == null) {
                        throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
                    }
                    return result;
                }
            };
        } else {
            defaultTokenServices = new DefaultTokenServices();
        }
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServices);
        resources.resourceId(resourceId);
        resources.authenticationDetailsSource(new OAuth2AuthenticationDetailsSource());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers("/ws/**")
                .and()
                .authorizeRequests()
                .antMatchers("/ws/**", "/api/users**").authenticated()
                .antMatchers("/api/**").permitAll()
                .anyRequest().denyAll();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .csrfTokenRepository(
                        CookieCsrfTokenRepository.withHttpOnlyFalse());
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

    @Bean JaxRsJwtHeaderInterceptor jaxRsJwtHeaderInterceptor() {
        return new JaxRsJwtHeaderInterceptor();
    }

    private TokenStore tokenStore(){
        return new JwtTokenStore(accessTokenConverter());
    }

    private JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        ((DefaultAccessTokenConverter) converter.getAccessTokenConverter())
                .setUserTokenConverter(userAuthenticationConverter());

        RSAPublicKey publicKey = CryptoUtils.getRSAPublicKey(modulus, exponent);
        converter.setVerifier(new RsaVerifier(publicKey));
        return converter;
    }

    private UserAuthenticationConverter userAuthenticationConverter() {
        return new DefaultUserAuthenticationConverter() {
            @Override
            public Authentication extractAuthentication(Map<String, ?> map) {
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                Object obRole = map.get("roles");
                if (obRole instanceof List) {
                    for (Object roleCode : (List) obRole) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
                    }
                }
                UserDetails principal = new User("" + map.get(usernameAlias), "N/A", authorities);
                return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            }
        };
    }

}
