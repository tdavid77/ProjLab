package view;

import jarmuvek.Auto;
import jarmuvek.Busz;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import java.awt.*; // <-- Autó beimportálása
import javax.swing.*;
import motor.GameEngine;
import terkep.Ut;
import terkep.UtTipus; // <-- Fej beimportálása

public class GamePanel extends JPanel {
    private GameEngine engine;
    private MapView mapView;
    private JLabel statsLabel;
    private JPanel rightPanel;
    private Jarmu selectedJarmu;
    private int currentTurn = 1;

    public GamePanel(ZuzmaravarosApp mainApp) {
        this.engine = new GameEngine();
        
        initCityMap(); // <-- Kör helyett igazi város betöltése

        setLayout(new BorderLayout());

        JPanel topHUD = new JPanel(new BorderLayout());
        topHUD.setBackground(Color.DARK_GRAY);
        statsLabel = new JLabel(" Kör: " + currentTurn);
        statsLabel.setForeground(Color.WHITE);
        
        JButton nextTurnBtn = new JButton("Kör vége");
        nextTurnBtn.addActionListener(e -> {
            engine.getState().tickTime(); 
            currentTurn++;
            refreshPanel();
        });

        topHUD.add(statsLabel, BorderLayout.WEST);
        topHUD.add(nextTurnBtn, BorderLayout.EAST);

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Információk"));

        mapView = new MapView(engine, this);

        add(topHUD, BorderLayout.NORTH);
        add(mapView, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        refreshPanel();
    }

    private void initCityMap() {
        // 1. Építsünk egy logikus várost többsávos utakkal
        engine.getState().putUt(new Ut("Foutca", "Telephely", "Főtér", 150, UtTipus.NORMAL, 2)); // 2 sáv
        engine.getState().putUt(new Ut("Eszaki_ut", "Főtér", "Végállomás_Észak", 150, UtTipus.NORMAL, 2));
        engine.getState().putUt(new Ut("Kozpont_ut", "Főtér", "Vasútállomás", 150, UtTipus.NORMAL, 4)); // 4 sáv!
        engine.getState().putUt(new Ut("Ipari_ut", "Gyár", "Vasútállomás", 150, UtTipus.NORMAL, 2));
        engine.getState().putUt(new Ut("Kereszt_ut", "Telephely", "Gyár", 150, UtTipus.NORMAL, 1));
        engine.getState().putUt(new Ut("Deli_ut", "Vasútállomás", "Végállomás_Dél", 150, UtTipus.NORMAL, 2));
        engine.getState().putUt(new Ut("Kert_ut", "Vasútállomás", "Kertváros", 150, UtTipus.NORMAL, 1));

        // Kis havazás a látvány kedvéért
        engine.getState().getUt("Eszaki_ut").sav(0).ho = 20;
        engine.getState().getUt("Eszaki_ut").sav(1).ice = 15;

        // 2. Hókotró létrehozása ALAPÉRTELMEZETT FEJJEL
        Hokotro h1 = new Hokotro("Hokotro1");
        h1.currentUt = "Foutca";
        // --- FIGYELEM: Kérlek írd át a változót arra, ahogy a te modelledben hívják (pl. setFej(new SoproFej()))! ---
        // h1.aktivFej = new SoproFej(); 
        engine.getState().putEntity(h1);

        // 3. Busz létrehozása
        Busz b1 = new Busz("Busz1");
        b1.currentUt = "Kozpont_ut";
        // --- FIGYELEM: Írd át a kezdő és célállomás változóit a sajátjaidra! ---
        // b1.vegallomas1 = "Végállomás_Észak";
        // b1.vegallomas2 = "Végállomás_Dél";
        // b1.megtettKorok = 5; 
        engine.getState().putEntity(b1);
        
        // 4. Autó
        Auto a1 = new Auto("Auto1");
        a1.currentUt = "Kert_ut";
        engine.getState().putEntity(a1);
    }

    public void setSelectedJarmu(Jarmu j) {
        this.selectedJarmu = j;
        refreshPanel();
    }

    public void refreshPanel() {
        if (engine.getState() == null) return;
        
        statsLabel.setText(" Kör: " + currentTurn + " | Balesetek: " + engine.getState().accidents);
        
        rightPanel.removeAll();
        if (selectedJarmu != null) {
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.add(new JLabel("Kijelölve: " + selectedJarmu.name()));
            
            String poz = (selectedJarmu.currentUt == null) ? "Csomópont" : "Út: " + selectedJarmu.currentUt;
            rightPanel.add(new JLabel("Pozíció: " + poz));
            rightPanel.add(new JLabel("Állapot: " + (selectedJarmu.canMove() ? "Aktív" : "Baleset")));
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Üres hely
            
            // --- JÁRMŰTÍPUS SZERINTI KIÍRÁSOK ---
            if (selectedJarmu instanceof Busz) {
                Busz b = (Busz) selectedJarmu;
                rightPanel.add(new JLabel("Típus: Busz"));
                rightPanel.add(new JLabel("Útvonal:\n Végállomás_Észak - Vasútállomás"));
                rightPanel.add(new JLabel("Megtett körök: " + b.completedTrips));

            } else if (selectedJarmu instanceof Auto) {
                rightPanel.add(new JLabel("Típus: Autó"));

            } else if (selectedJarmu instanceof Hokotro) {
                Hokotro h = (Hokotro) selectedJarmu;
                rightPanel.add(new JLabel("Típus: Hókotró"));
                rightPanel.add(new JLabel("Aktív fej: Söprőfej"));
                
                JButton buyBtn = new JButton("Vásárlás (Telephelyen)");
                buyBtn.setEnabled(selectedJarmu.currentUt == null || selectedJarmu.currentUt.contains("Telep")); 
                rightPanel.add(buyBtn);
            }
            
            // --- MOZGÁS GOMB ---
            // Csak akkor rajzoljuk ki, ha NEM autó (azaz Busz vagy Hókotró)
            if (!(selectedJarmu instanceof Auto)) {
                rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                JButton moveBtn = new JButton("Lépés ezen az úton");
                moveBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Kattints egy szomszédos útra!"));
                rightPanel.add(moveBtn);
            }

        } else {
            rightPanel.add(new JLabel("Válassz egy járművet a térképen!"));
        }
        
        rightPanel.revalidate();
        rightPanel.repaint();
        if (mapView != null) mapView.repaint();
    }
}