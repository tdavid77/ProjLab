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
 * alapjan: csomopontok, utak, savok, jatekosok, hokotro a telephelyen,
 * busz egy vegallomason, kezdo NPC autok az otthon csomopontjukon.
 *
 * UJ MODELL: minden jarmu csomoponton kezd (currentNode beallitva, currentUt null).
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
        state.putUt(road("LazarJanos_ut", "Vasutallomas",  "Vegallomas_Del"));
        state.putUt(road("Kert_ut",     "Vasutallomas",  "Kertvaros"));
    }

    private static Ut road(String name, String nodeA, String nodeB) {
        return new Ut(name, nodeA, nodeB, DEFAULT_UT_HOSSZ, UtTipus.NORMAL, DEFAULT_SAV_SZAM);
    }

    /** Letrehozza a ket jatekost, a hokotrot es a buszt; mindketto csomoponton kezd. */
    private static void buildPlayers(GameState state, FejTipus startingHead) {
        TakaritoJatekos takarito = new TakaritoJatekos("Takarito1");
        BuszosJatekos buszos = new BuszosJatekos("Buszos1");
        state.putEntity(takarito);
        state.putEntity(buszos);

        // Hokotro a Telephely csomoponton
        Hokotro hokotro = new Hokotro("Hokotro1");
        hokotro.setAktivFej(startingHead);
        hokotro.owner = takarito.name();
        hokotro.currentNode = "Telephely";
        takarito.addVehicle(hokotro.name);
        state.putEntity(hokotro);

        // Busz a Vegallomas_Eszak csomoponton (kezdo vegallomas)
        Busz busz = new Busz("Busz1");
        busz.owner = buszos.name();
        busz.currentNode = "Vegallomas_Eszak";
        buszos.addVehicle(busz.name);
        state.putEntity(busz);
    }

    /**
     * Nehany NPC autot helyez el a terkepen otthon es munkahely parokkal.
     * Az autok a setupRoute() segitsegevel inicializalodnak: az 'otthon' csomoponton
     * kezdenek, eloszor a 'munkahely' fele indulnak.
     */
    private static void buildNpcCars(GameState state) {
        // Kertvarosbol -> Vegallomas_Eszak
        Auto auto1 = new Auto("Auto1");
        auto1.setupRoute("Kertvaros", "Vegallomas_Eszak", "Kertvaros");
        state.putEntity(auto1);

        // Vegallomas_Del -> Telephely
        Auto auto2 = new Auto("Auto2");
        auto2.setupRoute("Vegallomas_Del", "Telephely", "Vegallomas_Del");
        state.putEntity(auto2);

        // Gyar -> Foter
        Auto auto3 = new Auto("Auto3");
        auto3.setupRoute("Gyar", "Foter", "Gyar");
        state.putEntity(auto3);
    }
}
