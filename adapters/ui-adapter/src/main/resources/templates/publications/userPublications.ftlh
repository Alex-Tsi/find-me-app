<#import "../macroses/common.ftlh" as c>
<@c.page>
    <p>
        <button class="btn btn-primary"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#collapseExample"
                aria-expanded="false"
                aria-controls="collapseExample">
            Добавить пост
        </button>
    </p>
    <div class="collapse mb-3" id="collapseExample">
        <div class="card card-body">
            <form method="post" action="/publications/create" enctype="multipart/form-data">
                <input class="form-control mb-3" type="text" name="description" placeholder="Введите сообщение"/>
                <input class="form-control mb-3" type="text" name="tag" placeholder="Введите Тэг"/>
                <input class="form-control mb-3" type="file" name="file">
                <button class="btn btn-primary mb-3" type="submit">Добавить</button>
                <input type="hidden" name="_csrf" value="${_csrf.token}">
            </form>
        </div>
    </div>
</@c.page>
