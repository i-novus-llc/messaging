package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "userRestClient", url = "${novus.messaging.security-admin-url}")
public interface UserRestClient extends UserRestService {
}
