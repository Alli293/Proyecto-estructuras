package ui;
import java.awt.*;
import java.awt.geom.*;

public class AvatarPainter {
    /**
     * Dibuja un avatar dentro de un círculo centrado en (cx,cy) con radio r.
     * tipo: "masculino", "femenino", o cualquier otro valor = predeterminado.
     */
    public static void draw(Graphics2D g2, String tipo, int cx, int cy, int r) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape oldClip = g2.getClip();
        g2.setClip(new Ellipse2D.Float(cx - r + 2, cy - r + 2, (r-2)*2, (r-2)*2));

        int headR  = r * 9 / 25;
        int headCY = cy - r/2 + headR/2 + 2;

        // Color de cuerpo según tipo
        Color bodyMain, bodyDark, hairC;
        switch (tipo == null ? "" : tipo.toLowerCase()) {
            case "masculino" -> { bodyMain = new Color(60,100,170); bodyDark = new Color(35,65,130);  hairC = new Color(60,40,20);  }
            case "femenino"  -> { bodyMain = new Color(190,65,95);  bodyDark = new Color(140,40,70);  hairC = new Color(60,40,20);  }
            default          -> { bodyMain = new Color(120,120,120);bodyDark = new Color(80,80,80);   hairC = new Color(100,80,60); }
        }

        // Cabeza
        g2.setColor(new Color(255, 218, 185));
        g2.fillOval(cx - headR, headCY - headR, headR*2, headR*2);

        // Cabello (arco superior)
        g2.setColor(hairC);
        g2.fillArc(cx - headR - 2, headCY - headR - 3, headR*2+4, headR+4, 0, 180);

        // Cuerpo
        int bodyTop  = headCY + headR - 3;
        int bodyH    = r + r/2;
        int bodyW    = r * 4 / 3;
        if ("femenino".equals(tipo)) {
            // Trapecio (falda/blazer)
            int[] xp = {cx - bodyW/3, cx + bodyW/3, cx + bodyW/2, cx - bodyW/2};
            int[] yp = {bodyTop, bodyTop, bodyTop + bodyH, bodyTop + bodyH};
            g2.setColor(bodyMain);
            g2.fillPolygon(xp, yp, 4);
            g2.setColor(bodyDark);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawPolygon(xp, yp, 4);
        } else {
            // Rectángulo (traje)
            int bw = bodyW * 3/5, bx = cx - bw/2;
            g2.setColor(bodyMain);
            g2.fillRoundRect(bx, bodyTop, bw, bodyH, 6, 6);
            // Camisa blanca central
            g2.setColor(new Color(240,240,240));
            g2.fillRect(cx - 4, bodyTop, 8, bodyH/2);
            g2.setColor(bodyDark);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(bx, bodyTop, bw, bodyH, 6, 6);
        }

        // Contorno de cabeza
        g2.setColor(new Color(180, 140, 110));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(cx - headR, headCY - headR, headR*2, headR*2);

        g2.setClip(oldClip);
    }
}