package Model;

public class Grupo {
    private final String     nombre;
    private final ColorGrupo color;

    public Grupo(String nombre, ColorGrupo color) {
        this.nombre = nombre; this.color = color;
    }
    public String     getNombre() { return nombre; }
    public ColorGrupo getColor()  { return color;  }

    @Override public String toString() { return nombre + " (" + color.getNombre() + ")"; }
}