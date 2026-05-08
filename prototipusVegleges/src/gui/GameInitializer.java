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

/**
 * Egy uj jatek kezdeti allapotanak felepiteseert felelos segedosztaly.
 *
 * Hardcoded varost epit fel a "Projlab model leiras" PDF-ben latott terkep
 * alapjan: csomopontok, utak, sávok, jatekosok, hokotro a telephelyen,
 * busz az utvonalon, kezdo NPC autok.
 *
 * Ez a logika kesobb refaktoralhato lesz egy fajl-betolto rendszerre.
 */
public final class GameInitializer {

    /** Standard sávszam minden uton (egyelore). */
    private static final int DEFAULT_SAV_SZAM = 2;
    /** Standard ut-hossz egyelore. */
    private static final int DEFAULT_UT_HOSSZ = 4;

    private GameInitializer() {
    }

    /**
     * Inicializalja a jatekallapotot a megadott nehezsegi szinttel
     * es a hokotro kezdo fejtipusaval.
     */
    public static void initialize(GameState state, Difficulty difficulty, FejTipus startingHead) {
        state.difficulty = difficulty;

        buildRoadNetwork(state);
        buildPlayers(state, startingHead);
        buildNpcCars(state);
    }

    /** Letrehozza az utakat a Projlab terkep szerint. */
    private static void buildRoadNetwork(GameState state) {
        state.putUt(road("Fout",        "Telephely",     "Foter"));
        state.putUt(road("Fout_kelet",  "Foter",         "Vegallomas_Eszak"));
        state.putUt(road("Kozpont_ut",  "Foter",         "Vasutallomas"));
        state.putUt(road("Gyari_ut",    "Telephely",     "Gyar"));
        state.putUt(road("Gyar_vasut",  "Gyar",          "Vasutallomas"));
        state.putUt(road("Vasut_kelet", "Vasutallomas",  "Vegallomas_Del"));
        state.putUt(road("Kert_ut",     "Vasutallomas",  "Kertvaros"));
    }

    private static Ut road(String name, String nodeA, String nodeB) {
        return new Ut(name, nodeA, nodeB, DEFAULT_UT_HOSSZ, UtTipus.NORMAL, DEFAULT_SAV_SZAM);
    }

    /** Letrehozza a két jatekost, a hokotrot (telephelyen) es a buszt (kezdo poziciot kap). */
    private static void buildPlayers(GameState state, FejTipus startingHead) {
        TakaritoJatekos takarito = new TakaritoJatekos("Takarito1");
        BuszosJatekos buszos = new BuszosJatekos("Buszos1");
        state.putEntity(takarito);
        state.putEntity(buszos);

        // Hokotro a telephelyen (currentUt == null jelenti, hogy telephelyen van)
        Hokotro hokotro = new Hokotro("Hokotro1");
        hokotro.setAktivFej(startingHead);
        hokotro.owner = takarito.name();
        takarito.addVehicle(hokotro.name);
        state.putEntity(hokotro);

        // Busz a Telephely-Foter szakaszon (a model leiras PDF-ben latott elrendezes szerint)
        Busz busz = new Busz("Busz1");
        busz.owner = buszos.name();
        busz.currentUt = "Fout";
        busz.savIndex = 0;
        buszos.addVehicle(busz.name);
        state.putEntity(busz);
    }

    /**
     * Nehany NPC autot helyez el a terkepen otthon es munkahely parokkal.
     * Az 'utolsoCsomopont' annak a vegpontnak a neve, ami fele a auto eppen halad
     * (a szomszedos cel csomopont a kezdesnel).
     */
    private static void buildNpcCars(GameState state) {
        // Kertvarosbol -> Vegallomas_Eszak (Vasutallomas-on at)
        Auto auto1 = new Auto("Auto1");
        auto1.currentUt = "Kert_ut";
        auto1.savIndex = 0;
        auto1.setupRoute("Kertvaros", "Vegallomas_Eszak", "Vasutallomas");
        state.putEntity(auto1);

        // Vegallomas_Del -> Telephely (Vasut, Foter, Telephely)
        Auto auto2 = new Auto("Auto2");
        auto2.currentUt = "Vasut_kelet";
        auto2.savIndex = 1;
        auto2.setupRoute("Vegallomas_Del", "Telephely", "Vasutallomas");
        state.putEntity(auto2);

        // Gyar -> Foter (Gyar -> Vasutallomas -> Foter)
        Auto auto3 = new Auto("Auto3");
        auto3.currentUt = "Gyar_vasut";
        auto3.savIndex = 1;
        auto3.setupRoute("Gyar", "Foter", "Vasutallomas");
        state.putEntity(auto3);
    }
}
