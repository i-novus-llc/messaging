package ru.inovus.messaging.server;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import org.springframework.context.annotation.Configuration;
import ru.i_novus.domrf.lkb.access.api.EmployeeBankService;
import ru.i_novus.domrf.lkb.access.api.EmployeeDomrfService;

@Configuration
@EnableJaxRsProxyClient(
    classes = {EmployeeBankService.class, EmployeeDomrfService.class},
    address = "${access.rest}")
public class MessagingSecurityAdminClientConfiguration {
}
