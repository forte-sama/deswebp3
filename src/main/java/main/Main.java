package main;

import freemarker.template.Configuration;
import models.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;
import wrappers.DB;
import wrappers.GestorUsuarios;
import wrappers.Sesion;

import java.util.HashMap;

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
            data.put("loggedIn", Sesion.isLoggedIn(request));

            return new ModelAndView(data,"index.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/admin/users", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","list_users");
            data.put("loggedIn", Sesion.isLoggedIn(request));

            //obtener los usuarios
            data.put("usuarios", GestorUsuarios.getUsuarios());

            return new ModelAndView(data,"user_list.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/admin/delete_user/:username",(request, response) -> {
            String username = request.params("username");

            Usuario target = GestorUsuarios.getUsuario(username);

            if(target != null) {
                //borrar
                GestorUsuarios.deleteUsuario(username);
            }
            //redireccionar
            response.redirect("/admin/users");

            return "";
        });

        get("/admin/edit_user/:username", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));

            String username = request.params("username");
            Usuario target = GestorUsuarios.getUsuario(username.trim());

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/users");
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

        post("/admin/edit_user", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","edit_user");
            data.put("loggedIn", Sesion.isLoggedIn(request));

            String username = request.queryParams("username");
            Usuario target = GestorUsuarios.getUsuario(username.trim());

            if(target == null) {
                //redireccionar por error
                response.redirect("/admin/users");
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
                    response.redirect("/admin/users");
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

        get("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");
            data.put("loggedIn", Sesion.isLoggedIn(request));

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

            if(!request.queryParams("submit").isEmpty()) {
                //obtener datos de quien desea iniciar sesion
                String username = request.queryParams("username");
                String password = request.queryParams("password");

                if(GestorUsuarios.credencialesValidas(username,password)) {
                    Usuario user = GestorUsuarios.getUsuario(username);
                    //TODO iniciar datos de sesion
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

        get("/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));

            return new ModelAndView(data,"register_edit_user.ftl");
        }, new FreeMarkerEngine(configuration));

        post("/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");
            data.put("loggedIn", Sesion.isLoggedIn(request));

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