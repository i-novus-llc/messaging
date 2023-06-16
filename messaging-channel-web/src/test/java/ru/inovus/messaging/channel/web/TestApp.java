package ru.inovus.messaging.channel.web;

import net.n2oapp.framework.api.ui.AlertMessageBuilder;
import net.n2oapp.framework.boot.*;
import net.n2oapp.platform.jaxrs.autoconfigure.JaxRsServerAutoConfiguration;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.inovus.messaging.channel.web.config.EmbeddedKafkaTestConfiguration;
import ru.inovus.messaging.channel.web.config.WebSecurityTestConfiguration;
import ru.inovus.messaging.channel.web.configuration.EnableWebChannel;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        KafkaAutoConfiguration.class,
        JaxRsServerAutoConfiguration.class,
        CxfAutoConfiguration.class, N2oFrameworkAutoConfiguration.class})
@Import({WebSecurityTestConfiguration.class, EmbeddedKafkaTestConfiguration.class, N2oMessagesConfiguration.class,
        N2oContextConfiguration.class,
        N2oEnvironmentConfiguration.class,
        N2oEngineConfiguration.class,
        N2oMetadataConfiguration.class})
@EnableWebSecurity
@EnableWebChannel
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @Bean
    AlertMessageBuilder messageBuilder(@Qualifier("n2oMessageSourceAccessor") MessageSourceAccessor messageSourceAccessor,
                                       PropertyResolver propertyResolver) {
        return new AlertMessageBuilder(messageSourceAccessor, propertyResolver);
    }
}
