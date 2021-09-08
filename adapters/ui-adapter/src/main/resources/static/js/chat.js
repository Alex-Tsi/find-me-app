let stompClient = null;

console.log()
if (currentUserId > 0) {

    function connect() {
        let socket = new SockJS('/messenger');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/' + currentUserId + "/messages", function (message) {
                showMessage(JSON.parse(message.body));
            });
        });
    }

    connect();
}

function sendMessage() {
    console.log(currentUserId + " " + recipientId)
    let message = {
        content: $("#content").val(),
        senderId: currentUserId,
        recipientId: recipientId,
        senderName: currentName.toString(),
        recipientName: recipientName.toString()
    };
    stompClient.send("/app/send", {}, JSON.stringify(message));
}

function showMessage(message) {
    if (message != null && message.senderId === companionId) {
        $("#showMessage").append(
            '<div class="incoming_msg">\n' +
            '                                <div class="incoming_msg_img"><img\n' +
            '                                            src="/img/' + companionProfileAvatar.valueOf() + '"' +
            '                                            alt="sunil"></div>\n' +
            '                                <div class="received_msg">\n' +
            '                                    <div class="received_withd_msg">\n' +
            '                                        <p>' + message.content + '</p>\n' +
            '                                        <span class="time_date"> 11:01 AM    |    June 9</span></div>\n' +
            '                                </div>\n' +
            '                            </div>'
        );
    } else {
        $("#showMessage").append('' +
            '<div class="outgoing_msg">\n' +
            '                                    <div class="sent_msg">\n' +
            '                                        <p>' + $("#content").val() + '</p>\n' +
            '                                        <span class="time_date"> 11:01 AM    |    June 9</span></div>\n' +
            '                                </div>'
        )
    }
}

$(function () {
    $("#preventDefaultOnForm").on('submit', function (e) {
        e.preventDefault();
    });
    $("#send").click(function () {
        sendMessage();
        showMessage(null);
    });
});
