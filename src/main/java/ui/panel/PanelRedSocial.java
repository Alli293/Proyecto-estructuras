package ui.panel;

import Model.Usuario;
import service.RedSocial;
import service.RedSocialListener;
import ui.AvatarPainter;
import ui.Refreshable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PanelRedSocial extends JPanel implements RedSocialListener, Refreshable {

    private static final int R = 38; // radio del nodo en píxeles

    private final RedSocial redSocial;
    private final Map<String, Point> posiciones = new LinkedHashMap<>();

    // Estado de interacción
    private String  primerNodo   = null;
    private boolean modoEliminar = false;
    private String  nodoDrag     = null;
    private int     dragOffX, dragOffY;
    private boolean seDrago      = false;

    private JLabel lblEstado;
    private final JPanel canvas;

    // ─── constructor ────────────────────────────────────────────────────────────

    public PanelRedSocial(RedSocial redSocial) {
        this.redSocial = redSocial;
        redSocial.addListener(this);

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        // Barra superior
        JPanel top = new JPanel(new BorderLayout(10,0));
        top.setBackground(new Color(35,60,130));
        top.setBorder(new EmptyBorder(8,12,8,12));

        JLabel titulo = new JLabel("Red Social — Grafo de Amistades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        top.add(titulo, BorderLayout.WEST);

        JPanel instrucciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        instrucciones.setOpaque(false);
        instrucciones.add(badge("Click izquierdo: agregar amistad", new Color(41,128,185)));
        instrucciones.add(badge("Click derecho: eliminar amistad",  new Color(192,57,43)));
        instrucciones.add(badge("Arrastrar: mover nodo",            new Color(142,68,173)));
        top.add(instrucciones, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Estado actual
        lblEstado = new JLabel("Listo  —  haga clic en un nodo para comenzar.");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setBorder(new EmptyBorder(5,12,5,0));
        add(lblEstado, BorderLayout.SOUTH);

        // Canvas de dibujo
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar((Graphics2D) g);
            }
        };
        canvas.setBackground(Color.WHITE);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) iniciarDrag(e);
            }
            @Override public void mouseDragged(MouseEvent e) { moverNodo(e); }
            @Override public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !seDrago) clickIzquierdo(e);
                seDrago = false; nodoDrag = null; canvas.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) clickDerecho(e);
            }
        };
        canvas.addMouseListener(ma);
        canvas.addMouseMotionListener(ma);

        add(new JScrollPane(canvas), BorderLayout.CENTER);
        actualizarPosiciones();
    }

    // ─── interacciones ──────────────────────────────────────────────────────────

    private void iniciarDrag(MouseEvent e) {
        String nodo = nodoEn(e.getX(), e.getY());
        if (nodo == null) { primerNodo = null; modoEliminar = false; canvas.repaint(); return; }
        nodoDrag  = nodo;
        Point pos = posiciones.get(nodo);
        dragOffX  = e.getX() - pos.x;
        dragOffY  = e.getY() - pos.y;
        seDrago   = false;
    }

    private void moverNodo(MouseEvent e) {
        if (nodoDrag == null) return;
        posiciones.put(nodoDrag, new Point(e.getX() - dragOffX, e.getY() - dragOffY));
        seDrago = true;
        canvas.repaint();
    }

    private void clickIzquierdo(MouseEvent e) {
        String nodo = nodoEn(e.getX(), e.getY());
        if (nodo == null) { primerNodo = null; canvas.repaint(); return; }

        if (primerNodo == null) {
            primerNodo   = nodo;
            modoEliminar = false;
            lblEstado.setText("Origen: " + nodo + "  — ahora haga clic izquierdo en el destino.");
        } else if (primerNodo.equals(nodo)) {
            primerNodo = null;
            lblEstado.setText("Selección cancelada.");
        } else {
            boolean ok = redSocial.agregarAmistad(primerNodo, nodo);
            lblEstado.setText(ok
                    ? "✅  Amistad agregada: " + primerNodo + " → " + nodo
                    : "⚠️  Esa amistad ya existe.");
            primerNodo = null;
        }
        canvas.repaint();
    }

    private void clickDerecho(MouseEvent e) {
        String nodo = nodoEn(e.getX(), e.getY());
        if (nodo == null) { primerNodo = null; modoEliminar = false; canvas.repaint(); return; }

        if (primerNodo == null) {
            primerNodo   = nodo;
            modoEliminar = true;
            lblEstado.setText("Eliminar desde: " + nodo + "  — ahora haga clic derecho en el destino.");
        } else if (primerNodo.equals(nodo)) {
            primerNodo = null; modoEliminar = false;
            lblEstado.setText("Selección cancelada.");
        } else if (modoEliminar) {
            boolean ok = redSocial.eliminarAmistad(primerNodo, nodo);
            lblEstado.setText(ok
                    ? "🗑️  Amistad eliminada: " + primerNodo + " → " + nodo
                    : "⚠️  No existe esa amistad.");
            primerNodo = null; modoEliminar = false;
        }
        canvas.repaint();
    }

    // ─── dibujo ─────────────────────────────────────────────────────────────────

    private void dibujar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Usuario> usuarios = new ArrayList<>(redSocial.getUsuarios());
        if (usuarios.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            g2.setColor(Color.GRAY);
            g2.drawString("No hay usuarios registrados.", 40, 60);
            return;
        }

        // Aristas (flechas dirigidas)
        for (Map.Entry<Usuario, List<Usuario>> entry : redSocial.getAmistades().entrySet()) {
            Point p1 = posiciones.get(entry.getKey().getUsername());
            if (p1 == null) continue;
            for (Usuario dest : entry.getValue()) {
                Point p2 = posiciones.get(dest.getUsername());
                if (p2 == null) continue;
                dibujarFlecha(g2, p1, p2);
            }
        }

        // Nodos
        for (Usuario u : usuarios) {
            Point p = posiciones.get(u.getUsername());
            if (p == null) continue;
            dibujarNodo(g2, u, p);
        }

        // Resaltar nodo seleccionado
        if (primerNodo != null) {
            Point p = posiciones.get(primerNodo);
            if (p != null) {
                g2.setColor(modoEliminar ? new Color(200,50,50,120) : new Color(50,130,200,120));
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(p.x - R - 4, p.y - R - 4, (R+4)*2, (R+4)*2);
            }
        }
    }

    private void dibujarNodo(Graphics2D g2, Usuario u, Point p) {
        // Sombra
        g2.setColor(new Color(0,0,0,30));
        g2.fillOval(p.x - R + 3, p.y - R + 3, R*2, R*2);

        // Fondo del nodo (color del grupo)
        Color bg = (u.getGrupo() != null)
                ? u.getGrupo().getColor().getColorClaro()
                : new Color(225, 232, 245);
        Color border = (u.getGrupo() != null)
                ? u.getGrupo().getColor().getColor()
                : new Color(130,150,200);

        g2.setColor(bg);
        g2.fillOval(p.x - R, p.y - R, R*2, R*2);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(p.x - R, p.y - R, R*2, R*2);

        // Avatar
        AvatarPainter.draw(g2, u.getAvatar(), p.x, p.y, R - 3);

        // Nombre del usuario
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String nombre = u.getUsername();
        int tw = fm.stringWidth(nombre);
        g2.setColor(new Color(30,30,30));
        g2.drawString(nombre, p.x - tw/2, p.y - R - 5);
    }

    private void dibujarFlecha(Graphics2D g2, Point from, Point to) {
        double dx = to.x - from.x, dy = to.y - from.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist < 1) return;
        double ux = dx/dist, uy = dy/dist;

        // Puntos de inicio y fin ajustados al borde del círculo
        int x1 = (int)(from.x + ux * (R + 2));
        int y1 = (int)(from.y + uy * (R + 2));
        int x2 = (int)(to.x   - ux * (R + 6));
        int y2 = (int)(to.y   - uy * (R + 6));

        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(80,80,100));
        g2.drawLine(x1, y1, x2, y2);

        // Cabeza de la flecha
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int    ah     = 10;
        int ax1 = (int)(x2 - ah * Math.cos(angulo - 0.45));
        int ay1 = (int)(y2 - ah * Math.sin(angulo - 0.45));
        int ax2 = (int)(x2 - ah * Math.cos(angulo + 0.45));
        int ay2 = (int)(y2 - ah * Math.sin(angulo + 0.45));

        g2.setColor(new Color(60,60,80));
        g2.fillPolygon(new int[]{x2,ax1,ax2}, new int[]{y2,ay1,ay2}, 3);
    }

    private String nodoEn(int x, int y) {
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point p = entry.getValue();
            if (Math.hypot(x - p.x, y - p.y) <= R) return entry.getKey();
        }
        return null;
    }

    // ─── posicionamiento ────────────────────────────────────────────────────────

    private void actualizarPosiciones() {
        List<Usuario> todos = new ArrayList<>(redSocial.getUsuarios());
        int n = todos.size();
        if (n == 0) return;

        int w  = Math.max(canvas.getWidth(), 700);
        int h  = Math.max(canvas.getHeight(), 550);
        int cx = w / 2, cy = h / 2;
        int r  = Math.min(w, h) / 3;

        for (int i = 0; i < n; i++) {
            String username = todos.get(i).getUsername();
            if (!posiciones.containsKey(username)) {
                double ang = 2 * Math.PI * i / n - Math.PI / 2;
                posiciones.put(username, new Point(cx + (int)(r * Math.cos(ang)),
                        cy + (int)(r * Math.sin(ang))));
            }
        }
        // Eliminar posiciones de usuarios borrados
        posiciones.keySet().retainAll(
                todos.stream().map(Usuario::getUsername).collect(java.util.stream.Collectors.toSet())
        );
    }

    @Override public void onDataChanged() { SwingUtilities.invokeLater(() -> { actualizarPosiciones(); canvas.repaint(); }); }
    @Override public void refresh()       { actualizarPosiciones(); canvas.repaint(); }

    private JLabel badge(String txt, Color bg) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(Color.WHITE);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(3,7,3,7));
        return l;
    }
}