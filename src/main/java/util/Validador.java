package util;
import java.time.LocalDate;

public class Validador {
    public static boolean textoVacio(String t)  { return t==null||t.trim().isEmpty(); }
    public static boolean esUsernameValido(String u) { return !textoVacio(u)&&u.matches("[a-zA-Z0-9_.]{3,20}"); }
    public static boolean esPasswordValida(String p) { return !textoVacio(p)&&p.length()>=6; }
    public static boolean esSoloLetras(String t) { return !textoVacio(t)&&t.trim().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+"); }
    public static boolean esFechaValida(String f) {
        try { LocalDate.parse(f); return true; } catch (Exception e) { return false; }
    }
    public static boolean esFechaEnPasado(String f) {
        try { return LocalDate.parse(f).isBefore(LocalDate.now()); } catch (Exception e) { return false; }
    }
}