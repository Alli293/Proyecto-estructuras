package Model;

public class Grupo {
    private String nombre;
    private String color;

    public Grupo(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return nombre + " (" + color + ")";
    }
}