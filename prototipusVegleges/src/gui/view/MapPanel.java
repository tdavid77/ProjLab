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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
 * UJ MODELL:
 *   - A jarmuvek alapesetben a currentNode csomoponton allnak.
 *   - Tobb jarmu egy csomoponton: kozepre igazitva, oldalra eltolva (1=kozep,
 *     2=kozep+jobb, 3=bal+kozep+jobb, ...).
 *   - Csak a balesetezett jarmuvek (currentNode==null, currentUt!=null) kerulnek
 *     kirajzolasra a savon.
 *
 * UJ SAVSZINEZES: 5 fokozatu gradiens hora (feher) es jegre (vilagoskek);
 * sima ut fekete, sozott szurke, zuzottko barna.
 */
public final class MapPanel extends JPanel implements GameStateListener {
    /** Tobb jarmu egy csomoponton vízszintes sávolasa (pixelben). */
    private static final int VEHICLE_SPACING = 30;

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

        // Eloszor utak (savokkal), aztan csomopontok, vegul jarmuvek
        for (Ut ut : state.getAllUtak()) {
            drawRoad(g2, ut);
        }
        for (Map.Entry<String, Point> entry : GameLayout.allNodes().entrySet()) {
            drawNode(g2, entry.getKey(), entry.getValue());
        }

        // Jarmuvek kettesszer: csomoponton allok (csoportositva), aztan az uton elakadt balesetezettek
        drawVehiclesAtNodes(g2);
        drawStuckVehiclesOnRoads(g2);

        // Jelmagyarazat
        drawLegend(g2);

        g2.dispose();
    }

    // ----- Sav/csomopont rajzolas -----

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

        // Felirat
        int mx = (a.x + b.x) / 2;
        int my = (a.y + b.y) / 2;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(new Color(60, 60, 80));
        g2.drawString(ut.name(), mx + 6, my - 6);
    }

    /**
     * Sav-szin meghatarozasa 5 fokozatu gradiens szerint:
     *   - Sima ut (semmi nincs rajta): fekete
     *   - Hovas: feher, 5 fokozat (1=halvany ... 5=teljes)
     *   - Jeges: vilagoskek, 5 fokozat
     *   - Sozott: szurke
     *   - Zuzottkos: barna
     */
    private Color laneColor(Sav sav) {
        Color base = new Color(40, 40, 45);
        if (sav.ice > 0) {
            int level = Math.min(5, sav.ice);
            return blendLevel(base, new Color(170, 205, 235), level);
        }
        if (sav.ho > 0) {
            int level = Math.min(5, sav.ho);
            return blendLevel(base, new Color(245, 245, 245), level);
        }
        if (sav.soHatralevoIdeje > 0) return new Color(170, 170, 175);
        if (sav.zuzalekHatralevoIdeje > 0) return new Color(140, 90, 50);
        return base;
    }

    /**
     * 5-fokozatu szin-keverest hajt vegre a base es target szinek kozott a 'level' alapjan.
     * A level=1 ertekhez nem nullarol indulunk: minimum 55% kevereshez ugorhat azonnal,
     * hogy az elso egysegnyi ho/jeg is egyertelmuen megkulonboztetheto legyen a sima uttol.
     * level=5 (es felette) eseten a target szin 100%-ban latszik.
     */
    private Color blendLevel(Color base, Color target, int level) {
        int clamped = Math.max(1, Math.min(5, level));
        double t = 0.55 + (clamped - 1) * (1.0 - 0.55) / 4.0;
        int r = (int) (base.getRed() + t * (target.getRed() - base.getRed()));
        int g = (int) (base.getGreen() + t * (target.getGreen() - base.getGreen()));
        int b = (int) (base.getBlue() + t * (target.getBlue() - base.getBlue()));
        return new Color(r, g, b);
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

    // ----- Jarmu rajzolas (csomoponton + uton elakadt) -----

    /**
     * Csomopont szerint csoportositja a csomoponton allo jarmuveket,
     * majd kirajzolja oket az "1=kozep, 2=kozep+jobb, 3=bal+kozep+jobb, ..." mintaban.
     */
    private void drawVehiclesAtNodes(Graphics2D g2) {
        Map<String, List<Jarmu>> grouped = groupVehiclesByNode();
        for (Map.Entry<String, List<Jarmu>> entry : grouped.entrySet()) {
            Point center = GameLayout.nodePosition(entry.getKey());
            if (center == null) continue;
            List<Jarmu> vehicles = entry.getValue();
            int n = vehicles.size();
            int offsetBase = (int) Math.floor((n - 1) / 2.0);
            for (int i = 0; i < n; i++) {
                int relOffset = i - offsetBase;
                Point pos = new Point(center.x + relOffset * VEHICLE_SPACING, center.y);
                Jarmu v = vehicles.get(i);
                drawVehicle(g2, v, pos, isSelected(v));
            }
        }
    }

    /** Az uton elakadt balesetezett jarmuveket az adott savjuk kozepere rajzolja. */
    private void drawStuckVehiclesOnRoads(Graphics2D g2) {
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            if (v.currentNode != null) continue; // csomoponton, mar rajzoltuk
            if (v.currentUt == null) continue;   // valami inkonzisztens
            Point pos = computeStuckVehiclePosition(v);
            if (pos == null) continue;
            drawVehicle(g2, v, pos, isSelected(v));
        }
    }

    private Map<String, List<Jarmu>> groupVehiclesByNode() {
        Map<String, List<Jarmu>> map = new LinkedHashMap<>();
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            if (v.currentNode == null) continue;
            map.computeIfAbsent(v.currentNode, k -> new ArrayList<>()).add(v);
        }
        return map;
    }

    private Point computeStuckVehiclePosition(Jarmu v) {
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
        double offset = (v.savIndex - (ut.savSzam() - 1) / 2.0) * (GameLayout.LANE_WIDTH + GameLayout.LANE_GAP);
        int x = (int) ((a.x + b.x) / 2.0 + nx * offset);
        int y = (int) ((a.y + b.y) / 2.0 + ny * offset);
        return new Point(x, y);
    }

    private void drawVehicle(Graphics2D g2, Jarmu v, Point pos, boolean selected) {
        int size = 12;
        if (v instanceof Hokotro) {
            int[] xs = { pos.x, pos.x - size, pos.x + size };
            int[] ys = { pos.y - size, pos.y + size, pos.y + size };
            g2.setColor(new Color(50, 95, 220));
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(xs, ys, 3);
        } else if (v instanceof Busz) {
            g2.setColor(new Color(220, 60, 60));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        } else if (v instanceof Auto) {
            g2.setColor(new Color(245, 220, 70));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        }

        // Balesetes jelzo (X)
        if (v.disabledTime > 0) {
            g2.setColor(new Color(255, 50, 50));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(pos.x - size + 2, pos.y - size + 2, pos.x + size - 2, pos.y + size - 2);
            g2.drawLine(pos.x + size - 2, pos.y - size + 2, pos.x - size + 2, pos.y + size - 2);
        }

        if (selected) {
            g2.setColor(new Color(255, 110, 0));
            g2.setStroke(new BasicStroke(3));
            int pad = 5;
            g2.drawOval(pos.x - size - pad, pos.y - size - pad,
                    (size + pad) * 2, (size + pad) * 2);
        }

        // Felirat
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
     * visszaadja. Mind csomoponton allo, mind uton elakadt jarmuveket figyel.
     */
    public Jarmu vehicleAt(int x, int y) {
        Jarmu hit = null;
        // Csomoponton allok
        Map<String, List<Jarmu>> grouped = groupVehiclesByNode();
        for (Map.Entry<String, List<Jarmu>> entry : grouped.entrySet()) {
            Point center = GameLayout.nodePosition(entry.getKey());
            if (center == null) continue;
            List<Jarmu> vehicles = entry.getValue();
            int n = vehicles.size();
            int offsetBase = (int) Math.floor((n - 1) / 2.0);
            for (int i = 0; i < n; i++) {
                int relOffset = i - offsetBase;
                int vx = center.x + relOffset * VEHICLE_SPACING;
                int vy = center.y;
                if (Math.abs(x - vx) <= 16 && Math.abs(y - vy) <= 16) {
                    hit = vehicles.get(i);
                }
            }
        }
        // Uton elakadt balesetezettek
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            if (v.currentNode != null) continue;
            if (v.currentUt == null) continue;
            Point pos = computeStuckVehiclePosition(v);
            if (pos == null) continue;
            if (Math.abs(x - pos.x) <= 16 && Math.abs(y - pos.y) <= 16) {
                hit = v;
            }
        }
        return hit;
    }

    // ----- Jelmagyarazat -----

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

        rowY += 16;
        g2.setColor(new Color(220, 60, 60));
        g2.fillRect(x + 12, rowY - 4, 14, 8);
        g2.setColor(Color.BLACK);
        g2.drawRect(x + 12, rowY - 4, 14, 8);
        g2.drawString("Busz", x + 32, rowY + 2);

        rowY += 16;
        g2.setColor(new Color(245, 220, 70));
        g2.fillRect(x + 12, rowY - 4, 14, 8);
        g2.setColor(Color.BLACK);
        g2.drawRect(x + 12, rowY - 4, 14, 8);
        g2.drawString("Autó (NPC)", x + 32, rowY + 2);

        rowY += 16;
        g2.setColor(new Color(255, 110, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + 11, rowY - 7, 16, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Kijelölt", x + 32, rowY + 2);
    }
}
