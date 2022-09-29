package ru.inovus.messaging.n2o;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "novus.messaging.attachment.enabled", havingValue = "true")
public class ProxyConfig {
    @Bean
    public ServletRegistrationBean<ProxyServlet> proxyInputDataServiceServlet(
            @Value("${messaging.backend.path}" + "/attachments") String inputDataUrl) {
        ServletRegistrationBean<ProxyServlet> bean =
                new ServletRegistrationBean<>(new ProxyServlet(), "/proxy/api/attachments/*");
        Map<String, String> params = new HashMap<>();
        params.put("targetUri", inputDataUrl);
        params.put(ProxyServlet.P_LOG, "true");
        params.put(ProxyServlet.P_PRESERVECOOKIES, "true");
        bean.setInitParameters(params);
        return bean;
    }
}