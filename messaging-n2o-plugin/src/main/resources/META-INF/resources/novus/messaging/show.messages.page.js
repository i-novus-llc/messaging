define([
    'underscore',
    'n2o/controls/action/states/show.modal.state',
    'n2o/views/open.page',
    'n2o/core/utils/eval.resolution',
    'infect'
], function (_, ModalAction, OpenPage, EvalResolution, Infect) {
    "use strict";

    return ModalAction.extend({
        getContainerId: function () {
            return 'main';
        },
        getPageId: function () {
            return 'messaging_message';
        },
        isRefreshOnClose: function () {
            return true;
        },

        getMessageId: function() {
            var selectModel = typeof (this.cmp.getSelectModel()) === 'function'
                    ? this.cmp.getSelectModel()() : this.cmp.getSelectModel();
            return selectModel.id;
        },

        resolve: function () {
            var widget = this.cmp.getWidget();
            var externalMemento = this.createExternalMemento();
            var pageId = this.getPageId();
            var messageId = this.getMessageId();

            OpenPage
                .createOpenPageLayout(pageId, widget, { externalMemento: externalMemento })
                .done(function (layout) {
                    layout.on('closed', function () {
                        if (this.isRefreshOnClose()) {
                            return widget.updateQuery({
                                source: "close",
                                refreshDependentContainer: true,
                                refreshPolity: this.eventMetadata && this.eventMetadata.refreshPolity
                            }).done(function () {
                                widget.restoreFocus();
                                Infect.get('Messaging').confirmReading(messageId);
                            });
                        }
                        widget.restoreFocus();
                    }.bind(this));
                }.bind(this));
        }
    });
});