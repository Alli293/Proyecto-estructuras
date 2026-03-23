package ui.panel;

import Model.Usuario;
import service.RedSocial;

import javax.swing.*;
import java.awt.*;

public class PanelAmistades extends JPanel {

    private final RedSocial redSocial;
    private JComboBox<String> cbUsuario1;
    private JComboBox<String> cbUsuario2;

    public PanelAmistades(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        cbUsuario1 = new JComboBox<>();
        cbUsuario2 = new JComboBox<>();

        JButton btnActualizar = new JButton("Actualizar usuarios");
        JButton btnAgregar = new JButton("Agregar amistad");
        JButton btnEliminar = new JButton("Eliminar amistad");

        form.add(new JLabel("Usuario 1:"));
        form.add(cbUsuario1);
        form.add(new JLabel("Usuario 2:"));
        form.add(cbUsuario2);
        form.add(btnAgregar);
        form.add(btnEliminar);

        btnActualizar.addActionListener(e -> cargarUsuarios());
        btnAgregar.addActionListener(e -> agregarAmistad());
        btnEliminar.addActionListener(e -> eliminarAmistad());

        add(btnActualizar, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
    }

    private void cargarUsuarios() {
        cbUsuario1.removeAllItems();
        cbUsuario2.removeAllItems();

        for (Usuario u : redSocial.getUsuarios()) {
            cbUsuario1.addItem(u.getUsername());
            cbUsuario2.addItem(u.getUsername());
        }
    }

    private void agregarAmistad() {
        String u1 = (String) cbUsuario1.getSelectedItem();
        String u2 = (String) cbUsuario2.getSelectedItem();

        if (u1 == null || u2 == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar usuarios.");
            return;
        }

        boolean ok = redSocial.agregarAmistad(u1, u2);
        JOptionPane.showMessageDialog(this, ok ? "Amistad agregada." : "No se pudo agregar la amistad.");
    }

    private void eliminarAmistad() {
        String u1 = (String) cbUsuario1.getSelectedItem();
        String u2 = (String) cbUsuario2.getSelectedItem();

        if (u1 == null || u2 == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar usuarios.");
            return;
        }

        boolean ok = redSocial.eliminarAmistad(u1, u2);
        JOptionPane.showMessageDialog(this, ok ? "Amistad eliminada." : "No se pudo eliminar la amistad.");
    }
}