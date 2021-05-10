<div class="row row-cols-1 row-cols-md-4 g-4">
    <#list publications as publication>
        <div class="col">
            <div class="card" style="width: 18rem;">
                <a class="card-header text-center text-black-50 nounderline"
                   href="/profile/${publication.author.id!""}">${publication.author.username!""}</a>
                <img class="card-img-top" src="/img/${publication.fileName!}" alt="изображение не добавлено">

                <div class="card-body">

                    <label class="card-title text-black-50 text-center">${publication.title!}</label>
                    <#--                    <p class="card-text">${publication.description!}</p>-->
                </div>

                <div class="card-footer transfer">
                    <i><h3 class="badge text-muted">#${publication.tags!}</h3></i>
                </div>

                <#--                <div class="card-text text-center">-->
                <a class="card-footer text-center btn-outline-secondary text-black-50 nounderline"
                   href="/publications/browse/${publication.id!}">Просмотреть публикацию</a>
                <#--                </div>-->
            </div>
        </div>
    </#list>
</div>
<br>