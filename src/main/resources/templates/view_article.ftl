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
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h1>${articulo.getTitulo()}</h1>
                        <hr />
                        <h5>Escrita por: <strong>${articulo.getAutorId()}</strong></h5>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col col-md-8">
                            ${articulo.getCuerpo()}
                            </div>
                            <div class="col col-md-4">
                                <#list articulo.etiquetas() as etiqueta>
                                <span class="label label-danger">${etiqueta}</span>
                                </#list>
                                <hr />
                                <div class="alert alert-danger">
                                    <p>Vamo a ver como se ve</p>
                                </div>
                                <hr />
                                <div class="alert alert-success">
                                    <p>Vamo a ver como se ve</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</html>