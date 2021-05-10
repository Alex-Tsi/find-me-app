<#import "../macroses/common.ftlh" as c>
<head>
    <#include "../macroses/imports.ftlh">
</head>
<@c.page>
    <#if isCurrentUser??>
        <a href="/publications/create" class="btn btn-outline-success mb-3 my-3">Добавить свою публикацию</a>

        <#include "../macroses/publicationsList.ftlh">
    </#if>


</@c.page>
