package ru.inovus.messaging.n2o;

import net.n2oapp.framework.engine.data.rest.SpringRestDataProviderEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static java.util.Objects.nonNull;

@Component
public class N2oRestTemplateBeanPostProcessor implements BeanPostProcessor {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //добавляем токен в headers в n2o restTemplate для авторизованных запросов на бэк
        if (beanName.equals("restDataProviderEngine")) {
            SpringRestDataProviderEngine restDataProviderEngine = (SpringRestDataProviderEngine) bean;
            try {
                Field restTemplateField = bean.getClass().getDeclaredField("restTemplate");
                restTemplateField.setAccessible(true);
                RestTemplate restTemplate = (RestTemplate) restTemplateField.get(restDataProviderEngine);
                setToken(restTemplate);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void setToken(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (nonNull(authentication) && authentication.getPrincipal() instanceof DefaultOidcUser) {
                DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
                request.getHeaders().add(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_PREFIX + principal.getIdToken().getTokenValue());
            }
            return execution.execute(request, body);
        });
    }
}