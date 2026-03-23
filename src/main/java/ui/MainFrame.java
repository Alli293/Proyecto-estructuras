package ui;

import service.RedSocial;
import ui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(RedSocial redSocial) {
        setTitle("Mini Red Social");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Nuevo Usuario", new PanelNuevoUsuario(redSocial));
        tabs.addTab("Nuevo Grupo", new PanelNuevoGrupo(redSocial));
        tabs.addTab("Amistades", new PanelAmistades(redSocial));
        tabs.addTab("Red Social", new PanelRedSocial(redSocial));
        tabs.addTab("Ver Usuario", new PanelVerUsuario(redSocial));

        add(tabs, BorderLayout.CENTER);
    }
}