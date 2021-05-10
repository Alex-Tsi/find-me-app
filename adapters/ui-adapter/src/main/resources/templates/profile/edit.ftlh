<#import "../macroses/common.ftlh" as c>
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "../macroses/imports.ftlh">
</head>
<@c.page>
    <form action="/update-profile" method="post" enctype="multipart/form-data">
        <div class="container">
            <div class="row">
                <div class="input-group mb-3">
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Пол</label>
                        <input type="text" name="firstName" aria-label="First name" class="form-control"
                               placeholder="${profile.getFirstName()!"Имя"}"
                               value="${profile.getFirstName()!"Имя"}">
                    </div>
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Пол</label>
                        <input type="text" name="lastName" aria-label="First name" class="form-control"
                               placeholder="${profile.getLastName()!"Фамилия"}"
                               value="${profile.getLastName()!"Фамилия"}">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="input-group mb-3">
                    <div class="col-sm">
                        <label class="input-group-text" for="inputGroupSelect01">Пол</label>
                        <select class="form-select" id="inputGroupSelect01" name="sex">
                            <option name="sex" value="${profile.sex!""}" selected>Выбрать пол</option>
                            <option value="мужской">Мужской</option>
                            <option value="женский">Женский</option>
                        </select>
                    </div>
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Телефон +7(XXX)-XX-XX</label>
                        <input class="form-control"
                               type="text" name="phoneNumber" placeholder="Номер телефона"
                               value="${profile.getPhoneNumber()!"+7(XXX)-XX-XX"}">
                    </div>
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Возраст</label>
                        <input type="text" name="age" aria-label="First name" class="form-control"
                               placeholder="${profile.age!23}"
                               value="${profile.age!23}">
                    </div>

                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Email</label>
                        <input type="text" name="email" aria-label="First name" class="form-control"
                               placeholder="${profile.email!"Ваша почта"}"
                               value="${profile.email!""}">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="input-group mb-3">
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Адрес</label>
                        <input class="form-control form-control-lg"
                               type="text" name="address" placeholder="Адрес"
                               value="${profile.address!"Адрес"}">
                    </div>

                    <div class="col-md-auto">
                        <label class="input-group-text" for="inputGroupSelect01">Город</label>
                        <input class="form-control form-control-lg"
                               type="text" name="city" placeholder="Город"
                               value="${profile.city!"Город"}">
                    </div>

                    <div class="col col-lg-2">
                        <label class="input-group-text" for="inputGroupSelect01">Страна</label>
                        <input class="form-control form-control-lg"
                               type="text" name="country" placeholder="Страна"
                               value="${profile.country!"Страна"}">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="input-group mb-3">
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Перечислите ваши умения
                            (скиллы)</label>
                        <input class="form-control form-control-lg"
                               type="text" name="skills" placeholder="Умения"
                               value="${profile.skills!"Умения"}">
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="input-group mb-3">
                    <div class="col">
                        <label class="input-group-text" for="inputGroupSelect01">Опишите себя в двух словах</label>
                        <textarea class="form-control" name="description" id="exampleFormControlTextarea1" rows="3"
                                  value="${profile.getDescription()!"Описание"}"></textarea>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group mb-3">
                        <input name="file"
                               type="file" class="form-control" id="inputGroupFile02">
                        <label class="input-group-text" for="inputGroupFile02">Загрузите свой аватар</label>
                    </div>
                </div>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <input type="hidden" name="id" value="${profile.id}">
            <button type="submit" class="btn btn-outline-success col-md-2">Изменить профиль</button>
        </div>
    </form>
</@c.page>
