/*global define, require, console */
define([
    'infect',
    'n2o/widgets/form/form'
], function (Infect, Form) {
    "use strict";

    var MessageForm = Form.extend({
        /**
         * Inherit className from base widget and add own.
         */
        className: 'n2o-form-widget n2o-form messaging-message-form',

        inheritEvents: true,

        afterRender: function (options) {
            Form.prototype.afterRender.apply(this, arguments);
            var messageId = this.getMessageId();
            if (messageId)
                Infect.get('Messaging').confirmReading(messageId);
        },

        getMessageId: function() {
            var selectModel = typeof (this.getSelectModel()) === 'function'
                    ? this.getSelectModel()() : this.getSelectModel();
            return selectModel.id;
        },
    });

    return MessageForm;
});
