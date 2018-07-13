define([
    'underscore',
    'n2o/controls/action/states/state',
    'n2o/views/widget',
    'infect'
], function (_, StateAction, Widget, Infect) {
    "use strict";

    return StateAction.extend({
        resolve: function () {
            var messageId;
            if (!this.eventMetadata.properties || !this.eventMetadata.properties['all'])
                messageId = this.cmp.getWidget().getSelectModel()['id'];
            Infect.get('Messaging').confirmReading(messageId);
        }
    });
});
