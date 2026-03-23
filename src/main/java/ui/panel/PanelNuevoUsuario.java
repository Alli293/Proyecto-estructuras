package ui.panel;

import Model.Usuario;
import service.RedSocial;
import util.Validador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PanelNuevoUsuario extends JPanel {

    private final RedSocial redSocial;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtPrimerNombre;
    private JTextField txtPrimerApellido;
    private JTextField txtSegundoApellido;
    private JTextField txtFechaNacimiento;
    private JComboBox<String> cbAvatar;
    private JComboBox<String> cbGrupo;

    public PanelNuevoUsuario(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtPrimerNombre = new JTextField();
        txtPrimerApellido = new JTextField();
        txtSegundoApellido = new JTextField();
        txtFechaNacimiento = new JTextField();
        cbAvatar = new JComboBox<>(new String[]{"masculino", "femenino", "default"});
        cbGrupo = new JComboBox<>();

        form.add(new JLabel("Usuario:"));
        form.add(txtUsername);

        form.add(new JLabel("Contraseña:"));
        form.add(txtPassword);

        form.add(new JLabel("Primer Nombre:"));
        form.add(txtPrimerNombre);

        form.add(new JLabel("Primer Apellido:"));
        form.add(txtPrimerApellido);

        form.add(new JLabel("Segundo Apellido:"));
        form.add(txtSegundoApellido);

        form.add(new JLabel("Fecha Nacimiento (YYYY-MM-DD):"));
        form.add(txtFechaNacimiento);

        form.add(new JLabel("Avatar:"));
        form.add(cbAvatar);

        form.add(new JLabel("Grupo:"));
        form.add(cbGrupo);

        JButton btnActualizarGrupos = new JButton("Actualizar grupos");
        JButton btnGuardar = new JButton("Guardar usuario");

        btnActualizarGrupos.addActionListener(e -> cargarGrupos());

        btnGuardar.addActionListener(e -> guardarUsuario());

        JPanel abajo = new JPanel();
        abajo.add(btnActualizarGrupos);
        abajo.add(btnGuardar);

        add(form, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
    }

    private void cargarGrupos() {
        cbGrupo.removeAllItems();
        cbGrupo.addItem("Sin grupo");
        redSocial.getGrupos().forEach(g -> cbGrupo.addItem(g.getNombre()));
    }

    private void guardarUsuario() {
        try {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String primerNombre = txtPrimerNombre.getText();
            String primerApellido = txtPrimerApellido.getText();
            String segundoApellido = txtSegundoApellido.getText();
            String fechaTexto = txtFechaNacimiento.getText();
            String avatar = cbAvatar.getSelectedItem().toString();

            if (Validador.textoVacio(username) || Validador.textoVacio(password)
                    || Validador.textoVacio(primerNombre) || Validador.textoVacio(primerApellido)
                    || Validador.textoVacio(segundoApellido) || Validador.textoVacio(fechaTexto)) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            LocalDate fecha = LocalDate.parse(fechaTexto);

            Usuario usuario = new Usuario(username, password, primerNombre, primerApellido,
                    segundoApellido, fecha, avatar);

            boolean agregado = redSocial.agregarUsuario(usuario);

            if (!agregado) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese username.");
                return;
            }

            Object grupoSeleccionado = cbGrupo.getSelectedItem();
            if (grupoSeleccionado != null && !grupoSeleccionado.toString().equals("Sin grupo")) {
                redSocial.asignarGrupoAUsuario(username, grupoSeleccionado.toString());
            }

            JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
            limpiarCampos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar usuario: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtPrimerNombre.setText("");
        txtPrimerApellido.setText("");
        txtSegundoApellido.setText("");
        txtFechaNacimiento.setText("");
        cbAvatar.setSelectedIndex(0);
    }
}