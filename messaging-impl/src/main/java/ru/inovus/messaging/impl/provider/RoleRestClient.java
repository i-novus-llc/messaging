package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.RoleRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "roleRestService", url = "${novus.messaging.recipient-provider.url}")
public interface RoleRestClient extends RoleRestService {
}
