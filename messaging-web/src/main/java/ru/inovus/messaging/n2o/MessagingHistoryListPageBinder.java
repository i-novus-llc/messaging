package ru.inovus.messaging.n2o;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.api.metadata.meta.control.DefaultValues;
import net.n2oapp.framework.config.metadata.compile.BaseMetadataBinder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Класс, который нужен для UI ,  для стр. messaging_message_list, устанавливает дефолтные значения в фильтре 'Дата отправки'
 */
@Component
public class MessagingHistoryListPageBinder implements BaseMetadataBinder<Page> {

    private static final String HISTORY_WIDGET = "__history";
    private static final String MESSAGING_SETTINGS_HISTORY_WIDGET1 = "messagingSettings_history";
    private static final String MESSAGING_SETTINGS_HISTORY_WIDGET2 = "messaging_settings_history";

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }

    @Override
    public Page bind(Page page, BindProcessor bindProcessor) {

        if (!page.getWidgets().containsKey(HISTORY_WIDGET) && !page.getWidgets().containsKey(MESSAGING_SETTINGS_HISTORY_WIDGET1)
                && !page.getWidgets().containsKey(MESSAGING_SETTINGS_HISTORY_WIDGET2))
            return page;

        DataSet data = new DataSet();

        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        data.put("sentAt.begin", begin);
        data.put("sentAt.end", end);

        DefaultValues df = new DefaultValues();
        df.setValues(data);
        ModelLink ml = new ModelLink(df);

        if (page.getWidgets().containsKey(HISTORY_WIDGET)) {
            page.getModels().add(ReduxModel.FILTER, HISTORY_WIDGET, ml);
        } else if (page.getWidgets().containsKey(MESSAGING_SETTINGS_HISTORY_WIDGET1)) {
            page.getModels().add(ReduxModel.FILTER, MESSAGING_SETTINGS_HISTORY_WIDGET1, ml);
        } else {
            page.getModels().add(ReduxModel.FILTER, MESSAGING_SETTINGS_HISTORY_WIDGET2, ml);
        }

        return page;
    }
}
