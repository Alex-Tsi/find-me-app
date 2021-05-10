<#import "../macroses/common.ftlh" as c>
<#include "../security/user.ftlh">
<head>
    <script>
        const currentUserId = ${currentUserId}
        const currentName = '${name}'
        <#if companion??>
        let companionId = ${companion.id}
        let companionProfileAvatar = '${companion.profile.avatar}'
        let recipientId = ${companion.id}
        let recipientName = '${companion.username}'
        <#else>
        let companionId = 0
        </#if>
    </script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
    <script src="https://bootstraptema.ru/plugins/jquery/jquery-1.11.3.min.js"></script>
    <script src="https://bootstraptema.ru/plugins/2015/b-v3-3-6/bootstrap.min.js"></script>
    <#include "../macroses/imports.ftlh">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" type="text/css"
          rel="stylesheet">
    <link href="/static/css/chat.css" type="text/css" rel="stylesheet">
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

</head>
<@c.page>
    <div class="container">
        <div class="messaging">
            <div class="inbox_msg">
                <div class="inbox_people">
                    <div class="headind_srch">
                        <div class="recent_heading">
                            <h4 id="">Сообщения</h4>
                        </div>
                    </div>
                    <div class="inbox_chat">
                        <#list users as user>
                            <form class="chat_list d-block m-1" action="/select-room" method="get">
                                <input type="hidden" name="currentUser" value="${currentUserId}">
                                <input type="hidden" name="companionUser" value="${user.id}">
                                <button class="border-0" type="submit">
                                <span class="chat_people">
                                            <span class="chat_img"><img
                                                        src="/img/${user.profile.avatar!""}"
                                                        alt="КОТ"></span>
                                            <span class="chat_ib">
                                                <h5>${user.username} <span class="chat_date">Dec 25</span></h5>
                                                <p>Test, which is a new approach to have all solutions
                                                    astrology under one roof.</p>
                                            </span>
                                        </span>
                                </button>
                            </form>
                        </#list>
                    </div>
                </div>
                <div class="mesgs max-size" style="overflow: scroll">
                    <#if (room.id > 0)>
                        <#list room.messages as message>
                            <#if message.recipientId == currentUserId>
                                <div class="incoming_msg">
                                    <div class="incoming_msg_img"><img
                                                src="/img/${companion.profile.avatar!""}"
                                                alt="sunil"></div>
                                    <div class="received_msg">
                                        <div class="received_withd_msg">
                                            <p>${message.content}</p>
                                            <span class="time_date"> 11:01 AM    |    June 9</span></div>
                                    </div>
                                </div>
                            <#else>
                                <div class="outgoing_msg">
                                    <div class="sent_msg">
                                        <p>${message.content}</p>
                                        <span class="time_date"> 11:01 AM    |    June 9</span></div>
                                </div>
                            </#if>
                        </#list>
                        <div id="showMessage">

                        </div>

                        <div class="type_msg">
                            <div class="input_msg_write">
                                <form id="preventDefaultOnForm" class="form-inline">
                                    <input id="content" type="text" class="write_msg" placeholder="Type a message"/>
                                    <button id="send" class="msg_send_btn" type="button"><i class="fa fa-paper-plane-o"
                                                                                  aria-hidden="true"></i></button>
                                </form>
                            </div>
                        </div>
                    <#else>
                        <div class=" align-content-center badge bg-primary text-wrap fs-3 ">
                            Выберите собеседника, чтобы начать диалог
                        </div>
                    </#if>

                </div>

            </div>

        </div>
    </div>
    <p id="response"></p>
</@c.page>