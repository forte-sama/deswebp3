package wrappers;

import models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by forte on 31/05/16.
 */
public class GestorUsuarios {
    private GestorUsuarios() { }

    public static boolean crearUsuario(Usuario target) {
        boolean exito = true;
        Connection con = null;
        String sql = "INSERT INTO usuarios(username,password,nombre,es_administrador,es_autor) VALUES(?,?,?,?,?);";

        try {
            con = DB.getConnection();

            boolean datosValidos = validarDatos(target,true);

            if(datosValidos) {
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setString(1,target.getUsername());
                pstm.setString(2,target.getPassword());
                pstm.setString(3,target.getNombre());
                pstm.setBoolean(4,target.isEsAdministrador());
                pstm.setBoolean(5,target.isEsAutor());

                exito = pstm.executeUpdate() > 0;
            }
            else {
                exito = false;
            }
        } catch(SQLException e) {
            //TODO CAMBIAR MENSAJE DE EXCEPCION
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch(SQLException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }
        }

        return exito;
    }

    private static boolean validarDatos(Usuario target, boolean estaCreando) {
        boolean validUsername = !target.getUsername().isEmpty() && target.getUsername().length() <= 50;
        boolean validPassword = !target.getPassword().isEmpty() && target.getPassword().length() <= 50;
        boolean validNombre   = !target.getNombre().isEmpty() && target.getNombre().length() <= 50;

        if(estaCreando) {
            validUsername = validUsername && esUsernameNuevo(target.getUsername());
        }
        else {
            validNombre = validNombre && esUsernameExistente(target.getUsername());
        }

        return validNombre && validPassword && validUsername;
    }

    private static boolean esUsernameExistente(String username) {
        Usuario us = getUsuario(username);

        return us != null;
    }

    public static Usuario getUsuario(String username_target) {
        Usuario user = null;
        Connection con = null;
        String sql = "SELECT username,password,nombre,es_administrador,es_autor FROM usuarios WHERE username=?;";

        try {
            //obtener conexion
            con = DB.getConnection();
            //crear preparedstatement para ejecutar consulta
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1,username_target);
            //ejecutar consulta
            ResultSet rs = pstm.executeQuery();
            //si encontro usuario, target no es usuario nuevo, de lo contrario si
            if(rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String nombre   = rs.getString("nombre");
                boolean es_administrador = rs.getBoolean("es_administrador");
                boolean es_autor = rs.getBoolean("es_autor");

                user = new Usuario(username,password,nombre,es_administrador,es_autor);
            }

        } catch (SQLException e) {
            //TODO CAMBIAR MENSAJE DE EXCEPCION
            e.printStackTrace();

        } finally {
            try {
                //cerrar conexion al finalizar
                con.close();
            } catch(SQLException e) {
                //TODO CAMBIAR MENSAJE DE EXCEPCION
                e.printStackTrace();
            }
        }

        return user;
    }
    private static boolean esUsernameNuevo(String target) {
        Usuario user = getUsuario(target);

        return user == null;
    }
}
