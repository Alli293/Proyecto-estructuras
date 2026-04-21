package ui.panel;

import Model.Usuario;
import service.RedSocial;
import service.RedSocialListener;
import ui.Refreshable;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Collection;

/**
 * Panel para gestionar amistades manualmente (asignar usuario a grupo también).
 * Se auto-actualiza cada vez que cambian los datos gracias a RedSocialListener.
 *
 * PUNTO 15: este panel ya NO necesita botón "Actualizar" manual.
 * RedSocial llama onDataChanged() automáticamente.
 */
public class PanelAmistades extends JPanel implements RedSocialListener, Refreshable {

    private final RedSocial       redSocial;
    private JComboBox<String>     cbUsuario1;
    private JComboBox<String>     cbUsuario2;
    private JComboBox<String>     cbUsuarioGrupo;
    private JComboBox<String>     cbGrupo;

    public PanelAmistades(RedSocial redSocial) {
        this.redSocial = redSocial;

        // ── PUNTO 15: registrar este panel como listener ──────────────────────
        // Desde ahora, cada vez que se agrega un usuario/grupo en otro panel,
        // RedSocial llamará onDataChanged() y los ComboBox se actualizan solos.
        redSocial.addListener(this);
        // ─────────────────────────────────────────────────────────────────────

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Gestión de Amistades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(40, 80, 160));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setOpaque(false);
        north.add(titulo);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setOpaque(false);
        center.add(buildCardAmistad());
        center.add(buildCardGrupo());
        add(center, BorderLayout.CENTER);

        // Carga inicial
        cargarUsuarios();
    }

    // ─── construcción UI ────────────────────────────────────────────────────────

    private JPanel buildCardAmistad() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 210, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 5, 8, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        cbUsuario1 = new JComboBox<>();
        cbUsuario2 = new JComboBox<>();
        estilizarCombo(cbUsuario1);
        estilizarCombo(cbUsuario2);

        // Título de sección
        JLabel lbl = new JLabel("Agregar / Eliminar Amistad");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(41, 128, 185));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(lbl, g);
        g.gridwidth = 1;

        agregarFila(card, g, 1, "Usuario origen:", cbUsuario1);
        agregarFila(card, g, 2, "Usuario destino:", cbUsuario2);

        // Botones
        JButton btnAgregar  = boton("➕  Agregar amistad",  new Color(39, 174, 96));
        JButton btnEliminar = boton("🗑️  Eliminar amistad", new Color(192, 57, 43));

        btnAgregar.addActionListener(e -> {
            String u1 = (String) cbUsuario1.getSelectedItem();
            String u2 = (String) cbUsuario2.getSelectedItem();
            if (u1 == null || u2 == null || u1.equals(u2)) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona dos usuarios distintos.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (redSocial.agregarAmistad(u1, u2)) {
                JOptionPane.showMessageDialog(this,
                        "✅  Amistad " + u1 + " → " + u2 + " agregada.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Esa amistad ya existe o los usuarios no son válidos.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            String u1 = (String) cbUsuario1.getSelectedItem();
            String u2 = (String) cbUsuario2.getSelectedItem();
            if (u1 == null || u2 == null) return;
            if (redSocial.eliminarAmistad(u1, u2)) {
                JOptionPane.showMessageDialog(this,
                        "🗑️  Amistad " + u1 + " → " + u2 + " eliminada.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No existe esa amistad.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        g.gridx = 0; g.gridy = 3; g.gridwidth = 1; g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.CENTER; g.insets = new Insets(14, 5, 5, 5);
        card.add(btnAgregar, g);
        g.gridx = 1;
        card.add(btnEliminar, g);

        return card;
    }

    private JPanel buildCardGrupo() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 210, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 5, 8, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        cbUsuarioGrupo = new JComboBox<>();
        cbGrupo        = new JComboBox<>();
        estilizarCombo(cbUsuarioGrupo);
        estilizarCombo(cbGrupo);

        JLabel lbl = new JLabel("Asignar Usuario a Grupo");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(39, 174, 96));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(lbl, g);
        g.gridwidth = 1;

        agregarFila(card, g, 1, "Usuario:", cbUsuarioGrupo);
        agregarFila(card, g, 2, "Grupo:",   cbGrupo);

        JButton btnAsignar = boton("✔  Asignar Grupo", new Color(41, 128, 185));
        btnAsignar.addActionListener(e -> {
            String user  = (String) cbUsuarioGrupo.getSelectedItem();
            String grupo = (String) cbGrupo.getSelectedItem();
            if (user == null || grupo == null || grupo.equals("Sin grupos creados")) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un usuario y un grupo válido.", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (redSocial.asignarGrupoAUsuario(user, grupo)) {
                JOptionPane.showMessageDialog(this,
                        "✅  " + user + " asignado al grupo " + grupo + ".", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo asignar el grupo.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.CENTER; g.insets = new Insets(14, 5, 5, 5);
        card.add(btnAsignar, g);

        return card;
    }

    // ─── helpers UI ─────────────────────────────────────────────────────────────

    private void agregarFila(JPanel p, GridBagConstraints g, int fila,
                             String label, JComponent campo) {
        g.gridx = 0; g.gridy = fila; g.gridwidth = 1; g.weightx = 0;
        g.fill  = GridBagConstraints.NONE;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(campo, g);
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        return b;
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setPreferredSize(new Dimension(180, 28));
    }

    // ─── PUNTO 15: carga de datos (se llama automáticamente) ────────────────────

    /**
     * Recarga los ComboBox de usuarios y grupos.
     * Se llama:
     *   - Al crear el panel (constructor)
     *   - Automáticamente via onDataChanged() cuando cambia algo en RedSocial
     *   - Al cambiar de pestaña via refresh()
     */
    private void cargarUsuarios() {
        // Guardar selecciones actuales para no perderlas
        Object selU1    = cbUsuario1.getSelectedItem();
        Object selU2    = cbUsuario2.getSelectedItem();
        Object selUG    = cbUsuarioGrupo.getSelectedItem();
        Object selGrupo = cbGrupo.getSelectedItem();

        // Recargar usuarios
        cbUsuario1.removeAllItems();
        cbUsuario2.removeAllItems();
        cbUsuarioGrupo.removeAllItems();

        Collection<Usuario> usuarios = redSocial.getUsuarios();
        for (Usuario u : usuarios) {
            cbUsuario1.addItem(u.getUsername());
            cbUsuario2.addItem(u.getUsername());
            cbUsuarioGrupo.addItem(u.getUsername());
        }

        // Recargar grupos
        cbGrupo.removeAllItems();
        if (redSocial.getGrupos().isEmpty()) {
            cbGrupo.addItem("Sin grupos creados");
        } else {
            redSocial.getGrupos().forEach(g -> cbGrupo.addItem(g.getNombre()));
        }

        // Restaurar selecciones si siguen siendo válidas
        if (selU1    != null) cbUsuario1.setSelectedItem(selU1);
        if (selU2    != null) cbUsuario2.setSelectedItem(selU2);
        if (selUG    != null) cbUsuarioGrupo.setSelectedItem(selUG);
        if (selGrupo != null) cbGrupo.setSelectedItem(selGrupo);
    }

    // ─── implementación de interfaces ───────────────────────────────────────────

    /**
     * PUNTO 15: RedSocial llama este método automáticamente cada vez que
     * se agrega un usuario, grupo o amistad. No necesitas botón "Actualizar".
     */
    @Override
    public void onDataChanged() {
        // SwingUtilities.invokeLater garantiza que la actualización de UI
        // ocurre en el hilo correcto de Swing
        SwingUtilities.invokeLater(this::cargarUsuarios);
    }

    /**
     * Se llama cuando el usuario navega a esta pestaña (desde MainFrame).
     */
    @Override
    public void refresh() {
        cargarUsuarios();
    }
}