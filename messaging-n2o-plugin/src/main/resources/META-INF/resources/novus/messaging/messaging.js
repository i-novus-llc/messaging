(function() {
  define(['underscore', 'backbone', 'jquery', 'infect'], function(_, Backbone, $, infect) {
    'use strict';
    return Backbone.View.extend({
      DEBUG: false,
      WS_URL: '',
      REST_URL: '',
      SYSTEM_ID: '',
      MAX_VISIBLE_MESSAGE: 7,
      MESSAGES_COLOR: {
        'INFO': '#3276B1',
        'WARNING': '#C79121',
        'ERROR': '#C46A69'
      },
      AUTH_TOKEN_HEADER: 'X-Auth-Token',
      SYSTEM_ID_HEADER: 'X-System-Id',
      dataKey: 'notification_data',
      workerKey: 'worker_key',
      requestCountKey: 'request_count',
      election_key: 'election_is_started',
      worker: false,
      store_interval: null,
      myTabId: '',
      ticker: null,
      checker: null,
      idsToConfirm: [],
      fullscreenConfirmKey: "fullscreenMessageConfirm",
      hintMessageConfirmKey: 'hintConfirm',
      hintMessages: {
        boxes: []
      },
      fullScreenElementId: 'fullscreen-message__wrapper',
      popupElementId: 'pop-message__wrapper',
      className: 'notificationElement',
      initGame: 'startElection',
      events: {
        'click .popup-message__button--close': function(e) {
          localStorage[this.hintMessageConfirmKey] = e.currentTarget.dataset.boxid;
          this.confirmReading(+e.currentTarget.dataset.messageid);
          this.closeHintMessage(+e.currentTarget.dataset.messageid);
        },
        'click .popup-message-miniicons__iconbox': function(e) {
          this.gotoHintMessage(+e.currentTarget.dataset.boxid, +e.currentTarget.dataset.messageid, this);
          this.$el.find('.popup-message-miniicons__iconbox').removeClass('popup-message-miniicons__iconbox--active');
          e.currentTarget.className += ' popup-message-miniicons__iconbox--active';

        },
        'click #fullscreenMessageConfirmButton': function(e) {
          this.confirmAllFullscreenMessages();
        },
        'click .popup-message-miniicons__hide-all': function () {
          this.closeAllMessage();
        }
      },
      initialize: function(target, view) {
        if (this.options.enabled) {
            this.WS_URL = this.options.address.ws + '/ws';
            this.REST_URL = this.options.address.rest;
            this.SYSTEM_ID = this.options.system.id;
            this.render();
            this.clearLocalStorageVars();
            this.setStorageListeners();
            window.application.once('initialized', (function(_this) {
              return function() {
                localStorage[_this.requestCountKey] = '';
                localStorage[_this.requestCountKey] = 'true';
                return _this.runChecker();
              };
            })(this));
        }
      },
      closeAllMessage: function(){
          this.hintMessages.boxes = [];
          return this.renderHintMessages();
      },
      setStorageListeners: function() {
        window.addEventListener('storage', (function(_this) {
          return function(e) {
            if (e.key === _this.requestCountKey && localStorage[_this.requestCountKey] !== '') {
                _this.requestCount();
                return;
            }
            if (e.key === _this.dataKey && localStorage[_this.dataKey] !== '') {
              _this.DEBUG && _this.log('пришло сообщение', e.newValue);
              _this.showMessage(JSON.parse(e.newValue));
              return;
            }
            if (e.key === _this.election_key && localStorage[_this.election_key] === 'true') {
              _this.runElection();
              return;
            }
            if (e.key === _this.fullscreenConfirmKey && localStorage[_this.fullscreenConfirmKey] === 'done') {
              localStorage[_this.fullscreenConfirmKey] = "";
              _this.$el.find('#' + _this.fullScreenElementId).html('');
              return;
            }
            if (e.key === _this.hintMessageConfirmKey && localStorage[_this.hintMessageConfirmKey] !== '') {
              _this.closeHintMessage(+localStorage[_this.hintMessageConfirmKey]);
              localStorage[_this.hintMessageConfirmKey] = "";
              return;
            }
          };
        })(this));
      },
      clearLocalStorageVars: function() {
        this.removeElection();
        localStorage[this.workerKey] = "";
        localStorage[this.dataKey] = "";
        localStorage[this.fullscreenConfirmKey] = "";
        localStorage[this.hintMessageConfirmKey] = "";
      },
      showMessage: function(message) {
        var page = window.application.getActivePage();
        if (page && page.options.id === 'messaging_message_list') {
          var region = page.getRegions()[0];
          if (region) {
              var cont = region.getContainer('main');
              if (cont) {
                  cont.widget().triggerMethod('updateQuery');
              }
          }
        }
        switch (message.alertType) {
          case "BLOCKER":
            this.renderOrAddFullscreenMessage(message);
            break;
          case "POPUP":
            message && this.hintMessages.boxes.push(message);
            this.renderHintMessages();
            break;
          default:
            if (typeof message.count === 'number') {
              this.updateCounter(message);
            }
        }
      },
      updateCounter: function(message) {
        infect.get('Header').updateNotifyCounter(message.count);
      },
      render: function() {
        this.$el.append('<div id="' + this.fullScreenElementId + '"></div>');
        this.$el.append('<div id="' + this.popupElementId + '"></div>');
        $('body').append(this.$el);
        return this;
      },

      renderHintMessages: _.throttle(function(){
        var $hintEl;
        $hintEl = this.$el.find('#' + this.popupElementId);
        $hintEl.html('');

        this.getTpl({
            LENGTH: this.hintMessages.boxes.length,
            boxes: this.hintMessages.boxes.slice(0, this.MAX_VISIBLE_MESSAGE).reverse()
        }, './novus/messaging/hintMessage.html')
            .done((function(_this) {
                return function(out) {
                    var hintMessageElement;
                    hintMessageElement = $(out);
                    hintMessageElement.find('.popup-message-miniicons__iconbox')
                        .last()
                        .addClass('popup-message-miniicons__iconbox--active');
                    return $hintEl.html(hintMessageElement);
                };
            })(this));
      }, 200),

      gotoHintMessage: function(boxid, messageid) {
        this.$el.find('.popup-message-box').css('z-index', '9998');
        this.$el.find('.popup-message-box[data-messageid=' + messageid + ']').css('z-index', '9999');
        var severity = this.hintMessages.boxes.filter(v => v.id == messageid)[0]['severity'];
        this.$el.find('#popup-message-miniicons__hide-all').css('background-color', this.MESSAGES_COLOR[severity]);
        this.$el.find('#popup-message-miniicons__show-more').css('background-color', this.MESSAGES_COLOR[severity]);
      },
      closeHintMessage: function(messageId) {
          this.hintMessages.boxes = this.hintMessages.boxes.filter(v => v.id != messageId);
        return this.renderHintMessages();
      },
      addMessageToFullscreenMessages: function(message) {
        var compiled, src, tmpl;
        src = '<span class="fullscreen-message__title">{caption}</span><p class="fullscreen-message__text" data-messageid="{id}">{text|s}</p>';
        compiled = window.dust.compile(src, 'msg');
        tmpl = window.dust.loadSource(compiled);
        return window.dust.render('msg', message, (function(_this) {
          return function(err, out) {
            return _this.$el.find('.fullscreen-message__container--middle').hide().prepend($(out)).fadeIn('slow');
          };
        })(this));
      },
      renderOrAddFullscreenMessage: function(message) {
        this.idsToConfirm.push(message.id);
        if (document.querySelector('.fullscreen-message')) {
          return this.addMessageToFullscreenMessages(message);
        } else {
          return this.renderFullScreenMessage(message);
        }
      },
      renderFullScreenMessage: function(message) {
        return this.getTpl(message, './novus/messaging/fullscreenMessage.html').done((function(_this) {
          return function(out) {
            var fullscreenMessageElement;
            fullscreenMessageElement = $(out);
            return _this.$el.find('#' + _this.fullScreenElementId).html(fullscreenMessageElement);
          };
        })(this));
      },
      confirmAllFullscreenMessages: function() {
        this.idsToConfirm.forEach((function(_this) {
          return function(messageId) {
            _this.confirmReading(messageId);
            return _this.idsToConfirm.splice(_this.idsToConfirm.indexOf(messageId + ''), 1);
          };
        })(this));
        this.$el.find('#' + this.fullScreenElementId).html('');
        return setTimeout((function(_this) {
          return function() {
            return localStorage[_this.fullscreenConfirmKey] = "done";
          };
        })(this), 1000);
      },
      requestCount: function() {
         this.getUserId().done(function(result) {
            this.ws.send(JSON.stringify({ type: 'COUNT', headers: this.getHeaders(result) }))
         }.bind(this));
      },
      confirmReading: function(messageId) {
        this.getUserId().done(function(result) {
            this.ws.send(JSON.stringify({ type:    'READ'
                                        , message: messageId ? { id : messageId } : null
                                        , headers: this.getHeaders(result) }));
        }.bind(this));
      },
      getHeaders: function(result) {
        var headers = {};
        headers[this.AUTH_TOKEN_HEADER] = result['user.id'];
        headers[this.SYSTEM_ID_HEADER] = this.SYSTEM_ID;
        return headers;
      },
      /*confirmReading: function(msgId, callback) {
        var url;
        url = this.REST_URL + '/messages/' + msgId + '/read';
        return $.post(url).done((function(_this) {
          return function(response) {
            _this.requestCount();
            return _this.DEBUG && _this.log('ok', response);
          };
        })(this)).fail((function(_this) {
          return function(e) {
            return _this.log('error:', e);
          };
        })(this));
      },*/
      openWs: function() {
        if (this.ws) {
          return;
        }
        this.ws = new WebSocket(this.WS_URL);
        this.ws.onopen = (function(_this) {
          return function(e) {
            localStorage['WSaliveTicker'] = Date.now();
            _this.ticker = setInterval(function() {
              localStorage['WSaliveTicker'] = Date.now();
              return localStorage[_this.workerKey] = 'true';
            }, 1000);
            _this.getUserId().done(function(result) {
              _this.ws.send(JSON.stringify({headers : _this.getHeaders(result), type : 'CONNECT'}));
              _this.DEBUG && _this.log('user id sent');
            });
          };
        })(this);
        this.ws.onmessage = (function(_this) {
          return function(e) {
            localStorage[_this.dataKey] = "";
            _this.showMessage(JSON.parse(e.data));
            localStorage[_this.dataKey] = e.data;
            _this.DEBUG && _this.log(e.data);
          };
        })(this);
        this.ws.onclose = (function(_this) {
          return function(e) {
            clearInterval(_this.ticker);
          };
        })(this);
        this.ws.onerror = (function(_this) {
          return function(e) {
            _this.DEBUG && _this.log('Messenger WebSocket error.', e);
          };
        })(this);
        window.addEventListener("beforeunload", (function(_this) {
          return function(e) {
            _this.ws.close();
          };
        })(this), false);
        window.addEventListener('unload', (function(_this) {
          return function(e) {
            _this.ws.close();
          };
        })(this));
      },
      getUserId: function() {
        var def;
        def = $.Deferred();
        $.get('context?param=user.id').done(function(data) {
          def.resolve(data);
        }).fail(function() {
          def.fail();
        });
        return def;
      },
      getTpl: function(value, src) {
        var context, def;
        context = _.extend({}, value);
        def = $.Deferred();
        src = src.match('.html') ? src : src + ".html";
        window.dust.render(src, context, function(err, out) {
          if (err) {
            return def.reject(err);
          } else {
            return def.resolve(out);
          }
        });
        return def;
      },
      socketAlive: function() {
        return (Date.now() - localStorage.getItem('WSaliveTicker')) / 1000 < 4;
      },
      runElection: function() {
        this.betMe();
        setTimeout((function(_this) {
          return function() {
            var max_tab, tabs;
            tabs = _this.getTabsForElection();
            max_tab = _.invert(tabs)[_.max(tabs)];
            if (max_tab === _this.myTabId) {
              _this.openWs();
              localStorage[_this.workerKey] = 'true';
            }
            _this.removeElection();
          };
        })(this), 500);
      },
      runChecker: function() {
        return this.checker = setInterval((function(_this) {
          return function() {
            if (!_this.socketAlive() && localStorage[_this.election_key] !== 'true') {
              _this.DEBUG && _this.log('Сокет просрочился, начинаем выборы!');
              localStorage[_this.election_key] = "true";
              _this.runElection();
            }
          };
        })(this), 2000);
      },
      betMe: function() {
        this.myTabId = 'tab-' + this.guid();
        localStorage[this.myTabId] = Math.random();
        return this.DEBUG && this.log(this.myTabId, localStorage[this.myTabId]);
      },
      getTabsForElection: function() {
        var i, tabs;
        tabs = {};
        i = 0;
        while (i < localStorage.length) {
          if (localStorage.key(i).substring(0, 3) === 'tab') {
            tabs[localStorage.key(i)] = localStorage.getItem(localStorage.key(i));
          }
          i++;
        }
        return tabs;
      },
      removeElection: function() {
        var key, results;
        localStorage[this.election_key] = 'false';
        results = [];
        for (key in localStorage) {
          if (key.substring(0, 3) === 'tab') {
            results.push(localStorage.removeItem(key));
          } else {
            results.push(void 0);
          }
        }
        return results;
      },
      log: function() {
        [].slice.call(arguments).forEach(function(el) {
          if (typeof el === 'string') {
            return console.error('%c' + el, 'font-size: 20px;');
          } else {
            return console.error(el);
          }
        });
      },
      guid: function() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
      }
    });
  });

}).call(this);

//# sourceMappingURL=notification.js.map
