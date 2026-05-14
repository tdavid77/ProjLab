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
    /** Tobb jarmu egy csomoponton vízszintes sávolasa (pixelben) — kicsit nagyobb mint az ikon-meret. */
    private static final int VEHICLE_SPACING = 40;

    private final GameState state;

    public MapPanel(GameState state) {
        this.state = state;
        // Sotetebb kek hatter — a feher feliratok jol latszanak rajta
        setBackground(new Color(55, 85, 125));
        setPreferredSize(new Dimension(GameLayout.mapWidth(), GameLayout.mapHeight()));
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

        // Eloszor a normal utak (savokkal), aztan a feluljarok (HID) hogy a kereszteszeknel
        // vizualisan felulre kerüljenek, vegul csomopontok, vegul jarmuvek.
        for (Ut ut : state.getAllUtak()) {
            if (ut.type != terkep.UtTipus.HID) {
                drawRoad(g2, ut);
            }
        }
        for (Ut ut : state.getAllUtak()) {
            if (ut.type == terkep.UtTipus.HID) {
                drawRoad(g2, ut);
            }
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
        boolean isOverpass = (ut.type == terkep.UtTipus.HID);
        Stroke originalStroke = g2.getStroke();

        // Feluljaro: az osszes sav alá rajzolunk egy sotet keretet, igy
        // egyertelmuen "emelt szerkezet" hatas keletkezik
        if (isOverpass) {
            int totalWidth = laneCount * GameLayout.LANE_WIDTH + (laneCount - 1) * GameLayout.LANE_GAP;
            g2.setStroke(new BasicStroke(totalWidth + 8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(45, 45, 60));
            g2.drawLine(a.x, a.y, b.x, b.y);
            // Sotetszurke keret a savok ala
            g2.setStroke(new BasicStroke(totalWidth + 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(105, 105, 120));
            g2.drawLine(a.x, a.y, b.x, b.y);
        }

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

        // Feluljaro: kicsi sotet "hidlab" jelek mindket vegen, hogy lassuk hol kezdodik az emelet
        if (isOverpass) {
            int rampLen = 14;
            int rampThick = laneCount * (GameLayout.LANE_WIDTH + GameLayout.LANE_GAP) + 6;
            g2.setColor(new Color(50, 50, 60));
            g2.setStroke(new BasicStroke(2));
            // A pont vegen ket rovid feher vonal a savok keresztiranyaban
            drawRampMark(g2, a, nx, ny, rampThick / 2);
            drawRampMark(g2, b, nx, ny, rampThick / 2);
        }

        g2.setStroke(originalStroke);

        // Felirat - feher szinnel a sotet hatteren, perpendicular eltolassal + pilula-hatterrel
        // hogy a feliratok ne fedjek egymast es mindenkeppen olvashatoak legyenek
        int mx = (a.x + b.x) / 2;
        int my = (a.y + b.y) / 2;
        // perpendicular eltolas: a (nx, ny) iranyaba toljuk a feliratot, hogy a utvonalon kivulre kerüljon
        int perpOffset = 14;
        int labelX = (int) (mx + nx * perpOffset);
        int labelY = (int) (my + ny * perpOffset);

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        String label = isOverpass ? ut.name() + " (felülj.)" : ut.name();
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(label);
        int textH = fm.getAscent();

        // Sotet, felig atlatszó pilula-hatter a szoveg moge
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(labelX - 4, labelY - textH + 1, textW + 8, textH + 3, 8, 8);

        g2.setColor(isOverpass ? new Color(255, 200, 130) : Color.WHITE);
        g2.drawString(label, labelX, labelY);
    }

    /** Rovid keresztiranyu fehér vonal a sav-szélességnek megfelelően a feluljaro veginez. */
    private void drawRampMark(Graphics2D g2, Point p, double nx, double ny, int halfThick) {
        int x1 = (int) (p.x + nx * halfThick);
        int y1 = (int) (p.y + ny * halfThick);
        int x2 = (int) (p.x - nx * halfThick);
        int y2 = (int) (p.y - ny * halfThick);
        g2.drawLine(x1, y1, x2, y2);
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
        g2.setColor(new Color(20, 20, 35));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(pos.x - r, pos.y - r, r * 2, r * 2);

        // Csomopont-felirat — feherrel, vastagon, sotet pilula-hatterrel hogy biztosan olvashato legyen
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        String label = GameLayout.displayLabel(name);
        int textW = fm.stringWidth(label);
        int textH = fm.getAscent();
        int tx = pos.x - textW / 2;
        int ty = pos.y - r - 6;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(tx - 5, ty - textH + 1, textW + 10, textH + 4, 8, 8);

        g2.setColor(Color.WHITE);
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
        // Ikon-meret: a csomopontra ferjen (NODE_RADIUS=22, atmero=44) -> 32px ikon ferjen
        int iconSize = 32;
        int half = iconSize / 2;

        java.awt.image.BufferedImage img = null;
        if (v instanceof Hokotro) img = VehicleIcons.getHokotro();
        else if (v instanceof Busz) img = VehicleIcons.getBusz();
        else if (v instanceof Auto) img = VehicleIcons.getAuto();

        if (img != null) {
            // Ikon kirajzolasa skálazva
            g2.drawImage(img, pos.x - half, pos.y - half, iconSize, iconSize, null);
        } else {
            // Fallback: a regi geometrikus alakzatok, ha az ikon-fajl hianyzik
            drawFallbackShape(g2, v, pos);
        }

        // Balesetes jelzo (X)
        if (v.disabledTime > 0) {
            g2.setColor(new Color(255, 50, 50));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(pos.x - half + 4, pos.y - half + 4, pos.x + half - 4, pos.y + half - 4);
            g2.drawLine(pos.x + half - 4, pos.y - half + 4, pos.x - half + 4, pos.y + half - 4);
        }

        if (selected) {
            g2.setColor(new Color(255, 110, 0));
            g2.setStroke(new BasicStroke(3));
            int pad = 3;
            g2.drawOval(pos.x - half - pad, pos.y - half - pad,
                    iconSize + 2 * pad, iconSize + 2 * pad);
        }

        // Felirat a jarmu nev mellett — feher, jol latszik a sotet hatteren
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString(v.name, pos.x + half + 3, pos.y + 4);
    }

    /**
     * Fallback rajzolas: ha nincs ikon-fajl, a regi geometrikus alakzatokat hasznaljuk.
     */
    private void drawFallbackShape(Graphics2D g2, Jarmu v, Point pos) {
        int size = 12;
        if (v instanceof Hokotro) {
            int[] xs = { pos.x, pos.x - size, pos.x + size };
            int[] ys = { pos.y - size, pos.y + size, pos.y + size };
            g2.setColor(new Color(245, 130, 30)); // narancs (mint a feltoltott ikon)
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(xs, ys, 3);
        } else if (v instanceof Busz) {
            g2.setColor(new Color(250, 200, 60));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        } else if (v instanceof Auto) {
            g2.setColor(new Color(220, 60, 60));
            g2.fillRect(pos.x - size, pos.y - size / 2, size * 2, size);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pos.x - size, pos.y - size / 2, size * 2, size);
        }
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
                if (Math.abs(x - vx) <= 18 && Math.abs(y - vy) <= 18) {
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
            if (Math.abs(x - pos.x) <= 18 && Math.abs(y - pos.y) <= 18) {
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
        int iconBox = 16;

        // Hokotro
        java.awt.image.BufferedImage hokotroImg = VehicleIcons.getHokotro();
        if (hokotroImg != null) {
            g2.drawImage(hokotroImg, x + 10, rowY - 10, iconBox, iconBox, null);
        } else {
            int[] hxs = { x + 18, x + 12, x + 24 };
            int[] hys = { rowY - 6, rowY + 4, rowY + 4 };
            g2.setColor(new Color(245, 130, 30));
            g2.fillPolygon(hxs, hys, 3);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(hxs, hys, 3);
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Hókotró", x + 32, rowY + 2);

        rowY += 18;
        java.awt.image.BufferedImage buszImg = VehicleIcons.getBusz();
        if (buszImg != null) {
            g2.drawImage(buszImg, x + 10, rowY - 10, iconBox, iconBox, null);
        } else {
            g2.setColor(new Color(250, 200, 60));
            g2.fillRect(x + 12, rowY - 4, 14, 8);
            g2.setColor(Color.BLACK);
            g2.drawRect(x + 12, rowY - 4, 14, 8);
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Busz", x + 32, rowY + 2);

        rowY += 18;
        java.awt.image.BufferedImage autoImg = VehicleIcons.getAuto();
        if (autoImg != null) {
            g2.drawImage(autoImg, x + 10, rowY - 10, iconBox, iconBox, null);
        } else {
            g2.setColor(new Color(220, 60, 60));
            g2.fillRect(x + 12, rowY - 4, 14, 8);
            g2.setColor(Color.BLACK);
            g2.drawRect(x + 12, rowY - 4, 14, 8);
        }
        g2.setColor(Color.BLACK);
        g2.drawString("Autó (NPC)", x + 32, rowY + 2);

        rowY += 16;
        g2.setColor(new Color(255, 110, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + 11, rowY - 7, 16, 14);
        g2.setColor(Color.BLACK);
        g2.drawString("Kijelölt", x + 32, rowY + 2);
    }
}
