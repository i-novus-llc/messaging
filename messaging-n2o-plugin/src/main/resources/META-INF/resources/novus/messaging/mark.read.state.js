define([
    'underscore',
    'n2o/controls/action/states/state',
    'n2o/views/widget',
    'infect'
], function (_, StateAction, Widget, Infect) {
    "use strict";

    return StateAction.extend({

        getAction: function () {
            if (this.$action) {
                return this.$action;
            }
            this.$action = this.cmp.createAction('read', 'messaging_message');
            return this.$action;
        },

        listenerAction: function (action, widget) {
            return action &&
                action
                    .done(function (response) {
                        Infect.get('Messaging').requestCount();
                        widget.triggerMethod('onSuccessAction', response);
                    })
                    .fail(function (response) {
                        widget.triggerMethod('onErrorAction', response);
                    })
                    .cancel(function () {
                        widget.triggerMethod('onCancelAction');
                    });
        },

        resolve: function () {
            var action = this.getAction();
            var widget = this.cmp.getWidget();
            action.widget = widget;
            var getSelectModel = this.cmp.getSelectModel();

            this.listenerAction(action, widget);

            // execute action with selectModel widget
            if (widget instanceof Widget) {
                if (this.isContext()) {
                    action.confirm(getSelectModel());
                } else {
                    action.confirm(widget.getDefaultModel());
                }
            } else {
                action.confirm({});
            }

            // start loading timeout in widget.
            // @see {LSDNTWOO-691}
            widget.loading && widget.loading();
        }
    });
});