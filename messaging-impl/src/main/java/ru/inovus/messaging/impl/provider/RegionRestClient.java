package ru.inovus.messaging.impl.provider;

import net.n2oapp.security.admin.rest.api.RegionRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "regionRestService", url = "${novus.messaging.recipient-provider.url}")
public interface RegionRestClient extends RegionRestService {
}
