package ui;
import Model.ColorGrupo;
import javax.swing.*;
import java.awt.*;

public class ColorComboRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JLabel lbl = (JLabel) super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        if (value instanceof ColorGrupo cg) {
            lbl.setText("  " + cg.getNombre());
            lbl.setIcon(new ColorIcon(cg.getColor()));
        }
        return lbl;
    }

    static class ColorIcon implements javax.swing.Icon {
        private final Color c;
        ColorIcon(Color c) { this.c = c; }
        public void paintIcon(Component comp, Graphics g, int x, int y) {
            g.setColor(c); g.fillRoundRect(x,y,20,14,4,4);
            g.setColor(Color.DARK_GRAY); g.drawRoundRect(x,y,20,14,4,4);
        }
        public int getIconWidth()  { return 22; }
        public int getIconHeight() { return 16; }
    }
}