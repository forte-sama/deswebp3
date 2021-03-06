package main;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import freemarker.template.Configuration;
import models.Articulo;
import models.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;
import wrappers.*;

import java.util.HashMap;
import java.util.Set;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {
    public static void main(String[] args) {

        //indicar ruta de archivos publicos.
        staticFileLocation("/public");
        //agregar pantalla de debug. Solo en desarrollo.
        enableDebugScreen();

        //freemarker template engine
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(Main.class, "/templates");

        //probar estado de la base de datos
        DB.test();

        //aplicar filtros
        Filtros.iniciarFiltros();

        //Rutas
        get("/", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","index");
            data.put("loggedIn",Sesion.isLoggedIn(request));
            data.put("articulos",GestorArticulos.getArticulos());
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"index.ftl");
        }, new FreeMarkerEngine(configuration));


        get("/admin/user/list", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","list_users");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //obtener los usuarios
            data.put("usuarios", GestorUsuarios.getUsuarios());

            return new ModelAndView(data,"user_list.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/admin/user/delete/:username",(request, response) -> {
            String username = request.params("username");

            Usuario target = GestorUsuarios.getUsuario(username);

            if(target != null) {
                //borrar
                GestorUsuarios.deleteUsuario(username);
            }
            //redireccionar
            response.redirect("/admin/user/list");

            return "";
        });

        get("/admin/user/edit/:username", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String username = request.params("username");
            Usuario target = GestorUsuarios.getUsuario(username.trim());

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/user/list");
            }
            else {
                //setear datos para llenar formulario
                data.put("username",target.getUsername());
                data.put("nombre",target.getNombre());

                if(target.isAdministrador()) {
                    data.put("esAdministrador","si");
                }
                else {
                    if (target.isAutor()) {
                        data.put("esAutor", "si");
                    }
                    else {
                        data.put("esLector", "si");
                    }
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/admin/user/edit", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String username = request.queryParams("username");
            Usuario target = GestorUsuarios.getUsuario(username.trim());

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/user/list");
            }
            else {
                //tratar de actualizar usuario
                String password = request.queryParams("password");
                String nombre   = request.queryParams("nombre");
                boolean esAdministrador = request.queryParams("type").contentEquals("administrador");
                boolean esAutor = request.queryParams("type").contentEquals("autor") || esAdministrador;

                //actulizar usuario
                target = new Usuario(username,password,nombre,esAdministrador,esAutor);

                if(GestorUsuarios.saveUsuario(target,false)) {
                    //redireccionar
                    response.redirect("/admin/user/list");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username", target.getUsername());
                    data.put("nombre", target.getNombre());
                    if (target.isAdministrador()) {
                        data.put("esAdministrador", "si");
                    }
                    else {
                        if (target.isAutor()) {
                            data.put("esAutor", "si");
                        } else {
                            data.put("esLector", "si");
                        }
                    }

                    data.put("msg_type", "error");
                    data.put("msg", "Hubo un error con el formulario. Revisa los campos.");
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));


        get("/article/new", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","new_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/article/new", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","new_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String titulo = request.queryParams("titulo");
            String cuerpo = request.queryParams("cuerpo");
            String raw_etiquetas = request.queryParams("etiquetas");

            Set<String> etiquetas = GestorEtiquetas.parsearEtiquetas(raw_etiquetas);

            //Crear articulo en el gestor
            boolean exito = GestorArticulos.newArticulo(Sesion.getUsuarioActivo(request),titulo,cuerpo,etiquetas);

            if(exito) {
                //redireccionar a vista con mis articulos
                response.redirect("/");
            }
            else {
                data.put("titulo",titulo);
                data.put("cuerpo",cuerpo);
                data.put("etiquetas",raw_etiquetas);

                data.put("msg_type","error");
                data.put("msg","Hubo un error en el formulario");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/article/edit/:articulo_id", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            String raw_id = request.params("articulo_id");
            Articulo articulo = null;

            try {
                Long long_id = Long.parseLong(raw_id);
                articulo = GestorArticulos.getArticulo(long_id);
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }

            if (articulo != null) {
                data.put("id",articulo.getId());
                data.put("cuerpo",articulo.getCuerpo());
                data.put("titulo",articulo.getTitulo());
                data.put("etiquetas",GestorEtiquetas.cargarEtiquetas(articulo.getId()));
            }
            else {
                response.redirect("/");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/article/edit", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_article");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //obtener datos del form y del usuario activo
            String raw_id = request.queryParams("id");
            long long_id = -1;
            boolean exito = true;

            String autor  = Sesion.getUsuarioActivo(request);
            String titulo = request.queryParams("titulo");
            String cuerpo = request.queryParams("cuerpo");
            String raw_etiquetas = request.queryParams("etiquetas");

            Set<String> etiquetas = GestorEtiquetas.parsearEtiquetas(raw_etiquetas);

            try {
                long_id = Long.parseLong(raw_id.trim());

                exito = GestorArticulos.editArticulo(long_id,autor,titulo,cuerpo,etiquetas);
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXITO
                e.printStackTrace();
            }

            if(exito) {
                response.redirect("/");
            }
            else {
                data.put("id",long_id);
                data.put("titulo",titulo);
                data.put("cuerpo",cuerpo);
                data.put("etiquetas",GestorEtiquetas.cargarEtiquetas(long_id));

                data.put("msg_type","error");
                data.put("msg","Hubo un error con el formulario.");
            }

            return new ModelAndView(data,"create_edit_article.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/article/delete/:article_id", (request, response) -> {
            String raw_id = request.params("article_id");

            try {
                long long_id = Long.parseLong(raw_id);

                Articulo articulo = GestorArticulos.getArticulo(long_id);

                if(articulo != null) {
                    GestorArticulos.deleteArticulo(articulo.getId());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            response.redirect("/");

            return "";
        });

        get("/article/view/:article_id", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","view_article");
            data.put("loggedIn",Sesion.isLoggedIn(request));
            data.put("currentUser",Sesion.getUsuarioActivo(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);
            if(esAdmin) {
                data.put("isAdmin","si");
            }

            String raw_article_id = request.params("article_id");
            boolean exito = false;

            try {
                long long_id = Long.parseLong(raw_article_id);

                Articulo articulo = GestorArticulos.getArticulo(long_id);

                if(articulo != null) {
                    data.put("articulo", articulo);
                    data.put("comentarios",GestorComentarios.getComentarios(articulo.getId()));
                    exito = true;
                }
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }

            if(!exito) {
                response.redirect("/");
            }

            return new ModelAndView(data,"view_article.ftl");
        }, new FreeMarkerEngine(configuration));


        post("/comment/new", (request, response) -> {
            if(!Sesion.isLoggedIn(request)) {
                response.redirect("/");
            }

            String username        = Sesion.getUsuarioActivo(request);
            String comentario      = request.queryParams("comentario");
            String raw_articulo_id = request.queryParams("articulo_id");

            boolean exito = false;

            try {
                long long_articulo_id = Long.parseLong(raw_articulo_id);
                GestorComentarios.newComentario(username, comentario, long_articulo_id);
                exito = true;
            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }

            if(exito) {
                response.redirect("/article/view/" + raw_articulo_id);
            }
            else {
                response.redirect("/");
            }

            return "";
        });

        get("/comment/delete/:article_id/:comment_id", (request, response) -> {
            String articulo_id   = request.params("article_id");

            boolean exito = false;

            try {
                long long_articulo   = Long.parseLong(articulo_id);

                Articulo articulo = GestorArticulos.getArticulo(long_articulo);

                exito = articulo.getAutorId() == Sesion.getUsuarioActivo(request);

            } catch (NumberFormatException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                exito = false;
                e.printStackTrace();
            }

            boolean esAdministrador = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);

            if(exito || esAdministrador) {
                String comentario_id = request.params("comment_id");

                try {
                    long long_comentario_id = Long.parseLong(comentario_id);
                    GestorComentarios.deleteComentario(long_comentario_id);
                } catch (NumberFormatException e) {
                    //TODO CAMBIAR MENSAJE DE EXCEPCION
                    e.printStackTrace();
                }
            }

            response.redirect("/article/view/" + articulo_id);

            return "";
        });


        get("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"login.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/logout",(request, response) -> {
            Sesion.cerrar(request);

            response.redirect("/");

            return "";
        });

        post("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            if(!request.queryParams("submit").isEmpty()) {
                //obtener datos de quien desea iniciar sesion
                String username = request.queryParams("username");
                String password = request.queryParams("password");

                if(GestorUsuarios.credencialesValidas(username,password)) {
                    Usuario user = GestorUsuarios.getUsuario(username);
                    //iniciar sesion
                    Sesion.iniciar(request,user);

                    //redireccionar con estado de exito
                    response.redirect("/");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username",username);

                    data.put("msg_type","error");
                    data.put("msg","No se pudo iniciar sesion. Username/password no coinciden.");
                }
            }

            return new ModelAndView(data,"login.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/user/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/user/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));
            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            data.put("isAutor",Sesion.getTipoUsuarioActivo(request) == "autor" || esAdmin);

            //si el request llego desde el formulario
            if(!request.queryParams("submit").isEmpty()) {
                //obtener datos de nuevo usuario
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String nombre   = request.queryParams("nombre");
                //no es administrador by default
                boolean esAutor = request.queryParams("type").contentEquals("autor"); //1 : autor, 0 : lector

                //crear nueva instancia
                Usuario newUser = new Usuario(username,password,nombre,false,esAutor);

                //persistir nueva instancia, en caso de ser valida
                if(GestorUsuarios.saveUsuario(newUser,true)) {
                    //redireccionar con mensaje de exito
                    response.redirect("/");
                }
                else {
                    //setear datos para llenar formulario
                    data.put("username",newUser.getUsername());
                    data.put("nombre",newUser.getNombre());
                    if(newUser.isAutor()) {
                        data.put("esAutor","si");
                    }
                    else {
                        data.put("esLector","si");
                    }

                    data.put("msg_type","error");
                    data.put("msg","No se pudo crear usuario. Revisar datos del formulario.");
                }
            }

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));
    }
}