package ru.inovus.messaging.n2o;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrontendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FrontendApplicationTest {
    @Test
    public void contextLoads() {
    }
}
