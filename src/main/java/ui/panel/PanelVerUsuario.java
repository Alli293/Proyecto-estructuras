package ui.panel;

import Model.Usuario;
import service.RedSocial;
import service.RedSocialListener;
import ui.AvatarPainter;
import ui.Refreshable;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVerUsuario extends JPanel implements RedSocialListener, Refreshable {

    private final RedSocial redSocial;
    private JComboBox<String> cbUsuarios;
    private JPanel            infoPanel;
    private JTable            tablaAmigos, tablaSugerencias;

    public PanelVerUsuario(RedSocial redSocial) {
        this.redSocial = redSocial;
        redSocial.addListener(this);
        setLayout(new BorderLayout(0,10));
        setBorder(new EmptyBorder(15,20,15,20));
        setBackground(new Color(245,247,250));

        // Selector de usuario
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectorPanel.setOpaque(false);
        cbUsuarios = new JComboBox<>();
        cbUsuarios.setPreferredSize(new Dimension(220,28));
        cbUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnVer = new JButton("Ver Perfil");
        btnVer.setBackground(new Color(41,128,185));
        btnVer.setForeground(Color.WHITE);
        btnVer.setFocusPainted(false);
        btnVer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVer.addActionListener(e -> mostrarUsuario());
        selectorPanel.add(new JLabel("Seleccione usuario:"));
        selectorPanel.add(cbUsuarios);
        selectorPanel.add(btnVer);
        add(selectorPanel, BorderLayout.NORTH);

        // Panel de info + tablas
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(320);
        split.setOpaque(false);

        infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,210,230),1,true), new EmptyBorder(12,12,12,12)));
        split.setLeftComponent(new JScrollPane(infoPanel));

        JPanel tablas = new JPanel(new GridLayout(2,1,0,10));
        tablas.setOpaque(false);
        tablaAmigos      = crearTabla();
        tablaSugerencias = crearTabla();
        tablas.add(wrapTabla("👥  Amigos directos",      tablaAmigos));
        tablas.add(wrapTabla("💡  Sugerencias (BFS)",    tablaSugerencias));
        split.setRightComponent(tablas);

        add(split, BorderLayout.CENTER);
        cargarUsuarios();
    }

    private void mostrarUsuario() {
        String username = (String) cbUsuarios.getSelectedItem();
        if (username == null) {
            JOptionPane.showMessageDialog(this,"Seleccione un usuario.","Aviso",JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario u = redSocial.buscarUsuario(username);
        if (u == null) return;

        // Panel de info
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout(0,10));

        // Avatar grande
        JPanel avatarWrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int cx=getWidth()/2, cy=getHeight()/2, r=50;
                Color bg = (u.getGrupo()!=null) ? u.getGrupo().getColor().getColorClaro()
                        : new Color(220,230,245);
                g2.setColor(bg); g2.fillOval(cx-r,cy-r,r*2,r*2);
                g2.setColor(new Color(100,130,200)); g2.setStroke(new BasicStroke(2));
                g2.drawOval(cx-r,cy-r,r*2,r*2);
                AvatarPainter.draw(g2, u.getAvatar(), cx, cy, r-2);
                g2.dispose();
            }
        };
        avatarWrap.setPreferredSize(new Dimension(120,120));
        avatarWrap.setBackground(Color.WHITE);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centro.setBackground(Color.WHITE);
        centro.add(avatarWrap);
        infoPanel.add(centro, BorderLayout.NORTH);

        // Datos
        JPanel datos = new JPanel(new GridLayout(0,1,0,5));
        datos.setBackground(Color.WHITE);
        datos.add(campo("Usuario",         u.getUsername()));
        datos.add(campo("Nombre completo", u.getNombreCompleto()));
        datos.add(campo("Fecha nacimiento",u.getFechaNacimiento().toString()));
        datos.add(campo("Avatar",          u.getAvatar()));
        datos.add(campo("Grupo", u.getGrupo()!=null ? u.getGrupo().getNombre() : "Sin grupo"));
        infoPanel.add(datos, BorderLayout.CENTER);
        infoPanel.revalidate(); infoPanel.repaint();

        // Tablas
        cargarTabla(tablaAmigos,      redSocial.obtenerAmigos(username));
        cargarTabla(tablaSugerencias, redSocial.obtenerSugerencias(username));
    }

    private JLabel campo(String label, String valor) {
        JLabel l = new JLabel("<html><b>" + label + ":</b>  " + valor + "</html>");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JTable crearTabla() {
        JTable t = new JTable();
        t.setRowHeight(24);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    private JPanel wrapTabla(String titulo, JTable tabla) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,210,230),1,true), new EmptyBorder(5,5,5,5)));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(new EmptyBorder(0,4,5,0));
        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    private void cargarTabla(JTable tabla, List<Usuario> datos) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Usuario","Nombre Completo","Grupo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Usuario u : datos) {
            model.addRow(new Object[]{
                    u.getUsername(),
                    u.getNombreCompleto(),
                    u.getGrupo()!=null ? u.getGrupo().getNombre() : "—"
            });
        }
        tabla.setModel(model);
    }

    private void cargarUsuarios() {
        Object sel = cbUsuarios.getSelectedItem();
        cbUsuarios.removeAllItems();
        redSocial.getUsuarios().forEach(u -> cbUsuarios.addItem(u.getUsername()));
        if (sel != null) cbUsuarios.setSelectedItem(sel);
    }

    @Override public void onDataChanged() { SwingUtilities.invokeLater(this::cargarUsuarios); }
    @Override public void refresh()       { cargarUsuarios(); }
}