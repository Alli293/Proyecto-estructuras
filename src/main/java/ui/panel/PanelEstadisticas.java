package ui.panel;

import Model.Usuario;
import service.RedSocial;
import service.RedSocialListener;
import ui.Refreshable;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// CAMBIAR ESTA LÍNEA en PanelEstadisticas.java:
public class PanelEstadisticas extends JPanel implements RedSocialListener, Refreshable {

    private final RedSocial redSocial;
    private JPanel contenido;

    public PanelEstadisticas(RedSocial redSocial) {
        this.redSocial = redSocial;
        redSocial.addListener(this);
        setLayout(new BorderLayout(0,10));
        setBackground(new Color(245,247,250));
        setBorder(new EmptyBorder(15,20,15,20));

        JLabel titulo = new JLabel("📊  Estadísticas de la Red Social");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(40,80,160));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setOpaque(false); north.add(titulo);
        add(north, BorderLayout.NORTH);

        contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(new Color(245,247,250));
        add(new JScrollPane(contenido), BorderLayout.CENTER);

        refresh();
    }

    @Override public void refresh()       { SwingUtilities.invokeLater(this::actualizar); }
    @Override public void onDataChanged() { SwingUtilities.invokeLater(this::actualizar); }

    private void actualizar() {
        contenido.removeAll();

        Collection<Usuario> usuarios = redSocial.getUsuarios();
        int totalUsuarios = usuarios.size();
        int totalAristas  = 0;
        int maxAmigos     = 0;
        String usuarioMasPopular = "—";

        for (Usuario u : usuarios) {
            List<Usuario> amigos = redSocial.obtenerAmigos(u.getUsername());
            totalAristas += amigos.size();
            if (amigos.size() > maxAmigos) {
                maxAmigos = amigos.size();
                usuarioMasPopular = u.getUsername() + " (" + u.getNombreCompleto() + ")";
            }
        }
        double promedio = totalUsuarios == 0 ? 0 : (double) totalAristas / totalUsuarios;

        contenido.add(tarjeta("Total de usuarios",          String.valueOf(totalUsuarios), new Color(41,128,185)));
        contenido.add(tarjeta("Total de conexiones",        String.valueOf(totalAristas),  new Color(39,174,96)));
        contenido.add(tarjeta("Usuario con más amigos",     usuarioMasPopular,             new Color(230,126,34)));
        contenido.add(tarjeta("Promedio de amigos/usuario", String.format("%.2f", promedio),new Color(142,68,173)));

        // Tabla de usuarios ordenada por número de amigos
        if (totalUsuarios > 0) {
            contenido.add(Box.createVerticalStrut(15));
            contenido.add(buildRanking());
        }

        contenido.revalidate();
        contenido.repaint();
    }

    private JPanel tarjeta(String label, String valor, Color color) {
        JPanel p = new JPanel(new BorderLayout(10,0));
        p.setBackground(color);
        p.setBorder(new EmptyBorder(14,20,14,20));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(Color.WHITE);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLbl.setForeground(new Color(255,255,255,200));

        JPanel texts = new JPanel(new GridLayout(2,1));
        texts.setOpaque(false);
        texts.add(lblVal);
        texts.add(lblLbl);
        p.add(texts, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(4,0,4,0));
        wrap.add(p);
        return wrap;
    }

    private JPanel buildRanking() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,210,230),1,true), new EmptyBorder(10,10,10,10)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lbl = new JLabel("Ranking de conectividad");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"Usuario","Nombre","Amigos salientes","Grupo"};
        Object[][] rows = redSocial.getUsuarios().stream()
                .sorted(Comparator.comparingInt(u -> -redSocial.obtenerAmigos(u.getUsername()).size()))
                .map(u -> new Object[]{
                        u.getUsername(), u.getNombreCompleto(),
                        redSocial.obtenerAmigos(u.getUsername()).size(),
                        u.getGrupo()!=null ? u.getGrupo().getNombre() : "—"
                }).toArray(Object[][]::new);

        JTable tabla = new JTable(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(4,0,4,0));
        wrap.add(p);
        return wrap;
    }
}