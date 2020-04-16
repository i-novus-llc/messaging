package ru.inovus.messaging.n2o;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.meta.control.DefaultValues;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PageBindersTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllDataPack(), new N2oFieldSetsPack(),
                new N2oControlsPack(), new N2oPagesPack(), new N2oCellsPack(),
                new N2oWidgetsPack(), new N2oRegionsPack(), new N2oActionsPack());
        builder.binders(new MessagingFeedPageBinder());
    }

    @Test
    public void testMessagingFeedPageBinder() {
        Page page = bind("ru/inovus/messaging/n2o/messaging_feed.page.xml")
                .get(new PageContext("messaging_feed"), new DataSet());

        assertDates(page, "filter['messaging_feed_feed']");
    }

    @Test
    public void testMessagingHistoryListPageBinder() {
        builder.sources(new CompileInfo("ru/inovus/messaging/n2o/test.query.xml"));
        builder.binders(new MessagingHistoryListPageBinder());
        Page page = bind("ru/inovus/messaging/n2o/messaging_settings.page.xml")
                .get(new PageContext("messaging_settings"), new DataSet());

        assertDates(page, "filter['messaging_settings_history']");
    }

    private void assertDates(Page page, String modelLink) {
        DefaultValues defaults = (DefaultValues) page.getModels().get(modelLink).getValue();
        LocalDateTime begin = (LocalDateTime) ((DataSet) defaults.getValues().get("sentAt")).get("begin");
        LocalDateTime end = (LocalDateTime) ((DataSet) defaults.getValues().get("sentAt")).get("end");

        assertThat(begin.compareTo(end) < 0, is(true));
    }
}