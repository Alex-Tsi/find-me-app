<#import "../macroses/common.ftlh" as c>
<#include "../security/user.ftlh">
<head>
    <#include "../macroses/imports.ftlh">
</head>

<@c.page>
    <form action="/update-publication" method="post" enctype="multipart/form-data">
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1">Заголовок публикации</span>
            <input value="${publication.title!""}" name="title"
                   type="text" class="form-control" placeholder="Титульник" aria-label="Придумайте титульник"
                   aria-describedby="basic-addon1">
            <span class="input-group-text">Тэги</span>
            <input value="${publication.tags!""}" name="tags"
                   type="text" class="form-control" placeholder="Перечислите теги">
        </div>

        <div class="input-group mb-3">
            <input value="${publication.motivations!""}" name="motivations"
                   type="text" class="form-control" placeholder="В чем причина возникновения такой идеи?"
                   aria-label="Recipient's username" aria-describedby="basic-addon2">
            <span class="input-group-text" id="basic-addon2">Мотивация</span>
        </div>

        <div class="input-group mb-3">
            <span class="input-group-text">Описание</span>
            <textarea name="description"
                      class="form-control" aria-label="Описание">
                ${publication.description!""}
            </textarea>
        </div>

        <div class="input-group mb-3">
            <span class="input-group-text">Какие награды получили бы участники?</span>
            <textarea name="rewards"
                      class="form-control" aria-label="Награды">
                ${publication.rewards!""}
            </textarea>
        </div>

        <div class="input-group flex-nowrap mb-3">
            <span class="input-group-text" id="addon-wrapping">Кого вы ищите?</span>
            <input value="${publication.whoNeed!""}" name="whoNeed"
                   type="text" class="form-control" placeholder="Специалисты" aria-label="Username"
                   aria-describedby="addon-wrapping">
        </div>

        <div class="input-group mb-3">
            <input name="fileName"
                   type="file" class="form-control" id="inputGroupFile02">
            <label class="input-group-text" for="inputGroupFile02">Загрузите изображение проекта</label>
        </div>

        <div class="input-group mb-3">
            <button type="submit" class="btn btn-outline-success my-3">Изменить</button>
            <a href="/publications/browse/${publication.id}" class="btn btn-outline-danger my-3">Отменить</a>
        </div>
        <input type="hidden" name="id" value="${publication.id}">
        <input type="hidden" name="_csrf" value="${_csrf.token}">


        <div class="input-group mb-3">

        </div>

    </form>
</@c.page>