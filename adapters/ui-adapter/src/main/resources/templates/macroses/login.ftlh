<#macro login path isRegisterForm accept>
    <form action="${path}" method="post" align="center">
        <div class="form-group" align="center">
            <label class="col-sm-2 col-form-label"> Имя пользователя : </label>
            <div class="col-sm-5">
                <input type="text" name="username" class="form-control" placeholder="Имя пользователя"/>
            </div>
        </div>
        <div class="form-group" align="center">
            <label class="col-sm-2 col-form-label"> Пароль : </label>
            <div class="col-sm-5">
                <input type="password" name="password" class="form-control" placeholder="Пароль"/>
            </div>
        </div>
        <div class="m-3" align="center">
            <input class="me-3" type="hidden" name="_csrf" value="${_csrf.token}"/>
            <input class="btn btn-primary ms-3" type="submit" value="${accept}"/>
            <#if !isRegisterForm>
                <a href="/registration">Регистрация</a>
            </#if>
        </div>
    </form>
</#macro>

<#macro logout>
    <div>
        <form action="/logout" method="post">
            <button type="submit" class="btn btn-primary" value="Sign out">
                Выйти
            </button>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
        </form>
    </div>
</#macro>