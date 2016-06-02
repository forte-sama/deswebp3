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
                    <#if action == "new_article">
                        <h1>Nuevo articulo</h1>
                    <#elseif action == "edit_article">
                        <h1>Edicion de articulo</h1>
                    </#if>
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
                        <form
                        <#--TODO MANDAR AL MISMO URL PARA PROCESAR CREACION/EDICION DE ARTICULO -->
                        <#--<#if action == "new_article">-->
                                <#--action="/article/new"-->
                        <#--<#elseif action == "edit_article">-->
                                <#--action="/article/edit"-->
                        <#--</#if>-->
                                action="article/process"
                                method="post">
                            <#if action == "edit_article">
                            <input type="hidden" name="id" value="${article.id?string["0"]}">
                            </#if>
                            <#--<#include "login_register_form_fields.ftl">-->
                            <div class="row">
                                <div class="col-md-7">
                                    <button type="submit" name="submit" value="submit" class="btn btn-primary btn-lg">
                                        Terminar <#if action == "new_article">Articulo<#elseif action == "edit_article">Edicion de Articulo</#if>
                                    </button>
                                </div>
                            <#if action == "edit_article" && username??>
                                <div class="col-md-3 col-md-offset-2">
                                    <a class="btn btn-danger" href="/article/delete/${article.id}">Borrar</a>
                                </div>
                            </#if>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</html>