<#import "../macroses/common.ftlh" as c>
<#include "../security/user.ftlh">
<head>
    <script>
        let recipientId = ${profile.user.id}
        let currentUserId = ${currentUserId}
        let currentName = "${name}"
        let recipientName = "${profile.user.username}"
    </script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
    <script src="https://bootstraptema.ru/plugins/jquery/jquery-1.11.3.min.js"></script>
    <script src="https://bootstraptema.ru/plugins/2015/b-v3-3-6/bootstrap.min.js"></script>
    <#include "../macroses/imports.ftlh">
    <style>
        body {
            background: url(https://bootstraptema.ru/images/bg/bg-1.png)
        }

        #main {
            background-color: #f2f2f2;
            padding: 20px;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            -ms-border-radius: 4px;
            -o-border-radius: 4px;
            border-radius: 4px;
            border-bottom: 4px solid #ddd;
        }

        #real-estates-detail #author img {
            -webkit-border-radius: 100%;
            -moz-border-radius: 100%;
            -ms-border-radius: 100%;
            -o-border-radius: 100%;
            border-radius: 100%;
            border: 5px solid #ecf0f1;
            margin-bottom: 10px;
        }

        #real-estates-detail .sosmed-author i.fa {
            width: 30px;
            height: 30px;
            border: 2px solid #bdc3c7;
            color: #bdc3c7;
            padding-top: 6px;
            margin-top: 10px;
        }

        .panel-default .panel-heading {
            background-color: #fff;
        }

        #real-estates-detail .slides li img {
            height: 450px;
        }
    </style>
</head>
<@c.page>




    <br><br><br>



    <div class="container">
        <div id="main">
            <div class="row" id="real-estates-detail">
                <div class="col-lg-4 col-md-4">
                    <div class="panel">
                        <div class="panel-heading">
                            <header class="panel-title">
                                <div class="text-center">
                                    <strong class="text-info">Пользователь сайта</strong>
                                </div>
                            </header>
                        </div>
                        <div class="panel-body">
                            <div class="text-center" id="author">
                                <img class="img-thumbnail" src="/img/${profile.avatar!""}">
                                <h3>${profile.firstName!""} ${profile.lastName!""}</h3>
                                <small class="label badge">${profile.country!""}</small>
                                <p>${profile.description!""}</p>
                                <p class="sosmed-author">
                                    <a href="#"><i class="fa fa-facebook" title="Facebook"></i></a>
                                    <a href="#"><i class="fa fa-twitter" title="Twitter"></i></a>
                                    <a href="#"><i class="fa fa-google-plus" title="Google Plus"></i></a>
                                    <a href="#"><i class="fa fa-linkedin" title="Linkedin"></i></a>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-8 col-md-8">
                    <div>
                        <hr>
                        <div>
                            <h4>Данные пользователя</h4>
                            <table class="table">
                                <tbody>
                                <tr>
                                    <td>Зарегистрирован:</td>
                                    <td>${profile.registrationDate!""}</td>
                                </tr>
                                <tr>
                                    <td>Страна:</td>
                                    <td>${profile.country!""}</td>
                                </tr>
                                <tr>
                                    <td>Город:</td>
                                    <td>${profile.city!""}</td>
                                </tr>

                                <tr>
                                    <td>Адрес:</td>
                                    <td>${profile.address!""}</td>
                                </tr>

                                <tr>
                                    <td>Пол:</td>
                                    <td>${profile.sex!""}</td>
                                </tr>
                                <tr>
                                    <td>Полных лет:</td>
                                    <td>${profile.age!""}</td>
                                </tr>

                                <tr>
                                    <td>Умения:</td>
                                    <td>${profile.skills!""}</td>
                                </tr>
                                <tr>
                                    <td>Почта:</td>
                                    <td>${profile.email!""}</td>
                                </tr>
                                <tr>
                                    <td>Контактный номер телефона:</td>
                                    <td>${profile.phoneNumber!""}</td>
                                </tr>
                                <tr>
                                    <#if currentUserId == profile.user.id>
                                        <td><a class="btn btn-outline-info" href="/redact-user-profile/${profile.user.id}">Редактировать</a></td>
                                    </#if>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </div><!-- /.main -->
    </div><!-- /.container -->
    <form id="preventDefaultOnForm" class="form-inline">
        <input id="content" type="text" placeholder="Сообщение">
        <button type="submit" id="send">Написать сообщение</button>
    </form>
</@c.page>

