package ui.panel;

import Model.Usuario;
import service.RedSocial;
import service.RedSocialListener;
import ui.AvatarPainter;
import ui.Refreshable;
import util.Validador;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;

public class PanelNuevoUsuario extends JPanel implements RedSocialListener, Refreshable {

    private final RedSocial redSocial;

    private JTextField     txtUsername, txtPrimerNombre, txtPrimerApellido,
            txtSegundoApellido, txtFechaNacimiento;
    private JPasswordField txtPassword;
    private JComboBox<String> cbGrupo;
    private JRadioButton   rbMasculino, rbFemenino, rbPredeterminado;
    private AvatarPreview  avatarPreview;

    // ─── constructor ────────────────────────────────────────────────────────────

    public PanelNuevoUsuario(RedSocial redSocial) {
        this.redSocial = redSocial;
        redSocial.addListener(this);
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(20,30,20,30));
        setBackground(new Color(245,247,250));

        add(buildTitulo(),   BorderLayout.NORTH);
        add(buildFormArea(), BorderLayout.CENTER);
    }

    // ─── construcción UI ────────────────────────────────────────────────────────

    private JPanel buildTitulo() {
        JLabel lbl = new JLabel("Registro de Nuevo Usuario");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(40,80,160));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        p.add(lbl);
        return p;
    }

    private JPanel buildFormArea() {
        JPanel outer = new JPanel(new BorderLayout(20,0));
        outer.setOpaque(false);
        outer.add(buildForm(),          BorderLayout.CENTER);
        outer.add(buildAvatarSection(), BorderLayout.EAST);
        return outer;
    }

    private JPanel buildForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,210,230), 1, true),
                new EmptyBorder(20,20,20,20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(7, 5, 7, 5);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        txtUsername        = new JTextField(22);
        txtPassword        = new JPasswordField(22);
        txtPrimerNombre    = new JTextField(22);
        txtPrimerApellido  = new JTextField(22);
        txtSegundoApellido = new JTextField(22);
        txtFechaNacimiento = new JTextField(22);
        txtFechaNacimiento.setToolTipText("Formato: YYYY-MM-DD");

        cbGrupo = new JComboBox<>();
        cbGrupo.setPreferredSize(new Dimension(200, 28));

        agregarFila(card, g, 0, "Usuario *:",           txtUsername);
        agregarFila(card, g, 1, "Contraseña * (≥6) :",  txtPassword);
        agregarFila(card, g, 2, "Primer Nombre *:",      txtPrimerNombre);
        agregarFila(card, g, 3, "Primer Apellido *:",    txtPrimerApellido);
        agregarFila(card, g, 4, "Segundo Apellido *:",   txtSegundoApellido);
        agregarFila(card, g, 5, "Fecha Nacimiento *:",   txtFechaNacimiento);
        agregarFila(card, g, 6, "Grupo (opcional):",     cbGrupo);

        // Botón guardar
        JButton btn = new JButton("💾  Guardar Usuario");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(41,128,185));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8,18,8,18));
        btn.addActionListener(e -> guardarUsuario());

        g.gridx=0; g.gridy=8; g.gridwidth=2; g.fill=GridBagConstraints.NONE;
        g.anchor=GridBagConstraints.CENTER; g.insets=new Insets(16,5,5,5);
        card.add(btn, g);

        return card;
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String label, JComponent campo) {
        g.gridx=0; g.gridy=fila; g.gridwidth=1; g.weightx=0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, g);
        g.gridx=1; g.weightx=1;
        p.add(campo, g);
    }

    private JPanel buildAvatarSection() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(180,0));

        JLabel titulo = new JLabel("Avatar *", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(new Color(60,60,60));
        p.add(titulo, BorderLayout.NORTH);

        avatarPreview = new AvatarPreview("predeterminado");
        p.add(avatarPreview, BorderLayout.CENTER);

        ButtonGroup bg = new ButtonGroup();
        rbMasculino     = new JRadioButton("Masculino",     false);
        rbFemenino      = new JRadioButton("Femenino",      false);
        rbPredeterminado= new JRadioButton("Predeterminado",true);

        for (JRadioButton rb : new JRadioButton[]{rbMasculino, rbFemenino, rbPredeterminado}) {
            bg.add(rb);
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rb.setOpaque(false);
            rb.addActionListener(e -> avatarPreview.setTipo(obtenerAvatar()));
        }

        JPanel radioPanel = new JPanel(new GridLayout(3,1,0,4));
        radioPanel.setOpaque(false);
        radioPanel.add(rbMasculino);
        radioPanel.add(rbFemenino);
        radioPanel.add(rbPredeterminado);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap.setOpaque(false);
        wrap.add(radioPanel);
        p.add(wrap, BorderLayout.SOUTH);
        return p;
    }

    // ─── lógica ─────────────────────────────────────────────────────────────────

    private String obtenerAvatar() {
        if (rbMasculino.isSelected())  return "masculino";
        if (rbFemenino.isSelected())   return "femenino";
        return "predeterminado";
    }

    private void guardarUsuario() {
        String username   = txtUsername.getText().trim();
        String password   = new String(txtPassword.getPassword());
        String nombre     = txtPrimerNombre.getText().trim();
        String apellido1  = txtPrimerApellido.getText().trim();
        String apellido2  = txtSegundoApellido.getText().trim();
        String fechaTxt   = txtFechaNacimiento.getText().trim();
        String avatar     = obtenerAvatar();

        // Validaciones detalladas
        if (!Validador.esUsernameValido(username)) {
            mostrarError("El usuario debe tener entre 3-20 caracteres alfanuméricos (letras, números, _ o .)");
            txtUsername.requestFocus(); return;
        }
        if (!Validador.esPasswordValida(password)) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            txtPassword.requestFocus(); return;
        }
        if (!Validador.esSoloLetras(nombre)) {
            mostrarError("El primer nombre solo puede contener letras."); return;
        }
        if (!Validador.esSoloLetras(apellido1)) {
            mostrarError("El primer apellido solo puede contener letras."); return;
        }
        if (!Validador.esSoloLetras(apellido2)) {
            mostrarError("El segundo apellido solo puede contener letras."); return;
        }
        if (!Validador.esFechaValida(fechaTxt)) {
            mostrarError("Fecha inválida. Use el formato YYYY-MM-DD  (ej: 2000-05-20)"); return;
        }
        if (!Validador.esFechaEnPasado(fechaTxt)) {
            mostrarError("La fecha de nacimiento debe ser anterior a hoy."); return;
        }

        LocalDate fecha = LocalDate.parse(fechaTxt);
        Usuario usuario = new Usuario(username, password, nombre, apellido1, apellido2, fecha, avatar);

        if (!redSocial.agregarUsuario(usuario)) {
            mostrarError("Ya existe un usuario con el nombre de usuario " + username + "");
            return;
        }

        String grupoSeleccionado = (String) cbGrupo.getSelectedItem();
        if (grupoSeleccionado != null && !grupoSeleccionado.equals("Sin grupo")) {
            redSocial.asignarGrupoAUsuario(username, grupoSeleccionado);
        }

        JOptionPane.showMessageDialog(this,
                " Usuario " + username + "registrado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error de validación", JOptionPane.WARNING_MESSAGE);
    }

    private void limpiar() {
        txtUsername.setText(""); txtPassword.setText(""); txtPrimerNombre.setText("");
        txtPrimerApellido.setText(""); txtSegundoApellido.setText(""); txtFechaNacimiento.setText("");
        cbGrupo.setSelectedIndex(0);
        rbPredeterminado.setSelected(true);
        avatarPreview.setTipo("predeterminado");
    }

    @Override public void refresh()        { cargarGrupos(); }
    @Override public void onDataChanged()  { SwingUtilities.invokeLater(this::cargarGrupos); }

    private void cargarGrupos() {
        Object seleccionado = cbGrupo.getSelectedItem();
        cbGrupo.removeAllItems();

        if (redSocial.getGrupos().isEmpty()) {
            cbGrupo.addItem("⚠ Cree un grupo primero");
            cbGrupo.setForeground(new Color(180, 60, 60));
            cbGrupo.setEnabled(false);
        } else {
            cbGrupo.setForeground(new Color(30, 30, 30));
            cbGrupo.setEnabled(true);
            cbGrupo.addItem("Sin grupo");
            redSocial.getGrupos().forEach(g -> cbGrupo.addItem(g.getNombre()));
            if (seleccionado != null) cbGrupo.setSelectedItem(seleccionado);
        }
    }
    // ─── Inner class: preview de avatar ─────────────────────────────────────────

    static class AvatarPreview extends JPanel {
        private String tipo;

        AvatarPreview(String tipo) {
            this.tipo = tipo;
            setPreferredSize(new Dimension(120,120));
            setBackground(new Color(240,244,252));
            setBorder(new LineBorder(new Color(180,200,230), 1, true));
        }

        void setTipo(String tipo) { this.tipo = tipo; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int cx = getWidth()/2, cy = getHeight()/2;
            int r  = Math.min(cx, cy) - 8;
            // fondo del nodo
            g2.setColor(new Color(200,215,240));
            g2.fillOval(cx-r, cy-r, r*2, r*2);
            g2.setColor(new Color(100,130,190));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(cx-r, cy-r, r*2, r*2);
            AvatarPainter.draw(g2, tipo, cx, cy, r);
            g2.dispose();
        }
    }
}