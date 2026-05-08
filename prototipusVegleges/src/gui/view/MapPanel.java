package gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Map;
import javax.swing.JPanel;
import gui.GameLayout;
import jarmuvek.Auto;
import jarmuvek.Busz;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import motor.GameState;
import motor.GameStateListener;
import motor.NamedEntity;
import terkep.Sav;
import terkep.Ut;

/**
 * A jatekter rajzolasaert felelos panel (BorderLayout.CENTER).
 *
 * "Pull" oldal: a paintComponent() minden hivasakor a model aktualis
 * allapotabol olvas (csomopontok, sávok, jarmuvek, ho/jeg ertekek).
 *
 * "Push" oldal: a GameStateListener-en keresztul jelzi a model, hogy
 * frissulnie kell -- ezt egyszeruen egy repaint() hivassal kezeljuk.
 *
 * Tartalmaz egy public vehicleAt() metodust, amit a MapController hasznal a
 * kattintas-talalatok feloldasara (find-by-coordinate).
 */
public final class MapPanel extends JPanel implements GameStateListener {
    private final GameState state;

    public MapPanel(GameState state) {
        this.state = state;
        setBackground(new Color(225, 238, 248));
        setPreferredSize(new Dimension(GameLayout.MAP_WIDTH, GameLayout.MAP_HEIGHT));
    }

    @Override
    public void onStateChanged(GameState state) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Eloszor utak (sávokkal), aztan csomopontok, vegul jarmuvek -- ez biztositja
        // a helyes Z-rendet (jarmu mindig latszik a csomopont es ut tetejen)
        for (Ut ut : state.getAllUtak()) {
            drawRoad(g2, ut);
        }
        for (Map.Entry<String, Point> entry : GameLayout.allNodes().entrySet()) {
            drawNode(g2, entry.getKey(), entry.getValue());
        }
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            Point pos = computeVehiclePosition(v);
            if (pos == null) continue;
            drawVehicle(g2, v, pos, isSelected(v));
        }

        // Jelmagyarazat a jobb felso sarokban
        drawLegend(g2);

        g2.dispose();
    }

    private void drawLegend(Graphics2D g2) {
        int x = getWidth() - 175;
        int y = 12;
        int w = 165;
        int h = 88;
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(new Color(120, 130, 150));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, w, h, 8, 8);

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(40, 40, 60));
        g2.drawString("Jelmagyarázat", x + 10, y + 14);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int rowY = y + 30;

        // Hokotro
        int[] hxs = { x + 18, x + 12, x + 24 };
        int[] hys = { rowY - 6, rowY + 4, rowY + 4 };
        g2.setColor(new Color(50, 95, 220));
        g2.fillPolygon(hxs, hys, 3);
        g2.setColor(Color.BLACK);
        g2.drawString("Hókotró", x + 32, rowY + 2);

        // Busz
        rowY += 16;
        g2.setColor(new Color(220, 60, 60));
        g2.fillRect(x + 12, rowY - 4, 14, 8);
        g2.setColor(Color.BLACK);
        g2.drawRect(x + 12, rowY - 4, 14, 8);
        g2.drawString("Busz", x + 32, rowY + 2);

        // Auto
        rowY += 16;
        g2.setColor(new Color(245, 220, 70));
        g2.fillRect(x + 12, rowY - 4, 14, 8);
        g2.setColor(Color.BLACK);
        g2.drawRect(x + 12, rowY - 4, 14, 8);
        g2.drawString("Autó (NPC)", x + 32, rowY + 2);

        // Kijelolt
        rowY += 16;
        g2.setColor(new Color(255, 110, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + 11, rowY - 7, 16, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Kijelölt", x + 32, rowY + 2);
    }

    // ----- Rajzolasi segedmetodusok -----

    private void drawRoad(Graphics2D g2, Ut ut) {
        Point a = GameLayout.nodePosition(ut.nodeA);
        Point b = GameLayout.nodePosition(ut.nodeB);
        if (a == null || b == null) return;

        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double len = Math.hypot(dx, dy);
        if (len == 0) return;
        double nx = -dy / len;
        double ny = dx / len;

        int laneCount = ut.savSzam();
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(GameLayout.LANE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < laneCount; i++) {
            double offset = (i - (laneCount - 1) / 2.0) * (GameLayout.LANE_WIDTH + GameLayout.LANE_GAP);
            int ax = (int) (a.x + nx * offset);
            int ay = (int) (a.y + ny * offset);
            int bx = (int) (b.x + nx * offset);
            int by = (int) (b.y + ny * offset);

            Sav sav = ut.sav(i);
            g2.setColor(laneColor(sav));
            g2.drawLine(ax, ay, bx, by);
        }
        g2.setStroke(originalStroke);

        // Felirat a kozepre
        int mx = (a.x + b.x) / 2;
        int my = (a.y + b.y) / 2;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(60, 60, 80));
        g2.drawString(ut.name(), mx + 6, my - 6);
    }

    private Color laneColor(Sav sav) {
        // A model leirasban definialt szinrend:
        // jeges = vilagoskek, havas = feher, sozott = szurke,
        // zuzottko = barna, sima = fekete
        if (sav.ice > 0) return new Color(170, 205, 235);
        if (sav.ho > 0) return new Color(245, 245, 245);
        if (sav.soHatralevoIdeje > 0) return new Color(170, 170, 175);
        if (sav.zuzalekHatralevoIdeje > 0) return new Color(140, 90, 50);
        return new Color(40, 40, 45);
    }

    private void drawNode(Graphics2D g2, String name, Point pos) {
        int r = GameLayout.NODE_RADIUS;
        Color fill;
        if (name.equalsIgnoreCase("Telephely")) {
            fill = new Color(255, 220, 60);
        } else if (name.toLowerCase().startsWith("vegallomas")) {
            fill = new Color(75, 200, 110);
        } else {
            fill = new Color(210, 210, 215);
        }

        g2.setColor(fill);
        g2.fillOval(pos.x - r, pos.y - r, r * 2, r * 2);
        g2.setColor(new Color(40, 40, 60));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(pos.x - r, pos.y - r, r * 2, r * 2);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        String label = GameLayout.displayLabel(name);
        int tx = pos.x - fm.stringWidth(label) / 2;
        int ty = pos.y - r - 6;
        g2.drawString(label, tx, ty);
    }

    private Point computeVehiclePosition(Jarmu v) {
        if (v.currentUt == null) {
            // Telephelyen all -- a Telephely csomopont kozepere rajzoljuk
            return GameLayout.nodePosition("Telephely");
        }
        Ut ut = state.getUt(v.currentUt);
        if (ut == null) return null;
        Point a = GameLayout.nodePosition(ut.nodeA);
        Point b = GameLayout.nodePosition(ut.nodeB);
        if (a == null || b == null) return null;

        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double len = Math.hypot(dx, dy);
        if (len == 0) return new Point(a);
        double nx = -dy / len;
        double ny = dx / len;
        double offset = (v.savIndex - (ut.savSzam() - 1) / 2.0)
                * (GameLayout.LANE_WIDTH + GameLayout.LANE_GAP);

        int x = (int) ((a.x + b.x) / 2.0 + nx * offset);
        int y = (int) ((a.y + b.y) / 2.0 + ny * offset);
        return new Point(x, y);
    }

    private void drawVehicle(Graphics2D g2, Jarmu v, Point pos, boolean selected) {
        int size = 12;
        if (v instanceof Hokotro) {
            // KEK HAROMSZOG
            int[] xs = { pos.x, pos.x - size, pos.x + size };
            int[] ys = { pos.y - size, pos.y + size, pos.y + size };
            g2.setColor(new Color(50, 95, 220));
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(xs, ys, 3);
        } else if (v instanceof Busz) {
            // PIROS TEGLALAP
            g2.setColor(new Color(220, 60, 60));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        } else if (v instanceof Auto) {
            // SARGA TEGLALAP
            g2.setColor(new Color(245, 220, 70));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        }

        if (selected) {
            g2.setColor(new Color(255, 110, 0));
            g2.setStroke(new BasicStroke(3));
            int pad = 5;
            g2.drawOval(pos.x - size - pad, pos.y - size - pad,
                    (size + pad) * 2, (size + pad) * 2);
        }

        // Felirat a jarmu mellett
        g2.setColor(new Color(20, 20, 30));
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.drawString(v.name, pos.x + size + 3, pos.y + 4);
    }

    private boolean isSelected(Jarmu v) {
        NamedEntity sel = state.selected();
        return sel != null && sel.name().equalsIgnoreCase(v.name);
    }

    // ----- Hit testing a controllernek -----

    /**
     * Megkeresi, hogy a megadott pixel-koordinatan van-e jarmu, es ha igen,
     * visszaadja. A talalati zona kicsit nagyobb, mint a rajzolt forma,
     * hogy konnyebb legyen rakattintani.
     */
    public Jarmu vehicleAt(int x, int y) {
        // Forditott rendben iteralunk, hogy ha tobb egymason, a felso nyerjen
        Jarmu hit = null;
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            Point pos = computeVehiclePosition(v);
            if (pos == null) continue;
            int hitRadius = 16;
            if (Math.abs(x - pos.x) <= hitRadius && Math.abs(y - pos.y) <= hitRadius) {
                hit = v; // tovabbiteralunk -- a kesobbi (felulvont) nyer
            }
        }
        return hit;
    }
}
