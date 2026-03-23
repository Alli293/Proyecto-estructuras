package service;

import Model.*;

import java.util.*;

public class RedSocial {

    private final Map<String, Usuario> usuarios;
    private final Map<String, Grupo> grupos;
    private final Map<Usuario, List<Usuario>> amistades;

    public RedSocial() {
        usuarios = new LinkedHashMap<>();
        grupos = new LinkedHashMap<>();
        amistades = new LinkedHashMap<>();
    }

    public boolean agregarUsuario(Usuario usuario) {
        if (usuario == null || usuarios.containsKey(usuario.getUsername())) {
            return false;
        }
        usuarios.put(usuario.getUsername(), usuario);
        amistades.put(usuario, new ArrayList<>());
        return true;
    }

    public boolean agregarGrupo(Grupo grupo) {
        if (grupo == null || grupos.containsKey(grupo.getNombre().toLowerCase())) {
            return false;
        }
        grupos.put(grupo.getNombre().toLowerCase(), grupo);
        return true;
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios.values();
    }

    public Collection<Grupo> getGrupos() {
        return grupos.values();
    }

    public Usuario buscarUsuario(String username) {
        return usuarios.get(username);
    }

    public Grupo buscarGrupo(String nombre) {
        return grupos.get(nombre.toLowerCase());
    }

    public boolean asignarGrupoAUsuario(String username, String nombreGrupo) {
        Usuario usuario = buscarUsuario(username);
        Grupo grupo = buscarGrupo(nombreGrupo);

        if (usuario == null || grupo == null) {
            return false;
        }

        usuario.setGrupo(grupo);
        return true;
    }

    public boolean agregarAmistad(String user1, String user2) {
        Usuario u1 = buscarUsuario(user1);
        Usuario u2 = buscarUsuario(user2);

        if (u1 == null || u2 == null || u1.equals(u2)) {
            return false;
        }

        if (!amistades.get(u1).contains(u2)) {
            amistades.get(u1).add(u2);
        }

        if (!amistades.get(u2).contains(u1)) {
            amistades.get(u2).add(u1);
        }

        return true;
    }

    public boolean eliminarAmistad(String user1, String user2) {
        Usuario u1 = buscarUsuario(user1);
        Usuario u2 = buscarUsuario(user2);

        if (u1 == null || u2 == null) {
            return false;
        }

        amistades.get(u1).remove(u2);
        amistades.get(u2).remove(u1);
        return true;
    }

    public List<Usuario> obtenerAmigos(String username) {
        Usuario usuario = buscarUsuario(username);
        if (usuario == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(amistades.get(usuario));
    }

    // BFS para sugerencias: amigos de amigos
    public List<Usuario> obtenerSugerencias(String username) {
        Usuario inicio = buscarUsuario(username);
        if (inicio == null) {
            return new ArrayList<>();
        }

        Set<Usuario> visitados = new LinkedHashSet<>();
        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> sugerencias = new LinkedHashSet<>();

        visitados.add(inicio);
        cola.offer(inicio);

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();

            for (Usuario vecino : amistades.get(actual)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.offer(vecino);
                }
            }
        }

        List<Usuario> amigosDirectos = amistades.get(inicio);

        for (Usuario visitado : visitados) {
            if (!visitado.equals(inicio) && !amigosDirectos.contains(visitado)) {
                sugerencias.add(visitado);
            }
        }

        return new ArrayList<>(sugerencias);
    }

    public Map<Usuario, List<Usuario>> getAmistades() {
        return amistades;
    }
}