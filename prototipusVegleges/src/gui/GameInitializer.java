package gui;

import jarmuvek.Auto;
import jarmuvek.Busz;
import jarmuvek.Hokotro;
import jatekosok.BuszosJatekos;
import jatekosok.TakaritoJatekos;
import motor.Difficulty;
import motor.GameState;
import takaritofejek.FejTipus;
import terkep.Ut;
import terkep.UtTipus;
import gui.MapLoader.MapConfig;
import gui.MapLoader.NodeInfo;
import gui.MapLoader.RoadInfo;
import gui.MapLoader.PlayerInfo;
import gui.MapLoader.NpcInfo;

/**
 * Egy uj jatek kezdeti allapotanak felepiteseert felelos segedosztaly.
 *
 * Harom kulonbozo terkep-varianst tamogat a Difficulty alapjan:
 *   - EASY:   7 csomopont, 7 ut (alap Projlab terkep)
 *   - MEDIUM: 14 csomopont, ~16 ut (~2x)
 *   - HARD:   18 csomopont, ~24 ut (~3x)
 *
 * Mindharom terkepen 2 vegallomas talalhato (Eszak, Del), egymastol minel
 * tavolabb helyezve, hogy a busznak ertelmes utvonalat kelljen megtennie.
 *
 * Mindegyik valtozat a GameLayout-ot tolti fel a kepi koordinatakkal es az
 * ekezetes feliratokkal, igy a MapPanel egyseges modon tudja rajzolni a terkepet.
 */
public final class GameInitializer {

    private static final int DEFAULT_SAV_SZAM = 2;
    private static final int DEFAULT_UT_HOSSZ = 4;

    private GameInitializer() {
    }

    public static void initialize(GameState state, Difficulty difficulty, FejTipus startingHead) {
        state.difficulty = difficulty;
        GameLayout.clear();

        // Map fájl betöltése a difficulty alapján
        MapConfig mapConfig;
        switch (difficulty) {
            case MEDIUM:
                mapConfig = MapLoader.loadMap("medium");
                break;
            case HARD:
                mapConfig = MapLoader.loadMap("hard");
                break;
            case EASY:
            default:
                mapConfig = MapLoader.loadMap("easy");
                break;
        }

        // Map felépítése a fileból
        buildMapFromConfig(state, mapConfig);
        buildPlayersFromConfig(state, mapConfig, startingHead);
        buildNpcCarsFromConfig(state, mapConfig);
    }

    private static Ut road(String name, String nodeA, String nodeB) {
        return new Ut(name, nodeA, nodeB, DEFAULT_UT_HOSSZ, UtTipus.NORMAL, DEFAULT_SAV_SZAM);
    }

    /** Tetszoleges savszamu normal ut. */
    private static Ut road(String name, String nodeA, String nodeB, int savSzam) {
        return new Ut(name, nodeA, nodeB, DEFAULT_UT_HOSSZ, UtTipus.NORMAL, savSzam);
    }

    /**
     * Feluljaro (HID tipusu ut): a meglevo modellben mar tamogatott — a MapPanel
     * vizualisan kulonbozteti meg (vastag soter outline-ral es a normal utak fole rajzolva).
     * Az alagutaktol elteroen a feluljaron is hull a ho a "szelek miatt" (lasd 2.2.2).
     */
    private static Ut overpass(String name, String nodeA, String nodeB, int savSzam) {
        return new Ut(name, nodeA, nodeB, DEFAULT_UT_HOSSZ, UtTipus.HID, savSzam);
    }

    // ============================================================
    // Map felépítés fileból
    // ============================================================

    /**
     * Felépíti a map-et a betöltött konfigurációból.
     */
    private static void buildMapFromConfig(GameState state, MapConfig config) {
        // Map méretének beállítása
        GameLayout.setMapSize(config.mapWidth, config.mapHeight);

        // Csomópontok regisztrálása
        for (NodeInfo node : config.nodes.values()) {
            GameLayout.register(node.id, node.displayName, node.x, node.y);
        }

        // Utak létrehozása
        for (RoadInfo road : config.roads.values()) {
            UtTipus roadType = "HID".equals(road.type) ? UtTipus.HID : UtTipus.NORMAL;
            Ut ut = new Ut(road.name, road.nodeA, road.nodeB, road.length, roadType, road.lanes);
            state.putUt(ut);
        }
    }

    /**
     * Felépíti a játékosokat a betöltött konfigurációból.
     */
    private static void buildPlayersFromConfig(GameState state, MapConfig config, FejTipus startingHead) {
        TakaritoJatekos takarito = null;
        BuszosJatekos buszos = null;

        // Játékosok létrehozása
        for (PlayerInfo player : config.players.values()) {
            if ("TAKARITO".equals(player.type)) {
                takarito = new TakaritoJatekos(player.name);
                state.putEntity(takarito);
            } else if ("BUSZOS".equals(player.type)) {
                buszos = new BuszosJatekos(player.name);
                state.putEntity(buszos);
            }
        }

        // Jármű szöveg alapú konfigurációjára vagyunk szükség (vehicle type, name, startNode)
        // mivel a fileban nem írjuk ki az autó típusát. Ezért manuálisan beálltjuk az alábbi alapértelmezett
        // konfiguráció alapján, amit a játékosok tárolnak.

        if (takarito != null) {
            // Hokotro meghatározása
            Hokotro hokotro = new Hokotro("Hokotro1");
            hokotro.setAktivFej(startingHead);
            hokotro.owner = takarito.name();
            
            // StartNode a config-ból való keresés: az első TAKARITO játékos startNode-ja
            for (PlayerInfo player : config.players.values()) {
                if ("TAKARITO".equals(player.type)) {
                    hokotro.currentNode = player.startNode;
                    break;
                }
            }
            
            takarito.addVehicle(hokotro.name);
            state.putEntity(hokotro);
        }

        if (buszos != null) {
            // Busz meghatározása
            Busz busz = new Busz("Busz1");
            busz.owner = buszos.name();
            
            // StartNode a config-ból való keresés: az első BUSZOS játékos startNode-ja
            for (PlayerInfo player : config.players.values()) {
                if ("BUSZOS".equals(player.type)) {
                    busz.currentNode = player.startNode;
                    break;
                }
            }
            
            buszos.addVehicle(busz.name);
            state.putEntity(busz);
        }
    }

    /**
     * Felépíti az NPC-ket (autókat) a betöltött konfigurációból.
     */
    private static void buildNpcCarsFromConfig(GameState state, MapConfig config) {
        for (NpcInfo npc : config.npcs.values()) {
            Auto auto = new Auto(npc.name);
            auto.setupRoute(npc.otthon, npc.munkahely, npc.startNode);
            state.putEntity(auto);
        }
    }

    // ============================================================
    // Egykori hardkódolt metódusok (már nem használt, de hagyunk itt egy megjegyzést)
}
