package ru.inovus.messaging.n2o;

import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.security.admin.rest.client.AdminRestClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@SpringBootApplication
@Import(AdminRestClientConfiguration.class)
public class FrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

    @Autowired
    private QueryProcessor queryProcessor;

    @Bean
    public UserMessageViewPageNameBinder  pageNameBinder() {
        return new UserMessageViewPageNameBinder(queryProcessor);
    }
}

