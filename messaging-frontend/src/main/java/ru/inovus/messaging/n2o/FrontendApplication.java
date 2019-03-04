package ru.inovus.messaging.n2o;

import net.n2oapp.framework.api.data.QueryProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
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

