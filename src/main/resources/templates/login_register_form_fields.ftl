<div class="row">
    <div class="form-group col-md-7">
        <label for="username">Username</label>
        <input type="text" class="form-control" name="username" placeholder="forte-sama" <#if username??>value="${username}"</#if>>
    </div>
</div>
<div class="row">
    <div class="form-group col-md-7">
        <label for="password">Password</label>
        <input type="password" class="form-control" name="password" placeholder="pass">
    </div>
</div>
<#if action == "register">
<#-- incluir datos para cuando se este registrando -->
<div class="row">
    <div class="form-group col-md-7">
        <label for="nombre">Nombre</label>
        <input type="text" class="form-control" name="nombre" placeholder="Juan Perez" <#if nombre??>value="${nombre}"</#if>>
    </div>
</div>
<div class="row">
    <div class="form-group col-md-7">
        <label for="type">Registrarme como</label>
        <select class="form-control" name="type">
            <option value="lector" <#if esLector??>selected="selected"</#if>>Lector</option>
            <option value="autor" <#if esAutor??>selected="selected"</#if>>Autor</option>
        </select>
    </div>
</div>
</#if>