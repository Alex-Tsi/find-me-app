<#import "../macroses/common.ftlh" as c>
<#import "../macroses/login.ftlh" as l>
<head>
    <#include "../macroses/imports.ftlh">
</head>

<@c.page>
    <br>
    <h1 class="font-monospace d-flex justify-content-center">Добавить пользователя</h1>
    <br>
    <h3 class="d-flex justify-content-center">${error!}</h3>
    <@l.login "/registration", true, "Зарегистрироваться"></@l.login>
</@c.page>