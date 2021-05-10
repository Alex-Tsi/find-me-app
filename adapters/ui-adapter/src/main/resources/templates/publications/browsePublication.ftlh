<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <meta name="description" content=""/>
    <meta name="author" content=""/>
    <title>Публикация</title>
    <!-- Core theme CSS (includes Bootstrap)-->
    <link href="/static/css/styles.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6"
          crossorigin="anonymous">
</head>
<body>
<!-- Navigation-->
<#include "../macroses/navbar.ftlh">
<!-- Page content-->
<div class="container">
    <div class="row">
        <!-- Post content-->
        <div class="col-lg-8">
            <!-- Title-->
            <h1 class="mt-4">${publication.title!"Не задан"}</h1>
            <!-- Author-->
            <p class="lead">
                От
                <a href="#!">${publication.author.username}</a>
            </p>
            <hr/>
            <!-- Date and time-->
            <p>${publication.date!"Дата не указана"}</p>
            <hr/>
            <!-- Preview image-->
            <img class="img-fluid rounded" src="/img/${publication.fileName!"default.png"}" alt="..."/>
            <hr/>
            <!-- Post content-->
            <div class="transfer">
                <p>${motivations!""}</p>

            </div>
            <div class="transfer">
                <p class="lead text-black-50 transfer">

                    ${descriptions!""}
                </p>
            </div>
            <#if currentUserId == publication.author.id>
                <a class="btn btn-secondary" href="/edit-publication/${publication.id}">Редактировать</a>
                <a class="btn btn-outline-danger" href="/delete-publication/${publication.id}">Удалить</a>
            </#if>
            <hr/>
            <!-- Comments form-->
            <div class="card my-4">
                <h5 class="card-header">Оставить комментарий:</h5>
                <div class="card-body">
                    <form action="/add-comment/" method="post">
                        <div class="form-group"><textarea name="text" class="form-control" rows="3"></textarea></div>
                        <button class="btn btn-primary" type="submit">Добавить</button>
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <input type="hidden" name="publication_id" value="${publication.id}">
                    </form>
                </div>
            </div>
            <!-- Single comment-->
            <#list publication.comments as comment>
                <div class="media mb-4">
                    <img class="d-flex mr-3 rounded-circle" src="https://via.placeholder.com/50x50" alt="..."/>
                    <div class="media-body">
                        <h5 class="mt-0">${comment.user.username}</h5>
                        ${comment.text}
                    </div>
                </div>
            </#list>
        </div>
        <!-- Sidebar widgets column-->
        <div class="col-md-4">
            <!-- Search widget-->
            <div class="card my-4">
                <h5 class="card-header">Search</h5>
                <div class="card-body">
                    <div class="input-group">
                        <input class="form-control" type="text" placeholder="Search for..."/>
                        <span class="input-group-append"><button class="btn btn-secondary"
                                                                 type="button">Go!</button></span>
                    </div>
                </div>
            </div>
            <!-- Categories widget-->
            <div class="card my-4">
                <h5 class="card-header">Тэги</h5>
                <div class="card-body">

                    <#list tags as tag>
                        <a class="text-muted u-size-2 text-center" href="/publications/filter/${tag}">${tag!""}</a>
                    </#list>

                </div>

            </div>
            <!-- Side widget-->
            <div class="card my-4">
                <h5 class="card-header">Требуются в помощь</h5>
                <div class="card-body">
                    <p class="text-success">
                        <#list helpers as helper>
                            ${helper!"Не указано"}
                        </#list>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- Footer-->

<!-- Bootstrap core JS-->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>
<!-- Core theme JS-->
<script src="/static/js/chat.js"></script>
</body>
</html>
