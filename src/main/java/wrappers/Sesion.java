package wrappers;

import models.Articulo;
import models.Comentario;
import models.Usuario;
import spark.Request;
import spark.Session;

import static wrappers.AccessTypes.ADMIN_ONLY;
import static wrappers.AccessTypes.AUTHOR_ONLY;

/**
 * Created by forte on 01/06/16.
 */

public class Sesion {
    public static boolean accesoValido(AccessTypes level, Request request, Object recurso_a_validar) {
        switch (level) {
            case LOGGED_IN_ONLY:
                return logged_in_control(request);

            case OWNER_ONLY:
                return owner_control(request,recurso_a_validar);

            case ADMIN_ONLY:
                return type_control(request,"administrador");

            case AUTHOR_ONLY:
                return type_control(request,"autor");

            default:
                return true;
        }
    }

    public static void iniciar(Request request, Usuario user) {
        Session session = request.session(true);

        session.attribute("username",user.getUsername());

        String user_type = "";
        if(user.isAdministrador()) {
            user_type = "administrador";
        }
        else if(user.isAutor()) {
            user_type = "autor";
        }
        else {
            user_type = "lector";
        }

        session.attribute("user_type",user_type);
    }

    public static void cerrar(Request request){
        Session session = request.session(true);

        session.removeAttribute("user_type");
        session.removeAttribute("username");

        session.invalidate();
    }

    public static boolean isLoggedIn(Request request) {
        Session session = request.session(false);

        if(session != null){
            if(session.attribute("username") != null) return true;
        }

        return false;
    }

    //creacion de articulos
    //creacion de etiquetas a que pertenece articulo
    //creacion de comentarios
    private static boolean logged_in_control(Request request) {
        Session session = request.session(true);

        String usr_type = session.attribute("user_type");
        String username_actual = session.attribute("username");

        //permitir acceso cuando se han seteado datos de la sesion
        if(usr_type != null && username_actual != null) {
            return true;
        }
        else {
            return false;
        }
    }

    //edicion de articulos
    //edicion de comentarios (solo owner del comentario)
    //borrado de articulos
    //borrado de comentarios en articulo (solo el owner del articulo)
    private static boolean owner_control(Request request,Object recurso) {
        Session session = request.session(true);

        String usr_type = session.attribute("user_type");
        String username_actual = session.attribute("username");

        //permitir acceso cuando se han seteado datos de la sesion
        if(usr_type != null && username_actual != null) {
            //recurso actual (articulo, comentario) pertenece a usuario en sesion actual
            if(recurso instanceof Articulo) {
                return ((Articulo) recurso).getAutorId() == username_actual;
            }
            else if(recurso instanceof Comentario) {
                return ((Comentario) recurso).getAutorId() == username_actual;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    //admin
        //creacion de articulos
        //creacion de comentarios
        //edicion de articulos
        //edicion de usuarios
        //borrado de articulos
        //borrado de comentarios
    //autor
        //creacion de articulos
    private static boolean type_control(Request request, String tipo) {
        Session session = request.session(true);

        String usr_type = session.attribute("user_type");
        String username = session.attribute("username");

        //permitir acceso cuando se han seteado datos de la sesion
        //y el tipo de usuario coincida (String tipo debe coincidir con dato en session user_type)
        if(usr_type != null && username != null) {
            return usr_type == tipo;
        }
        else {
            return false;
        }
    }
}