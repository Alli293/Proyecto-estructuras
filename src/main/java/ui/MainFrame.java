package ui;

import service.RedSocial;
import ui.panel.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(RedSocial redSocial) {
        setTitle("Mini Red Social — CENFOTEC");
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        PanelNuevoUsuario  pNuevoUsuario  = new PanelNuevoUsuario(redSocial);
        PanelNuevoGrupo    pNuevoGrupo    = new PanelNuevoGrupo(redSocial);
        PanelRedSocial     pRedSocial     = new PanelRedSocial(redSocial);
        PanelVerUsuario    pVerUsuario    = new PanelVerUsuario(redSocial);
        PanelEstadisticas  pEstadisticas  = new PanelEstadisticas(redSocial);

        tabs.addTab("1. Nuevo Usuario",  pNuevoUsuario);
        tabs.addTab("2. Agregar Grupo",  pNuevoGrupo);
        tabs.addTab("3. Red Social",     pRedSocial);
        tabs.addTab("4. Ver Usuario",    pVerUsuario);
        tabs.addTab("5. Estadísticas",   pEstadisticas);

        // Auto-refresh cuando el usuario navega entre pestañas
        tabs.addChangeListener(e -> {
            Component sel = tabs.getSelectedComponent();
            if (sel instanceof Refreshable r) r.refresh();
        });

        add(tabs, BorderLayout.CENTER);
    }
}