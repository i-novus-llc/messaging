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
    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }

    @Override
    public Page bind(Page page, BindProcessor bindProcessor) {

        if (!page.getWidgets().containsKey("__history") && !page.getWidgets().containsKey("messagingSettings_history"))
            return page;

        DataSet data = new DataSet();

        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        data.put("sentAt.begin", begin);
        data.put("sentAt.end", end);

        DefaultValues df = new DefaultValues();
        df.setValues(data);
        ModelLink ml = new ModelLink(df);

        if (page.getWidgets().containsKey("__history")) {
            page.getModels().put(String.format("%s['%s']", ReduxModel.FILTER.name().toLowerCase(), page.getWidgets().get("__history").getId()), ml);
        } else {
            page.getModels().put(String.format("%s['%s']", ReduxModel.FILTER.name().toLowerCase(), page.getWidgets().get("messagingSettings_history").getId()), ml);
        }

        return page;
    }
}
