<#assign
known = Session.SPRING_SECURITY_CONTEXT??
>
<#if known>
    <#assign
    user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    currentUserId = user.getId()!""
    skills = user.getProfile().getSkills()!""
    >
<#else>
    <#assign
    name = "Посетитель"
    currentUserId = -1
    >
</#if>