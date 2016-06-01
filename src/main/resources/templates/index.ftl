<!DOCTYPE html>
<html lang="en">
<head>
    <link type="text/css" rel="stylesheet" href="/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="/css/custom.css">
    <meta charset="UTF-8">
    <title>Inicio</title>
    <script type="text/javascript" src="/js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="/js/bootstrap.min.js"></script>
</head>
<body>
<#include "nav.ftl">
</body>
<div class="container">
    <div class="row">
        <div class="col col-md-12">
            <div class="well well-lg">
                <h1>Commit inicial</h1>
                <p>Estado inicial de la practica #3</p>
                <#if msg??>
                    <div
                        <#if msg_type == "error">
                        class="alert alert-danger alert-dismissible"
                        <#elseif msg_type == "success">
                        class="alert alert-success alert-dismissible"
                        </#if>
                        role="alert">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <p>${msg}</p>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>
</html>