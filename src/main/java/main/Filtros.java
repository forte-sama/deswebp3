package main;

import models.Usuario;
import wrappers.AccessTypes;
import wrappers.GestorUsuarios;
import wrappers.Sesion;

import static spark.Spark.before;

/**
 * Created by forte on 01/06/16.
 */
public class Filtros {
    public static void iniciarFiltros() {
        before("/register",(request, response) -> {
            //si la sesion esta activa, redireccionar
            if(Sesion.isLoggedIn(request)) {
                response.redirect("/");
            }
        });

        before("/admin/user/delete/:username",(request, response) -> {
            String username = request.params("username");

            Usuario user = GestorUsuarios.getUsuario(username);

            boolean esAdmin = Sesion.accesoValido(AccessTypes.ADMIN_ONLY,request,null);
            boolean esUsuarioActivo = Sesion.accesoValido(AccessTypes.OWNER_ONLY,request,user);

            //si no es (un admin o el mismo usuario), redireccionar
            if(!(esAdmin || esUsuarioActivo)) {
                response.redirect("/");
            }
        });

        before("/article/new", (request, response) -> {
            //si no ha iniciado sesion, redireccionar
            if(!Sesion.isLoggedIn(request)) {
                response.redirect("/");
            }
        });

        before("/article/process",(request, response) -> {
            //TODO filtrar si esta editando y quien edita no es duenio
        });
    }
}
