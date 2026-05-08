package gui.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gui.view.MapPanel;
import jarmuvek.Jarmu;
import motor.GameState;

/**
 * Egerkattintasok kezeloje a terkep panelen.
 *
 * Az MVC kontroller-retegehez tartozik: a felhasznaloi inputot
 * model-akciokra forditja le. Itt a kattintast jarmu-kivalasztassa.
 *
 * Ha egy jarmu kepe alatt kattintunk, kivalasztjuk azt a GameState-ben;
 * ha ures teruleten, leleszunk a kivalasztassal. A model fireStateChanged()
 * hivasa miatt a hallgato view-k automatikusan frissulnek.
 */
public final class MapController extends MouseAdapter {
    private final GameState state;
    private final MapPanel panel;

    public MapController(GameState state, MapPanel panel) {
        this.state = state;
        this.panel = panel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Jarmu hit = panel.vehicleAt(e.getX(), e.getY());
        if (hit != null) {
            state.setSelectedName(hit.name);
        } else {
            state.setSelectedName(null);
        }
    }
}
