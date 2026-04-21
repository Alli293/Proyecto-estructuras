package ui.panel;


import Model.Grupo;
import Model.Usuario;
import service.RedSocial;

import javax.swing.*;
import java.awt.*;

public class PanelAsignarGrupo extends JPanel {

    private final RedSocial redSocial;
    private JComboBox<String> cbUsuarios;
    private JComboBox<String> cbGrupos;

    public PanelAsignarGrupo(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Asignar grupo a usuario", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(3, 2, 10, 10));

        cbUsuarios = new JComboBox<>();
        cbGrupos = new JComboBox<>();

        JButton btnActualizar = new JButton("Actualizar listas");
        JButton btnAsignar = new JButton("Asignar grupo");

        panelCentral.add(new JLabel("Usuario:"));
        panelCentral.add(cbUsuarios);

        panelCentral.add(new JLabel("Grupo:"));
        panelCentral.add(cbGrupos);

        panelCentral.add(btnActualizar);
        panelCentral.add(btnAsignar);

        add(panelCentral, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> cargarDatos());
        btnAsignar.addActionListener(e -> asignarGrupo());

        cargarDatos();
    }

    private void cargarDatos() {
        cbUsuarios.removeAllItems();
        cbGrupos.removeAllItems();

        for (Usuario usuario : redSocial.getUsuarios()) {
            cbUsuarios.addItem(usuario.getUsername());
        }

        for (Grupo grupo : redSocial.getGrupos()) {
            cbGrupos.addItem(grupo.getNombre());
        }
    }

    private void asignarGrupo() {
        String username = (String) cbUsuarios.getSelectedItem();
        String nombreGrupo = (String) cbGrupos.getSelectedItem();

        if (username == null || nombreGrupo == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un usuario y un grupo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean asignado = redSocial.asignarGrupoAUsuario(username, nombreGrupo);

        if (asignado) {
            JOptionPane.showMessageDialog(this,
                    "Grupo asignado correctamente al usuario.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo asignar el grupo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}