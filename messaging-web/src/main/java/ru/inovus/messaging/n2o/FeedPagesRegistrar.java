package ru.inovus.messaging.n2o;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.api.register.route.RouteInfo;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import org.springframework.stereotype.Component;

@Component
public class FeedPagesRegistrar implements MetadataPack<N2oApplicationBuilder> {
    @Override
    public void build(N2oApplicationBuilder b) {
        b.routes(new RouteInfo("/feed", new PageContext("messaging_feed", "/feed")));
        b.routes(new RouteInfo("/feed_settings", new PageContext("messaging_user_setting", "/feed_settings")));
    }
}