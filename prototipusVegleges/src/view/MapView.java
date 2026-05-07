package view;

import jarmuvek.Auto;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import motor.GameEngine;
import motor.NamedEntity;
import terkep.Sav;
import terkep.Ut;

public class MapView extends JPanel {
    private GameEngine engine;
    private GamePanel parent;
    private Map<String, Point> nodePositions = new HashMap<>();

    public MapView(GameEngine engine, GamePanel parent) {
        this.engine = engine;
        this.parent = parent;
        setBackground(new Color(230, 240, 250));

        assignCityCoordinates(); // Város felépítése

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (NamedEntity entity : engine.getState().entities.values()) {
                    if (entity instanceof Jarmu) {
                        Jarmu j = (Jarmu) entity;
                        Point p = getJarmuPoint(j);
                        if (p != null && p.distance(e.getPoint()) < 20) {
                            parent.setSelectedJarmu(j);
                            return; 
                        }
                    }
                }
            }
        });
    }

    private void assignCityCoordinates() {
        // Fix, logikus pozíciók egy kisvároshoz
        nodePositions.put("Telephely", new Point(100, 100));
        nodePositions.put("Főtér", new Point(350, 100));
        nodePositions.put("Végállomás_Észak", new Point(600, 100));
        nodePositions.put("Gyár", new Point(100, 300));
        nodePositions.put("Vasútállomás", new Point(350, 300));
        nodePositions.put("Végállomás_Dél", new Point(600, 300));
        nodePositions.put("Kertváros", new Point(350, 500));

        // Ha egy csomópont nincs a fenti listában, betesszük a kép aljára
        int extraX = 100, extraY = 600;
        for (Ut ut : engine.getState().utak.values()) {
            if (!nodePositions.containsKey(ut.nodeA)) {
                nodePositions.put(ut.nodeA, new Point(extraX, extraY));
                extraX += 100;
            }
            if (!nodePositions.containsKey(ut.nodeB)) {
                nodePositions.put(ut.nodeB, new Point(extraX, extraY));
                extraX += 100;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nodePositions.isEmpty()) assignCityCoordinates();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. TÖBBSÁVOS UTAK RAJZOLÁSA
        g2d.setStroke(new BasicStroke(6)); // Sávok vastagsága
        
        for (Ut ut : engine.getState().utak.values()) {
            Point pA = nodePositions.get(ut.nodeA);
            Point pB = nodePositions.get(ut.nodeB);
            if (pA == null || pB == null) continue;

            // Matek a párhuzamos vonalak eltolásához (merőleges vektor)
            int dx = pB.x - pA.x;
            int dy = pB.y - pA.y;
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len == 0) continue;
            
            double nx = -dy / len; 
            double ny = dx / len;  

            int savSzam = ut.savSzam();
            double laneWidth = 10.0; // 10 pixel távolság a sávok között

            for (int i = 0; i < savSzam; i++) {
                Sav sav = ut.sav(i);
                
                // Kiszámoljuk az eltolást az i-edik sávhoz
                double offset = (i - (savSzam - 1) / 2.0) * laneWidth;
                int startX = (int) (pA.x + nx * offset);
                int startY = (int) (pA.y + ny * offset);
                int endX = (int) (pB.x + nx * offset);
                int endY = (int) (pB.y + ny * offset);

                // Sáv színe hó/jég alapján
                if (sav.ice > 0) g2d.setColor(new Color(150, 200, 255));
                else if (sav.ho > 0) g2d.setColor(Color.WHITE);
                else g2d.setColor(Color.DARK_GRAY);

                g2d.drawLine(startX, startY, endX, endY);

                // --- NYÍL RAJZOLÁSA AZ IRÁNYHOZ ---
                // Logika: páros sáv A->B, páratlan sáv B->A megy (mint a valóságban a jobb/balkéz szabály)
                boolean reverseDirection = (savSzam > 1 && i % 2 != 0); 
                drawArrow(g2d, startX, startY, endX, endY, reverseDirection, g2d.getColor());
            }
        }

        // 2. Csomópontok
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String nodeName = entry.getKey();
            Point p = entry.getValue();
            
            if (nodeName.toLowerCase().contains("telep")) g2d.setColor(Color.YELLOW);
            else if (nodeName.toLowerCase().contains("végállomás")) g2d.setColor(Color.GREEN);
            else g2d.setColor(Color.LIGHT_GRAY);

            g2d.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - 15, p.y - 15, 30, 30);
            g2d.drawString(nodeName, p.x - 20, p.y - 20);
        }

        // 3. Járművek
        for (NamedEntity entity : engine.getState().entities.values()) {
            if (entity instanceof Jarmu) {
                Jarmu j = (Jarmu) entity;
                Point p = getJarmuPoint(j);
                if (p != null) {
                    if (j instanceof Auto) g2d.setColor(Color.RED);
                    else if (j instanceof Hokotro) g2d.setColor(Color.ORANGE);
                    else g2d.setColor(Color.BLUE); // Busz
                    
                    g2d.fillRect(p.x - 10, p.y - 10, 20, 20);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(j.name().substring(0, 1), p.x - 4, p.y + 5);
                }
            }
        }
    }

    // Segédfüggvény a nyilak rajzolásához az utakra
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, boolean reverse, Color roadColor) {
        // Kontrasztos szín a nyílnak (fehér úton fekete, sötét úton fehér)
        g2d.setColor(roadColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        
        // A nyilat a sáv feléhez tesszük
        int mx = (x1 + x2) / 2;
        int my = (y1 + y2) / 2;
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        if (reverse) angle += Math.PI; // Ha szembe megy, megfordítjuk

        int size = 8;
        int[] px = {
            (int) (mx + size * Math.cos(angle)),
            (int) (mx + size * Math.cos(angle + 0.8 * Math.PI)),
            (int) (mx + size * Math.cos(angle - 0.8 * Math.PI))
        };
        int[] py = {
            (int) (my + size * Math.sin(angle)),
            (int) (my + size * Math.sin(angle + 0.8 * Math.PI)),
            (int) (my + size * Math.sin(angle - 0.8 * Math.PI))
        };
        g2d.fillPolygon(px, py, 3);
    }

    private Point getJarmuPoint(Jarmu j) {
        if (j.currentUt == null) {
            for (String node : nodePositions.keySet()) {
                if (node.toLowerCase().contains("telep")) return nodePositions.get(node);
            }
            return new Point(50, 50); 
        } else {
            Ut ut = engine.getState().getUt(j.currentUt);
            if (ut != null) {
                Point p1 = nodePositions.get(ut.nodeA);
                Point p2 = nodePositions.get(ut.nodeB);
                if (p1 != null && p2 != null) {
                    return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
                }
            }
        }
        return new Point(50, 50);
    }
}