package ui.panel;

import Model.Grupo;
import service.RedSocial;
import util.Validador;

import javax.swing.*;
import java.awt.*;

public class PanelNuevoGrupo extends JPanel {

    private final RedSocial redSocial;
    private JTextField txtNombre;
    private JTextField txtColor;

    public PanelNuevoGrupo(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));

        txtNombre = new JTextField();
        txtColor = new JTextField();

        form.add(new JLabel("Nombre del grupo:"));
        form.add(txtNombre);
        form.add(new JLabel("Color del grupo:"));
        form.add(txtColor);

        JButton btnGuardar = new JButton("Guardar grupo");
        btnGuardar.addActionListener(e -> guardarGrupo());

        add(form, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private void guardarGrupo() {
        String nombre = txtNombre.getText();
        String color = txtColor.getText();

        if (Validador.textoVacio(nombre) || Validador.textoVacio(color)) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        boolean agregado = redSocial.agregarGrupo(new Grupo(nombre, color));

        if (!agregado) {
            JOptionPane.showMessageDialog(this, "Ya existe un grupo con ese nombre.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Grupo agregado correctamente.");
        txtNombre.setText("");
        txtColor.setText("");
    }
}