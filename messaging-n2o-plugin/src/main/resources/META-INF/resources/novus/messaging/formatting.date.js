define([], function() {
    var IN_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSS';
    return {
        utcToLocal: function(value, format) {
            return moment.utc(value, IN_FORMAT).tz(moment.tz.guess()).format(format);
        }
    };
});