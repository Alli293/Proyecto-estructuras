package ui.panel;

import Model.Usuario;
import service.RedSocial;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PanelRedSocial extends JPanel {

    private final RedSocial redSocial;

    public PanelRedSocial(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JButton btnRefrescar = new JButton("Refrescar grafo");
        btnRefrescar.addActionListener(e -> repaint());

        add(btnRefrescar, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Usuario> usuarios = new ArrayList<>(redSocial.getUsuarios());
        Map<Usuario, Point> posiciones = new HashMap<>();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 - 30;
        int radio = 200;
        int n = usuarios.size();

        if (n == 0) {
            g.drawString("No hay usuarios registrados.", 50, 50);
            return;
        }

        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n;
            int x = centerX + (int) (radio * Math.cos(angulo));
            int y = centerY + (int) (radio * Math.sin(angulo));
            posiciones.put(usuarios.get(i), new Point(x, y));
        }

        g.setColor(Color.BLACK);
        for (Map.Entry<Usuario, List<Usuario>> entry : redSocial.getAmistades().entrySet()) {
            Usuario origen = entry.getKey();
            Point p1 = posiciones.get(origen);

            for (Usuario destino : entry.getValue()) {
                Point p2 = posiciones.get(destino);
                if (p1 != null && p2 != null) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        for (Usuario usuario : usuarios) {
            Point p = posiciones.get(usuario);

            Color colorNodo = Color.LIGHT_GRAY;
            if (usuario.getGrupo() != null) {
                String color = usuario.getGrupo().getColor().toLowerCase();
                switch (color) {
                    case "verde" -> colorNodo = Color.GREEN;
                    case "rojo" -> colorNodo = Color.RED;
                    case "azul" -> colorNodo = Color.CYAN;
                    case "amarillo" -> colorNodo = Color.YELLOW;
                    default -> colorNodo = Color.LIGHT_GRAY;
                }
            }

            g.setColor(colorNodo);
            g.fillOval(p.x - 25, p.y - 25, 50, 50);

            g.setColor(Color.BLACK);
            g.drawOval(p.x - 25, p.y - 25, 50, 50);
            g.drawString(usuario.getUsername(), p.x - 25, p.y - 30);
            g.drawString(usuario.getAvatar(), p.x - 20, p.y + 5);
        }
    }
}