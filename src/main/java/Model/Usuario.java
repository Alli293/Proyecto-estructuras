package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {
    private String username;
    private String password;
    private String primerNombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String avatar; // masculino, femenino, default
    private Grupo grupo;

    public Usuario(String username, String password, String primerNombre, String primerApellido,
                   String segundoApellido, LocalDate fechaNacimiento, String avatar) {
        this.username = username;
        this.password = password;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getAvatar() {
        return avatar;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getNombreCompleto() {
        return primerNombre + " " + primerApellido + " " + segundoApellido;
    }

    @Override
    public String toString() {
        return username + " - " + getNombreCompleto();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}