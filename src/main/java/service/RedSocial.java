package service;

import Model.Grupo;
import Model.Usuario;

import java.util.*;

public class RedSocial {

    // ─── datos ──────────────────────────────────────────────────────────────────
    private final Map<String, Usuario>       usuarios   = new LinkedHashMap<>();
    private final Map<String, Grupo>         grupos     = new LinkedHashMap<>();
    private final Map<Usuario, List<Usuario>> amistades = new LinkedHashMap<>();

    // ─── PUNTO 3 y 4: lista de listeners ────────────────────────────────────────
    private final List<RedSocialListener> listeners = new ArrayList<>();

    /** Registra un panel para recibir notificaciones cada vez que cambian los datos. */
    public void addListener(RedSocialListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    /** Llama onDataChanged() en todos los listeners registrados. */
    private void notificar() {
        for (RedSocialListener l : listeners) {
            l.onDataChanged();
        }
    }

    // ─── usuarios ────────────────────────────────────────────────────────────────

    /**
     * Agrega un usuario nuevo.
     * @return false si ya existe un usuario con ese username.
     */
    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getUsername())) return false;
        usuarios.put(usuario.getUsername(), usuario);
        amistades.put(usuario, new ArrayList<>());
        notificar();   // <-- notifica a todos los paneles
        return true;
    }

    public Usuario buscarUsuario(String username) {
        return usuarios.get(username);
    }

    public Collection<Usuario> getUsuarios() {
        return Collections.unmodifiableCollection(usuarios.values());
    }

    // ─── grupos ──────────────────────────────────────────────────────────────────

    /**
     * Agrega un grupo nuevo.
     * @return false si ya existe un grupo con ese nombre.
     */
    public boolean agregarGrupo(Grupo grupo) {
        if (grupos.containsKey(grupo.getNombre())) return false;
        grupos.put(grupo.getNombre(), grupo);
        notificar();   // <-- notifica a todos los paneles
        return true;
    }

    public Collection<Grupo> getGrupos() {
        return Collections.unmodifiableCollection(grupos.values());
    }

    /**
     * Asigna un grupo a un usuario por nombre.
     * @return false si el usuario o el grupo no existen.
     */
    public boolean asignarGrupoAUsuario(String username, String nombreGrupo) {
        Usuario u = usuarios.get(username);
        Grupo   g = grupos.get(nombreGrupo);
        if (u == null || g == null) return false;
        u.setGrupo(g);
        notificar();
        return true;
    }

    // ─── amistades ───────────────────────────────────────────────────────────────

    /**
     * Agrega una amistad DIRIGIDA de u1 → u2 (no recíproca automáticamente).
     * @return false si alguno no existe o la amistad ya existe.
     */
    public boolean agregarAmistad(String username1, String username2) {
        Usuario u1 = usuarios.get(username1);
        Usuario u2 = usuarios.get(username2);
        if (u1 == null || u2 == null || u1.equals(u2)) return false;

        List<Usuario> amigosU1 = amistades.get(u1);
        if (amigosU1.contains(u2)) return false;   // ya existe

        amigosU1.add(u2);
        notificar();
        return true;
    }

    /**
     * Elimina la amistad dirigida u1 → u2.
     * @return false si no existía.
     */
    public boolean eliminarAmistad(String username1, String username2) {
        Usuario u1 = usuarios.get(username1);
        Usuario u2 = usuarios.get(username2);
        if (u1 == null || u2 == null) return false;

        boolean eliminado = amistades.get(u1).remove(u2);
        if (eliminado) notificar();
        return eliminado;
    }

    /** Devuelve el mapa completo de amistades (para dibujar el grafo). */
    public Map<Usuario, List<Usuario>> getAmistades() {
        return Collections.unmodifiableMap(amistades);
    }

    // ─── consultas BFS ───────────────────────────────────────────────────────────

    /**
     * Devuelve la lista de amigos directos del usuario (salientes).
     */
    public List<Usuario> obtenerAmigos(String username) {
        Usuario u = usuarios.get(username);
        if (u == null) return Collections.emptyList();
        return Collections.unmodifiableList(amistades.getOrDefault(u, Collections.emptyList()));
    }

    /**
     * Devuelve sugerencias de amistad usando BFS (amigos de amigos que no son ya amigos directos).
     */
    public List<Usuario> obtenerSugerencias(String username) {
        Usuario origen = usuarios.get(username);
        if (origen == null) return Collections.emptyList();

        List<Usuario> amigosDirectos = amistades.getOrDefault(origen, Collections.emptyList());
        Set<Usuario>  yaConoce       = new HashSet<>(amigosDirectos);
        yaConoce.add(origen);

        List<Usuario> sugerencias = new ArrayList<>();
        Set<Usuario>  visto       = new HashSet<>();

        // BFS: nivel 2 (amigos de amigos)
        Queue<Usuario> cola = new LinkedList<>(amigosDirectos);
        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();
            List<Usuario> amigosDeAmigo = amistades.getOrDefault(actual, Collections.emptyList());
            for (Usuario candidato : amigosDeAmigo) {
                if (!yaConoce.contains(candidato) && !visto.contains(candidato)) {
                    sugerencias.add(candidato);
                    visto.add(candidato);
                }
            }
        }
        return sugerencias;
    }
}