package ru.inovus.messaging.n2o;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.global.view.page.BasePageUtil;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.control.DefaultValues;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.api.metadata.meta.widget.Widget;
import net.n2oapp.framework.config.metadata.compile.BaseMetadataBinder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс, который нужен для UI ,  для стр. messaging_message_list, устанавливает дефолтные значения в фильтре 'Дата отправки'
 */
@Component
public class MessagingHistoryListPageBinder implements BaseMetadataBinder<Page> {

    private static final String HISTORY_WIDGET = "history";
    private static final String MESSAGING_TEMPLATES_HISTORY_WIDGET1 = "messagingTemplates_history";
    private static final String MESSAGING_TEMPLATES_HISTORY_WIDGET2 = "messaging_templates_history";

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }

    @Override
    public Page bind(Page page, BindProcessor bindProcessor) {
        Map<String, Widget> widgets = new HashMap<>();
        if (page instanceof SimplePage) {
            SimplePage simplePage = (SimplePage) page;
            if (simplePage.getWidget() != null)
                widgets.put(simplePage.getWidget().getId(), simplePage.getWidget());
        } else if (page instanceof StandardPage) {
            StandardPage standardPage = (StandardPage) page;
            widgets = BasePageUtil.getCompiledWidgets(standardPage).stream()
                    .collect(Collectors.toMap(Widget::getId, w -> w));
        }
        if (!widgets.containsKey(HISTORY_WIDGET) && !widgets.containsKey(MESSAGING_TEMPLATES_HISTORY_WIDGET1)
                && !widgets.containsKey(MESSAGING_TEMPLATES_HISTORY_WIDGET2))
            return page;

        DataSet data = new DataSet();

        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        data.put("sentAt.begin", begin);
        data.put("sentAt.end", end);

        DefaultValues df = new DefaultValues();
        df.setValues(data);
        ModelLink ml = new ModelLink(df);

        if (widgets.containsKey(HISTORY_WIDGET)) {
            page.getModels().add(ReduxModel.FILTER, HISTORY_WIDGET, ml);
        } else if (widgets.containsKey(MESSAGING_TEMPLATES_HISTORY_WIDGET1)) {
            page.getModels().add(ReduxModel.FILTER, MESSAGING_TEMPLATES_HISTORY_WIDGET1, ml);
        } else {
            page.getModels().add(ReduxModel.FILTER, MESSAGING_TEMPLATES_HISTORY_WIDGET2, ml);
        }

        return page;
    }
}
