package gui;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A jatekter statikus elrendezeseert felelos segedosztaly.
 *
 * Tartalmazza a csomopontok kepi koordinatait es kijelzo-feliratait, valamint
 * a rajzolas konstansait (csomopont-sugar, savszelesseg).
 *
 * A pozic iotabla dinamikusan tölthetö (clear + register): a GameInitializer
 * a nehezsegtol fuggoen tolt be kulonbozo terkep-variansokat.
 */
public final class GameLayout {
    /** Csomopont kor sugar pixelben. */
    public static final int NODE_RADIUS = 22;
    /** Egy sav szelessege pixelben (rajzolasi szelesseg). */
    public static final int LANE_WIDTH = 6;
    /** Sav-koz pixelben (vizuális elvalasztashoz). */
    public static final int LANE_GAP = 2;
    /** A terkep panel ajanlott szelessege. Konnyu szinten — nagyobb terkepeknel a panel novekszik. */
    public static final int MAP_WIDTH_DEFAULT = 820;
    /** A terkep panel ajanlott magassaga (default). */
    public static final int MAP_HEIGHT_DEFAULT = 580;

    /** Belso azonosito -> kepi pozicio (insertion order szamit a paint sorrendnel). */
    private static final Map<String, Point> POSITIONS = new LinkedHashMap<>();
    /** Belso azonosito -> kijelzo-felirat (ekezetes szöveggel). */
    private static final Map<String, String> LABELS = new LinkedHashMap<>();

    /** Aktualis ajanlott terkep-szelesseg (a betoltott layout szerint). */
    private static int mapWidth = MAP_WIDTH_DEFAULT;
    /** Aktualis ajanlott terkep-magassag. */
    private static int mapHeight = MAP_HEIGHT_DEFAULT;

    private GameLayout() {
    }

    /** Letorli az osszes regisztralt csomopontot. Uj jatek inditasakor hivni. */
    public static void clear() {
        POSITIONS.clear();
        LABELS.clear();
        mapWidth = MAP_WIDTH_DEFAULT;
        mapHeight = MAP_HEIGHT_DEFAULT;
    }

    /** Beregisztral egy csomopontot a megadott koordinatara, opcionalis ekezetes felirattal. */
    public static void register(String id, String label, int x, int y) {
        POSITIONS.put(id, new Point(x, y));
        LABELS.put(id, label == null ? id : label);
    }

    /** Beallitja az ajanlott terkep-meretet (a MapPanel preferredSize-jahez). */
    public static void setMapSize(int width, int height) {
        mapWidth = width;
        mapHeight = height;
    }

    public static int mapWidth() {
        return mapWidth;
    }

    public static int mapHeight() {
        return mapHeight;
    }

    /** Visszaadja a megadott csomopont kepi poziciojat (masolatot ad), vagy null-t. */
    public static Point nodePosition(String node) {
        if (node == null) return null;
        Point p = POSITIONS.get(node);
        if (p != null) return new Point(p);
        // case-insensitive fallback
        for (Map.Entry<String, Point> entry : POSITIONS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(node)) {
                return new Point(entry.getValue());
            }
        }
        return null;
    }

    /** Visszaadja a megadott csomopont megjelenitendo ekezetes feliratat. */
    public static String displayLabel(String node) {
        if (node == null) return "";
        String label = LABELS.get(node);
        if (label != null) return label;
        for (Map.Entry<String, String> entry : LABELS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(node)) {
                return entry.getValue();
            }
        }
        return node;
    }

    /** Visszaadja az osszes csomopontot belso-azonosito -> pozicio formaban (csak olvasashoz). */
    public static Map<String, Point> allNodes() {
        return Collections.unmodifiableMap(POSITIONS);
    }
}
