<#import "../macroses/common.ftlh" as c>
<@c.page>
    <div class="row mb-3" xmlns="http://www.w3.org/1999/html">
        <form class="row row-cols-lg-auto g-3 align-items-center" method="get" action="/publications/filter">
            <div class="col-12">
                <label class="visually-hidden" for="inlineFormInputGroupUsername">Поиск по тегу</label>
                <div>
                    <input type="text" name="filter" class="form-control" id="inlineFormInputGroupUsername"
                           placeholder="Фильтр">
                </div>
            </div>
            <div class="col-12">
                <button type="submit" class="btn btn-primary">Поиск</button>
            </div>
        </form>
    </div>


</@c.page>

