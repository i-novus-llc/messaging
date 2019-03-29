package ru.inovus.messaging.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.inovus.messaging.server.config.DateMapperConfigurer;

@SpringBootApplication()
@EnableTransactionManagement
@ComponentScan({"ru.inovus.messaging"})
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    DateMapperConfigurer dateMapperConfigurer() {
        return new DateMapperConfigurer();
    }
}
