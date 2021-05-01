<#import "../macroses/common.ftlh" as c>
<#import "../macroses/login.ftlh" as l>
<@c.page>
    <br>
    Добавить пользователя
    <h3>${error!}</h3>
    <@l.login "/registration", true, "Зарегистрироваться"></@l.login>
</@c.page>