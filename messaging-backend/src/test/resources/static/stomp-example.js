// Пример stomp клиента

var stompClient = null;
var token = null;
var systemID = 'sysId9';

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
    token = document.getElementById('token');

    var socket = new SockJS('/ws/?access_token=' + token.value);
    stompClient = Stomp.over(socket);
    stompClient.connect({}
        , function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);

            // var url = stompClient.ws._transport.url;
            // url = url.substring(0, url.indexOf("/websocket"));
            // url = url.substring(url.lastIndexOf("/") + 1, url.length);
            // console.log("Your current session is: " + url);

            //Подписка на личные сообщения
            stompClient.subscribe('/user/exchange/' + systemID + '/message'
                , function (msg) {
                    console.log(msg);
                    showMessage(JSON.parse(msg.body), 'color:red');
                }
            );

            //Подписка на счетчик
            stompClient.subscribe('/user/exchange/' + systemID + '/message.count'
                , function (msg) {
                    console.log(msg);
                }
            );

            getCount();
            sendMarkRead('["id1","id2","id3"]');

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


//отправка личного сообщения
function sendPriv() {
    if (stompClient && stompClient.connected) {
        var name = document.getElementById('name').value;
        var message = document.getElementById('message').value;
        stompClient.send("/app/" + systemID + "/message.private." + name, {}, JSON.stringify({
            'id': name,
            'text': message
        }));
    }
}

//Запросить кол-во пропущенных сообщений
function getCount() {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/' + systemID + '/message.count');
    }
}

//Отметить все сообщения прочитанными
function sendMarkReadAll() {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/' + systemID + '/message.markreadall');
    }
}

//Отметить определенные сообщения прочитанными ids = JSON.stringify(["id1","id2","id3"])
function sendMarkRead(ids) {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/' + systemID + '/message.markread', {}, ids);
    }
}

function showMessage(message, stat) {
    console.log(message);
    var tb = document.getElementById('messages'),
        tr = document.createElement('tr'),
        text = document.createElement('tr'),
        caption = document.createElement('td');
    caption.textContent = message.id;
    text.textContent = message.text;
    caption.setAttribute("style", stat);
    tr.append(caption);
    tr.append(text);
    tb.append(tr);
}
