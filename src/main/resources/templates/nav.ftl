<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="glyphicon glyphicon-menu-hamburger"></span>
            </button>
            <a class="navbar-brand" href="/">
                <span class="glyphicon glyphicon-fire"></span>
            </a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-left">
                <li class="active"><a href="/">Home</a></li>
                <li><a href="#">Link</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <#if loggedIn == true>
                <li><a href="/logout">Cerrar Sesion</a></li>
                <#else>
                <#if action != "login">
                <li><a href="/login">Login</a></li>
                </#if>
                <#if action != "register">
                <li><a href="/register">Register</a></li>
                </#if>
                </#if>
                <#--TODO SOLO PRESENTAR DROPDOWN CUANDO SEA UN ADMIN -->
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button">Admin <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/admin/users">Ver Usuarios</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">Otra accion</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>