<#import "../macroses/common.ftlh" as c>
<head>
    <#include "../macroses/imports.ftlh">
</head>
<#include "../security/user.ftlh">
<@c.page>
    <#if currentUserId == publication.author.id>
        <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton1"
                    data-bs-toggle="dropdown" aria-expanded="false">
                Dropdown button
            </button>
            <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
                <li><a class="dropdown-item"
                       href="/redact-publication/${publication.id}">Редактировать</a></li>
                <li><a class="dropdown-item" href="/delete-publication/${publication.id}">Удалить
                        публикацию</a>
                </li>
            </ul>
        </div>
    </#if>
    <table class="my-5 table-borderless">
        <tr>
            <td>
                <div class="my-5">
                </div>
                <div class="accordion accordion-flush" id="accordionFlushExample">
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="flush-headingOne">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#flush-collapseOne" aria-expanded="false"
                                    aria-controls="flush-collapseOne">
                                Описание публикации
                            </button>
                        </h2>
                        <div id="flush-collapseOne" class="accordion-collapse collapse"
                             aria-labelledby="flush-headingOne"
                             data-bs-parent="#accordionFlushExample">
                            <div class="accordion-body">${publication.description!"Описание не добавлено"}</div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="flush-headingTwo">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#flush-collapseTwo" aria-expanded="false"
                                    aria-controls="flush-collapseTwo">
                                Мотивация автора при публикации
                            </button>
                        </h2>
                        <div id="flush-collapseTwo" class="accordion-collapse collapse"
                             aria-labelledby="flush-headingTwo"
                             data-bs-parent="#accordionFlushExample">
                            <div class="accordion-body">${publication.motivations!"Мотивация не добавлена"}</div>
                        </div>
                    </div>
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="flush-headingThree">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#flush-collapseThree" aria-expanded="false"
                                    aria-controls="flush-collapseThree">
                                Если есть, то какие награды бы выделялись для ваших будущих сотрудников?
                            </button>
                        </h2>
                        <div id="flush-collapseThree" class="accordion-collapse collapse"
                             aria-labelledby="flush-headingThree"
                             data-bs-parent="#accordionFlushExample">
                            <div class="accordion-body">${publication.rewards!"Не заданы награды, их скорее и не будет("}</div>
                        </div>
                    </div>
                </div>
                <br>
            </td>
            <td>
                <img src="/img/${publication.fileName!"noimage.jpeg"}" class="rounded d-block"
                     alt="Картинка проекта не добавлена ;(">
            </td>
        </tr>
    </table>
</@c.page>
