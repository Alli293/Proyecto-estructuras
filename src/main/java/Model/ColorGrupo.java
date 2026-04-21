package Model;
import java.awt.Color;

public enum ColorGrupo {
    VERDE    ("Verde",          new Color(39,  174,  96)),
    AZUL     ("Azul",           new Color(41,  128, 185)),
    ROJO     ("Rojo",           new Color(192,  57,  43)),
    AMARILLO ("Amarillo",       new Color(241, 196,  15)),
    NARANJA  ("Naranja",        new Color(230, 126,  34)),
    MORADO   ("Morado",         new Color(142,  68, 173)),
    ROSA     ("Rosa",           new Color(231,  84, 128)),
    TURQUESA ("Turquesa",       new Color( 22, 160, 133)),
    CAFE     ("Café",           new Color(109,  76,  65)),
    GRIS     ("Gris azulado",   new Color(127, 140, 141));

    private final String nombre;
    private final Color  color;

    ColorGrupo(String nombre, Color color) { this.nombre = nombre; this.color = color; }

    public String getNombre() { return nombre; }
    public Color  getColor()  { return color;  }

    public Color getColorClaro() {
        return new Color(Math.min(255, color.getRed()+110),
                Math.min(255, color.getGreen()+110),
                Math.min(255, color.getBlue()+110));
    }
    @Override public String toString() { return nombre; }
}