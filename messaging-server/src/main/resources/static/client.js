(function () {
"use strict";

//+private members

function WindowManager() {
    this.id = new Date().getTime();
    this.isMaster = false;
    this.others = {};

    window.addEventListener( 'storage', this, false );
    window.addEventListener( 'unload', this, false );

    this.broadcast( 'hello' );

    var that = this;
    var check = function check () {
        that.check();
        that._checkTimeout = setTimeout( check, 9000 );
    };
    var ping = function ping () {
        that.sendPing();
        that._pingTimeout = setTimeout( ping, 17000 );
    };
    this._checkTimeout = setTimeout( check, 500 );
    this._pingTimeout  = setTimeout( ping, 17000 );
    }

WindowManager.prototype.destroy = function () {
    clearTimeout( this._pingTimeout );
    clearTimeout( this._checkTimeout );

    window.removeEventListener( 'storage', this, false );
    window.removeEventListener( 'unload', this, false );

    this.broadcast( 'bye' );
};

WindowManager.prototype.handleEvent = function ( event ) {
    if ( event.type === 'unload' ) {
        this.destroy();
    } else if ( event.key === 'broadcast' ) {
        try {
            var data = JSON.parse( event.newValue );
            if ( data.id !== this.id ) {
                this[ data.type ]( data );
            }
        } catch ( error ) {}
    }
};

WindowManager.prototype.sendPing = function () {
    this.broadcast( 'ping' );
};

WindowManager.prototype.hello = function ( event ) {
    this.ping( event );
    if ( event.id < this.id ) {
        this.check();
    } else {
        this.sendPing();
    }
};

WindowManager.prototype.ping = function ( event ) {
    this.others[ event.id ] = +new Date();
};

WindowManager.prototype.bye = function ( event ) {
    delete this.others[ event.id ];
    this.check();
};

WindowManager.prototype.check = function ( event ) {
    var now = +new Date(),
        takeMaster = true,
        id;
    for ( id in this.others ) {
        if ( this.others[ id ] + 23000 < now ) {
            delete this.others[ id ];
        } else if ( id < this.id ) {
            takeMaster = false;
        }
    }
    if ( this.isMaster !== takeMaster ) {
        this.isMaster = takeMaster;
        this.masterDidChange();
    }
};

WindowManager.prototype.masterDidChange = function () {};

WindowManager.prototype.broadcast = function ( type, data ) {
    var event = {
        id: this.id,
        type: type
    };
    for ( var x in data ) {
        event[x] = data[x];
    }
    try {
        localStorage.setItem( 'broadcast', JSON.stringify( event ) );
    } catch ( error ) {}
};

function Client() {
    wm = wm || new WindowManager();
}
Client.prototype.connect = function(opts) {
    var that = this;
    var masterDidChange = function masterDidChange() {
        if (!wm.isMaster)
            return;
        ws = new WebSocket(opts.url);
        ws.onopen = () => {
            ws.send(JSON.stringify({'headers': opts.headers, 'type': 'CONNECT'}));
            that.onopen && that.onopen();
            wm.broadcast('onopen');
        };
        ws.onmessage = (event) => {
            var message = event.data;
            wm.onmessage && wm.onmessage(message);
            wm.broadcast('onmessage', message);
        };
        ws.onclose = (ev) => {
            that.onclose && that.onclose(ev);
            wm.broadcast('onclose');
        };
    }
    wm.onopen = that.onopen || (() => undefined);
    wm.onclose = that.onclose || (() => undefined);
    wm.masterDidChange = masterDidChange;
    masterDidChange();
    wm.onmessage = function (message) {
        that.onmessage(message);
    };
    wm.send = function(message) {
        if (wm.isMaster)
            ws.send(message);
    };
    wm.disconnect = function() {
        if (wm.isMaster) {
            ws && ws.close();
        }
    };
};
Client.prototype.send = function(message) {
    wm.broadcast('send', message);
};
Client.prototype.disconnect = function() {
    wm.isMaster && ws && ws.close();
    wm.broadcast('disconnect');
};

var ws, wm;

//-private members

window.novus = window.novus || {};
window.novus.messaging = window.novus.messaging || {
    Client: Client
  , WindowManager: WindowManager
}

return window.novus.messaging.Client;
})();
