package ui.panel;

import Model.Usuario;
import service.RedSocial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVerUsuario extends JPanel {

    private final RedSocial redSocial;
    private JComboBox<String> cbUsuarios;
    private JTextArea txtInfo;
    private JTable tablaAmigos;
    private JTable tablaSugerencias;

    public PanelVerUsuario(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JPanel arriba = new JPanel();
        cbUsuarios = new JComboBox<>();
        JButton btnActualizar = new JButton("Actualizar usuarios");
        JButton btnVer = new JButton("Ver usuario");

        btnActualizar.addActionListener(e -> cargarUsuarios());
        btnVer.addActionListener(e -> mostrarUsuario());

        arriba.add(new JLabel("Usuario:"));
        arriba.add(cbUsuarios);
        arriba.add(btnActualizar);
        arriba.add(btnVer);

        txtInfo = new JTextArea(6, 50);
        txtInfo.setEditable(false);

        tablaAmigos = new JTable();
        tablaSugerencias = new JTable();

        JPanel centro = new JPanel(new GridLayout(3, 1));
        centro.add(new JScrollPane(txtInfo));
        centro.add(new JScrollPane(tablaAmigos));
        centro.add(new JScrollPane(tablaSugerencias));

        add(arriba, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    private void cargarUsuarios() {
        cbUsuarios.removeAllItems();
        for (Usuario u : redSocial.getUsuarios()) {
            cbUsuarios.addItem(u.getUsername());
        }
    }

    private void mostrarUsuario() {
        String username = (String) cbUsuarios.getSelectedItem();

        if (username == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        Usuario u = redSocial.buscarUsuario(username);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
            return;
        }

        txtInfo.setText(
                "Username: " + u.getUsername() + "\n" +
                        "Nombre completo: " + u.getNombreCompleto() + "\n" +
                        "Fecha nacimiento: " + u.getFechaNacimiento() + "\n" +
                        "Avatar: " + u.getAvatar() + "\n" +
                        "Grupo: " + (u.getGrupo() != null ? u.getGrupo().getNombre() : "Sin grupo")
        );

        cargarTabla(tablaAmigos, redSocial.obtenerAmigos(username), "Amigos");
        cargarTabla(tablaSugerencias, redSocial.obtenerSugerencias(username), "Sugerencias");
    }

    private void cargarTabla(JTable tabla, List<Usuario> datos, String titulo) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(titulo);
        model.addColumn("Nombre completo");

        for (Usuario u : datos) {
            model.addRow(new Object[]{u.getUsername(), u.getNombreCompleto()});
        }

        tabla.setModel(model);
    }
}