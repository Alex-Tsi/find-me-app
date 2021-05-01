<div class="row row-cols-1 row-cols-md-4 g-4">
    <#list publications as publication>
        <div class="col">
            <div class="card" style="width: 18rem;">
                <#--                        <#if publication.fileName??>-->
                <img class="card-img-top" src="/img/${publication.fileName!}" alt="изображение не добавлено">
                <#--                        </#if>-->
                <div class="card-body">
                    <h5 class="card-title">${publication.title!}</h5>
                    <p class="card-text">${publication.description!}</p>
                </div>
                <div class="card-footer">
                    <h3 class="badge text-muted">${publication.tag!}</h3>
                </div>

            </div>
        </div>
    <#else>
        No Publications
    </#list>
</div>
<br>