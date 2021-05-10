<head>
    <#include "../macroses/imports.ftlh">
</head>
<#import "../macroses/common.ftlh" as c>
<#import "../macroses/login.ftlh" as l>

<@c.page>
    <@l.login "/login", false, "Войти"></@l.login>
</@c.page>
