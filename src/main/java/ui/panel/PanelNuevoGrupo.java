package ui.panel;

import Model.ColorGrupo;
import Model.Grupo;
import service.RedSocial;
import ui.ColorComboRenderer;
import ui.Refreshable;
import util.Validador;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelNuevoGrupo extends JPanel implements Refreshable {

    private final RedSocial  redSocial;
    private JTextField       txtNombre;
    private JComboBox<ColorGrupo> cbColor;

    public PanelNuevoGrupo(RedSocial redSocial) {
        this.redSocial = redSocial;
        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));
        setBorder(new EmptyBorder(30,40,30,40));

        JLabel titulo = new JLabel("Crear Nuevo Grupo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(40,80,160));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,210,230),1,true), new EmptyBorder(25,30,25,30)));
        card.setMaximumSize(new Dimension(450,300));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,5,10,5);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cbColor = new JComboBox<>(ColorGrupo.values());
        cbColor.setRenderer(new ColorComboRenderer());
        cbColor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbColor.setPreferredSize(new Dimension(200, 30));

        // Fila nombre
        g.gridx=0; g.gridy=0; g.weightx=0;
        card.add(etiqueta("Nombre del grupo *:"), g);
        g.gridx=1; g.weightx=1;
        card.add(txtNombre, g);

        // Fila color
        g.gridx=0; g.gridy=1; g.weightx=0;
        card.add(etiqueta("Color del grupo *:"), g);
        g.gridx=1; g.weightx=1;
        card.add(cbColor, g);

        // Botón
        JButton btn = new JButton("💾  Crear Grupo");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(39,174,96));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8,18,8,18));
        btn.addActionListener(e -> guardarGrupo());

        g.gridx=0; g.gridy=2; g.gridwidth=2; g.anchor=GridBagConstraints.CENTER;
        g.fill=GridBagConstraints.NONE; g.insets=new Insets(18,5,5,5);
        card.add(btn, g);

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setOpaque(false);
        north.add(titulo);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        center.setOpaque(false);
        center.add(card);

        add(north,  BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JLabel etiqueta(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void guardarGrupo() {
        String nombre = txtNombre.getText().trim();

        if (Validador.textoVacio(nombre)) {
            JOptionPane.showMessageDialog(this, "El nombre del grupo no puede estar vacío.",
                    "Error", JOptionPane.WARNING_MESSAGE); return;
        }
        if (nombre.length() < 2) {
            JOptionPane.showMessageDialog(this, "El nombre debe tener al menos 2 caracteres.",
                    "Error", JOptionPane.WARNING_MESSAGE); return;
        }

        ColorGrupo color = (ColorGrupo) cbColor.getSelectedItem();
        Grupo grupo = new Grupo(nombre, color);

        if (!redSocial.agregarGrupo(grupo)) {
            JOptionPane.showMessageDialog(this, "Ya existe un grupo con el nombre " + nombre + ".",
                    "Error", JOptionPane.WARNING_MESSAGE); return;
        }

        JOptionPane.showMessageDialog(this,
                "✅  Grupo " + nombre + " creado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        txtNombre.setText("");
        cbColor.setSelectedIndex(0);
    }

    @Override public void refresh() {}
}