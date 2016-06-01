<!DOCTYPE html>
<html lang="en">
<head>
    <link type="text/css" rel="stylesheet" href="/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="/css/custom.css">
    <meta charset="UTF-8">
    <title>Registro</title>
    <script type="text/javascript" src="/js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
</head>
<body>
<#include "nav.ftl">
</body>
<div class="container">
    <div class="row">
        <div class="col col-md-6 col-md-push-3">
            <div class="well well-lg">
                <div class="row">
                    <div class="col col-md-12">
                        <h1>Formulario de registro</h1>
                    </div>
                </div>
                <#if msg??>
                <div class="row">
                    <div class="col col-md-12 text-danger">
                        <div class="alert alert-danger alert-dismissible" role="alert">
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                            <p>${msg}</p>
                        </div>
                    </div>
                </div>
                </#if>
                <div class="row">
                    <div class="col col-md-12">
                        <form action="/register" method="post">
                            <#include "login_register_form_fields.ftl">
                            <div class="row">
                                <div class="col-md-7">
                                    <button type="submit" name="submit" value="submit" class="btn btn-primary btn-lg">Terminar registro</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</html>