package ru.inovus.messaging.n2o;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FrontendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "SPRING_CONFIG_IMPORT='optional:consul:'" })
public class FrontendApplicationTest {
    @Test
    public void contextLoads() {
    }
}
