// Пример stomp клиента

var stompClient = null;

function setConnected(val) {
    var btnConn = document.getElementById('connect'),
        btnDisconn = document.getElementById('disconnect'),
        inpName = document.getElementById('name');
    if (val) {
        btnConn.setAttribute('disabled', '');
        btnDisconn.removeAttribute('disabled');
    } else {
        btnDisconn.setAttribute('disabled', '');
        if (inpName.value) {
            btnConn.removeAttribute('disabled');
        }
    }
}

function connectWS() {
    var token = document.getElementById('token');
    var socket = new SockJS('/ws/?access_token=' + token.value);
    stompClient = Stomp.over(socket);
    stompClient.connect({}
        , function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);

            //Подписка на общие сообщения
            stompClient.subscribe('/topic/chat.message', function (greeting) {
                showMessage(JSON.parse(greeting.body), 'color:green');
            });

            // var url = stompClient.ws._transport.url;
            // url = url.substring(0, url.indexOf("/websocket"));
            // url = url.substring(url.lastIndexOf("/") + 1, url.length);
            // console.log("Your current session is: " + url);

            //Подписка на личные сообщения
            stompClient.subscribe('/user/exchange/amq.direct/chat.message'
                , function (msg) {
                    showMessage(JSON.parse(msg.body), 'color:red');
                }
            );

        }, function (errorCallback) {
            console.log(errorCallback);
        }
    );
}

function disconnectWS() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

//отправка сообщения всем
function sendPub() {
    if(stompClient && stompClient.connected) {
        var message = document.getElementById('message').value;
        stompClient.send("/app/chat.message", {}, JSON.stringify({'username': null, 'message': message}));
    }
}

//отправка личного сообщения
function sendPriv() {
    if(stompClient && stompClient.connected) {
        var name = document.getElementById('name').value;
        var message = document.getElementById('message').value;
        stompClient.send("/app/chat.private." + name, {}, JSON.stringify({'username': null, 'message': message}));
    }
}

function showMessage(message, stat) {
    console.log(message);
    var tb = document.getElementById('messages'),
        tr = document.createElement('tr'),
        text = document.createElement('tr'),
        caption = document.createElement('td');
    caption.textContent = message.username;
    text.textContent = message.message;
    caption.setAttribute("style", stat);
    tr.append(caption);
    tr.append(text);
    tb.append(tr);
}
