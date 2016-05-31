package main;

import freemarker.template.Configuration;
import models.Usuario;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import wrappers.DB;
import wrappers.GestorUsuarios;

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

        //Rutas
        /** Ver lista de estudiantes */
        get("/", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","index");

            return new ModelAndView(data,"index.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/login", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","login");

            GestorUsuarios.crearUsuario(crear());

            System.out.println(GestorUsuarios.getUsuario(crear().getUsername()));

            return new ModelAndView(data,"login.ftl");
        }, new FreeMarkerEngine(configuration));

        get("/register", (request, response) -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("action","register");

            return new ModelAndView(data,"register.ftl");
        }, new FreeMarkerEngine(configuration));
    }

    public static Usuario crear() {
        return new Usuario("coco","abc","nombre",false,false);
    }
}