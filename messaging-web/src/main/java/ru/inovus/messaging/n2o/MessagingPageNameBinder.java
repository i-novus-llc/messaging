package ru.inovus.messaging.n2o;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.criteria.N2oPreparedCriteria;
import net.n2oapp.framework.api.criteria.Restriction;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.aware.CompiledClassAware;
import net.n2oapp.framework.api.metadata.compile.BindProcessor;
import net.n2oapp.framework.api.metadata.compile.MetadataBinder;
import net.n2oapp.framework.api.metadata.global.view.page.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class MessagingPageNameBinder implements MetadataBinder<Page>, CompiledClassAware {

    private final String widgetId;
    private final String pageId;

    @Autowired
    @Lazy
    private QueryProcessor queryProcessor;

    public MessagingPageNameBinder(String widgetId, String pageId) {
        this.widgetId = widgetId;
        this.pageId = pageId;
    }

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }

    @Override
    public Page bind(Page page, BindProcessor p) {
        N2oWidget widget = null;
        String widgetId = this.widgetId;
        if (pageId != null) {
            N2oPage n2oPage = p.getSource(pageId, N2oPage.class);
            if (n2oPage.getWidgets() != null) {
                widget = n2oPage.getWidgets().stream()
                        .filter(w -> w.getId().equals(this.widgetId))
                        .findFirst()
                        .orElse(null);
            }
            widgetId = pageId + "_" + this.widgetId;
        } else {
            widget = p.getSource(widgetId, N2oWidget.class);
        }
        if (widget == null) {
            throw new N2oException(String.format("Widget not found. PageId: %s, widgetId: %s", pageId, this.widgetId));
        }
        N2oPreparedCriteria criteria = new N2oPreparedCriteria();
        criteria.setSize(1);
        String id = p.resolveText("{id}", new ModelLink(ReduxModel.RESOLVE, widgetId));
        if (id == null || id.isEmpty() || id.equals("{id}"))
            return page;
        criteria.addRestriction(new Restriction("id", id));
        String queryId = widget.getQueryId();
        DataSet data = queryProcessor.executeOneSizeQuery(p.getCompiled(new QueryContext(queryId)), criteria)
                .getCollection().iterator().next();

        final String title = page.getPageProperty().getTitle();

        if(!StringUtils.isEmpty(title)){
            page.getPageProperty().setTitle(StringUtils.resolveLinks(title, data));
        }

        final String htmlTitle = page.getPageProperty().getHtmlTitle();

        if(!StringUtils.isEmpty(htmlTitle)){
            page.getPageProperty().setHtmlTitle(StringUtils.resolveLinks(htmlTitle, data));
        }

        final String headerTitle = page.getPageProperty().getHeaderTitle();

        if(!StringUtils.isEmpty(headerTitle)){
            page.getPageProperty().setHeaderTitle(StringUtils.resolveLinks(headerTitle, data));
        }

        page.getBreadcrumb().forEach(c -> {

            if (!StringUtils.isEmpty(c.getLabel())) {

                final String label = StringUtils.resolveLinks(c.getLabel(), data);

                c.setLabel(label);

                if (StringUtils.isEmpty(page.getPageProperty().getHtmlTitle())) {
                    page.getPageProperty().setHtmlTitle(label);
                }

                if (StringUtils.isEmpty(page.getPageProperty().getHeaderTitle())) {
                    page.getPageProperty().setHeaderTitle(label);
                }
            }
        });

        return page;

    }
}
