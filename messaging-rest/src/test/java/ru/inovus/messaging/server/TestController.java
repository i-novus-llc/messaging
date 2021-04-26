package ru.inovus.messaging.server;

import net.n2oapp.security.admin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {
    @GetMapping("/users")
    public Page<User> users(HttpServletRequest request) {
        if (!"Bearer test_token".equals(request.getHeader("Authorization"))) {
            throw new IllegalStateException();
        }
        return Page.empty();
    }
}
