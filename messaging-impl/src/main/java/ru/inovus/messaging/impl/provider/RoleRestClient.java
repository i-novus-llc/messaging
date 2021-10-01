package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.RoleRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "roleRestClient", url = "${novus.messaging.security-admin-url}")
public interface RoleRestClient extends RoleRestService {
}
