package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "organizationRestService", url = "${novus.messaging.recipient-provider.url}")
public interface OrganizationRestClient extends OrganizationRestService {
}
