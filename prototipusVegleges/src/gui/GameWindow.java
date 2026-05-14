package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import gui.controller.ActionController;
import gui.controller.MapController;
import gui.view.ContextPanel;
import gui.view.EventLogPanel;
import gui.view.HudPanel;
import gui.view.MapPanel;
import motor.Difficulty;
import motor.GameActions;
import motor.GameState;
import takaritofejek.FejTipus;

/**
 * A fo jatekablak, BorderLayout alapjan a Projlab model leiras szerint:
 *   NORTH  -> HudPanel    (jatekosadatok, balesetek, Pass'N'Play gomb)
 *   CENTER -> MapPanel    (terkep, csomopontok, sávok, jarmuvek, jelmagyarazat)
 *   EAST   -> ContextPanel (kivalasztott entitas reszletei + akciogombok + telephelyi akciok)
 *   SOUTH  -> EventLogPanel (legfrissebb esemenyek + szin-magyarazat)
 *
 * A konstruktor megepiti a GameState-et (GameInitializer-rel), letrehozza a
 * GameActions-et (a meglevo modellbeli akciok), az ActionController-t (ami a
 * gombokrol forditja le a hivasokat), majd a view panelokat. Mindenkit
 * feliratkoztatja a state-re push-ertesiteshez, es egy initial fireStateChanged-del
 * kivaltja az elso renderelest.
 */
public final class GameWindow extends JFrame {
    private final GameState state;

    public GameWindow(Difficulty difficulty, FejTipus startingHead) {
        super("Zúzmaraváros - Hóeltakarító Szimulátor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Model — eloszor inicializaljuk, mert a GameLayout meretei alapjan
        // szamoljuk az ablak meretet (a Hard terkep ~960px szeles)
        this.state = new GameState();
        GameInitializer.initialize(state, difficulty, startingHead);

        int windowWidth = Math.max(1200, gui.GameLayout.mapWidth() + 360);  // +300 ContextPanel + sav
        int windowHeight = Math.max(820, gui.GameLayout.mapHeight() + 220); // +HUD +EventLog
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        // Akciok + controller
        GameActions gameActions = new GameActions(state);
        ActionController actionController = new ActionController(state, gameActions);
        actionController.setOwnerFrame(this);

        // View panelok
        HudPanel hud = new HudPanel(state, actionController);
        MapPanel mapPanel = new MapPanel(state);
        ContextPanel contextPanel = new ContextPanel(state, actionController);
        EventLogPanel eventLog = new EventLogPanel(state);

        // Push-feliratkozas
        state.addListener(hud);
        state.addListener(mapPanel);
        state.addListener(contextPanel);
        state.addListener(eventLog);

        // Map kontroller (egerkattintas)
        MapController mapController = new MapController(state, mapPanel);
        mapPanel.addMouseListener(mapController);

        // Layout
        setLayout(new BorderLayout());
        add(hud, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(contextPanel, BorderLayout.EAST);
        add(eventLog, BorderLayout.SOUTH);

        // Inditasi esemeny + render
        state.enqueueEvent("Játék elindítva — nehézség: " + difficulty.name()
                + ", kezdő fej: " + startingHead.name());
        state.fireStateChanged();
    }
}
