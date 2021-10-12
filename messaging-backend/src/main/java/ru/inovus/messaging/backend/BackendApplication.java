package ru.inovus.messaging.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.inovus.messaging.channel.email.EnableEmailChannel;
import ru.inovus.messaging.channel.web.configuration.EnableWebChannel;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableTransactionManagement
@ComponentScan({"ru.inovus.messaging"})
@PropertySource({"classpath:rest.properties"})
@EnableWebChannel
@EnableEmailChannel
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}