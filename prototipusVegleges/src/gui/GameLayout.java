package gui;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A jatekter statikus elrendezeseert felelos segedosztaly.
 *
 * Tartalmazza a csomopontok kepi koordinatait (a "Projlab model leiras" PDF-ben
 * latott elrendezes szerint), a csomopontok kijelzo-feliratait (ekezetes magyar
 * szöveggel) es a rajzolas konstansait (csomopont-sugar, savszelesseg).
 *
 * Egyelore a terkep hardcoded; a kesobbi etapokban fajlbol toltjuk be.
 */
public final class GameLayout {
    /** Csomopont kor sugar pixelben. */
    public static final int NODE_RADIUS = 22;
    /** Egy sav szelessege pixelben (rajzolasi szelesseg). */
    public static final int LANE_WIDTH = 6;
    /** Sav-koz pixelben (vizuális elvalasztashoz). */
    public static final int LANE_GAP = 2;
    /** A terkep panel ajanlott szelessege. */
    public static final int MAP_WIDTH = 820;
    /** A terkep panel ajanlott magassaga. */
    public static final int MAP_HEIGHT = 580;

    /** Belso azonosito -> kepi pozicio (insertion order szamit a paint sorrendnel). */
    private static final Map<String, Point> POSITIONS = new LinkedHashMap<>();
    /** Belso azonosito -> kijelzo-felirat (ekezetes szöveggel). */
    private static final Map<String, String> LABELS = new LinkedHashMap<>();

    static {
        register("Telephely",        "Telephely",          150, 110);
        register("Foter",            "Főtér",              410, 110);
        register("Vegallomas_Eszak", "Végállomás_Észak",   680, 110);
        register("Gyar",             "Gyár",               150, 290);
        register("Vasutallomas",     "Vasútállomás",       410, 290);
        register("Vegallomas_Del",   "Végállomás_Dél",     680, 290);
        register("Kertvaros",        "Kertváros",          410, 480);
    }

    private GameLayout() {
    }

    private static void register(String id, String label, int x, int y) {
        POSITIONS.put(id, new Point(x, y));
        LABELS.put(id, label);
    }

    /** Visszaadja a megadott csomopont kepi poziciojat (masolatot ad), vagy null-t. */
    public static Point nodePosition(String node) {
        Point p = POSITIONS.get(node);
        return p == null ? null : new Point(p);
    }

    /** Visszaadja a megadott csomopont megjelenitendo ekezetes feliratat. */
    public static String displayLabel(String node) {
        String label = LABELS.get(node);
        return label == null ? node : label;
    }

    /** Visszaadja az osszes csomopontot belso-azonosito -> pozicio formaban (csak olvasashoz). */
    public static Map<String, Point> allNodes() {
        return Collections.unmodifiableMap(POSITIONS);
    }
}
