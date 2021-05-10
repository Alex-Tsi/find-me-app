<#include "../security/user.ftlh">
<#import "login.ftlh" as l>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">FindME</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/publications/find">Просмотр постов</a>
                </li>
                <#if user??>
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">Просмотр соответствиющих мне</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" id="connect" href="/chat/${currentUserId}">Мессенджер</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="/user-publications/${currentUserId}">Мои
                            публикации</a>
                    </li>
                </#if>
            </ul>
            <#if user??>
                <a class="nav-link d-flex me-3" href="/profile/${currentUserId}">${name}</a>
                <@l.logout></@l.logout>
            <#else>
                <div class="navbar-text me-3">${name}</div>
                <a class="navbar-text me-3" href="../security/login">Авторизоваться</a>
            </#if>
        </div>
    </div>
</nav>