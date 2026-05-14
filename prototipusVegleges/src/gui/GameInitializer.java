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

        switch (difficulty) {
            case MEDIUM:
                buildMediumMap(state);
                buildPlayersMedium(state, startingHead);
                buildNpcCarsMedium(state);
                break;
            case HARD:
                buildHardMap(state);
                buildPlayersHard(state, startingHead);
                buildNpcCarsHard(state);
                break;
            case EASY:
            default:
                buildEasyMap(state);
                buildPlayersEasy(state, startingHead);
                buildNpcCarsEasy(state);
                break;
        }
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
    // EASY (7 csomopont, 7 ut) - eredeti Projlab terkep
    // ============================================================
    private static void buildEasyMap(GameState state) {
        GameLayout.setMapSize(820, 580);

        GameLayout.register("Telephely",        "Telephely",          150, 110);
        GameLayout.register("Foter",            "Főtér",              410, 110);
        GameLayout.register("Vegallomas_Eszak", "Végállomás_Észak",   680, 110);
        GameLayout.register("Gyar",             "Gyár",               150, 290);
        GameLayout.register("Vasutallomas",     "Vasútállomás",       410, 290);
        GameLayout.register("Vegallomas_Del",   "Végállomás_Dél",     680, 290);
        GameLayout.register("Kertvaros",        "Kertváros",          410, 480);

        state.putUt(road("Fout",          "Telephely",     "Foter",            2));
        state.putUt(road("FoutKelet",     "Foter",         "Vegallomas_Eszak", 3));  // foeut: 3 sav
        state.putUt(road("KozpontUt",     "Foter",         "Vasutallomas",     2));
        state.putUt(road("GyariUt",       "Telephely",     "Gyar",             1));  // kis sikator
        state.putUt(road("GyarVasut",     "Gyar",          "Vasutallomas",     2));
        state.putUt(road("LazarJanosUt",  "Vasutallomas",  "Vegallomas_Del",   2));
        state.putUt(road("KertUt",        "Vasutallomas",  "Kertvaros",        1));  // kis sikator

        // Feluljaro: Gyar -> Vegallomas_Eszak diagonalisan, atvezeti KozpontUt folott
        state.putUt(overpass("GyarEszak",  "Gyar", "Vegallomas_Eszak", 1));
    }

    private static void buildPlayersEasy(GameState state, FejTipus startingHead) {
        TakaritoJatekos takarito = new TakaritoJatekos("Takarito1");
        BuszosJatekos buszos = new BuszosJatekos("Buszos1");
        state.putEntity(takarito);
        state.putEntity(buszos);

        Hokotro hokotro = new Hokotro("Hokotro1");
        hokotro.setAktivFej(startingHead);
        hokotro.owner = takarito.name();
        hokotro.currentNode = "Telephely";
        takarito.addVehicle(hokotro.name);
        state.putEntity(hokotro);

        Busz busz = new Busz("Busz1");
        busz.owner = buszos.name();
        busz.currentNode = "Vegallomas_Eszak";
        buszos.addVehicle(busz.name);
        state.putEntity(busz);
    }

    private static void buildNpcCarsEasy(GameState state) {
        Auto auto1 = new Auto("Auto1");
        auto1.setupRoute("Kertvaros", "Vegallomas_Eszak", "Kertvaros");
        state.putEntity(auto1);

        Auto auto2 = new Auto("Auto2");
        auto2.setupRoute("Vegallomas_Del", "Telephely", "Vegallomas_Del");
        state.putEntity(auto2);

        Auto auto3 = new Auto("Auto3");
        auto3.setupRoute("Gyar", "Foter", "Gyar");
        state.putEntity(auto3);
    }

    // ============================================================
    // MEDIUM (14 csomopont, 16 ut) - ~2x eredeti
    // ============================================================
    private static void buildMediumMap(GameState state) {
        GameLayout.setMapSize(900, 670);

        GameLayout.register("Telephely",        "Telephely",          150, 90);
        GameLayout.register("Foter",            "Főtér",              330, 90);
        GameLayout.register("Park",             "Park",               520, 90);
        GameLayout.register("Vegallomas_Eszak", "Végállomás_Észak",   720, 90);

        GameLayout.register("Gyar",             "Gyár",               150, 240);
        GameLayout.register("Vasutallomas",     "Vasútállomás",       330, 240);
        GameLayout.register("Piac",             "Piac",               520, 240);
        GameLayout.register("Templom",          "Templom",            720, 240);

        GameLayout.register("Iskola",           "Iskola",             150, 400);
        GameLayout.register("Kertvaros",        "Kertváros",          330, 400);
        GameLayout.register("Strand",           "Strand",             520, 400);
        GameLayout.register("Korhaz",           "Kórház",             720, 400);

        GameLayout.register("Sportter",         "Sporttér",           520, 560);
        GameLayout.register("Vegallomas_Del",   "Végállomás_Dél",     720, 560);

        state.putUt(road("Fout",          "Telephely",     "Foter",            2));
        state.putUt(road("ParkUt",        "Foter",         "Park",             2));
        state.putUt(road("EszakiUt",      "Park",          "Vegallomas_Eszak", 3));  // foeut
        state.putUt(road("TemplomUt",     "Vegallomas_Eszak", "Templom",       2));

        state.putUt(road("GyariUt",       "Telephely",     "Gyar",             1));
        state.putUt(road("KozpontUt",     "Foter",         "Vasutallomas",     2));
        state.putUt(road("PiacUt",        "Park",          "Piac",             1));
        state.putUt(road("PiacTemplom",   "Piac",          "Templom",          1));

        state.putUt(road("GyarVasut",     "Gyar",          "Vasutallomas",     2));
        state.putUt(road("VasutPiac",     "Vasutallomas",  "Piac",             2));
        state.putUt(road("IskolaUt",      "Gyar",          "Iskola",           1));
        state.putUt(road("IskolaKert",    "Iskola",        "Kertvaros",        2));

        state.putUt(road("VasutKert",     "Vasutallomas",  "Kertvaros",        2));
        state.putUt(road("StrandUt",      "Kertvaros",     "Strand",           2));
        state.putUt(road("StrandKorhaz",  "Strand",        "Korhaz",           1));

        state.putUt(road("SportUt",       "Strand",        "Sportter",         1));
        state.putUt(road("KorhazDel",     "Korhaz",        "Vegallomas_Del",   3));  // foeut

        // Feluljaro: Foter -> Korhaz hosszu atloban, atvezeti ParkUt, PiacUt, TemplomUt folott
        state.putUt(overpass("FoterKorhaz", "Foter", "Korhaz", 2));
    }

    private static void buildPlayersMedium(GameState state, FejTipus startingHead) {
        TakaritoJatekos takarito = new TakaritoJatekos("Takarito1");
        BuszosJatekos buszos = new BuszosJatekos("Buszos1");
        state.putEntity(takarito);
        state.putEntity(buszos);

        Hokotro hokotro = new Hokotro("Hokotro1");
        hokotro.setAktivFej(startingHead);
        hokotro.owner = takarito.name();
        hokotro.currentNode = "Telephely";
        takarito.addVehicle(hokotro.name);
        state.putEntity(hokotro);

        Busz busz = new Busz("Busz1");
        busz.owner = buszos.name();
        busz.currentNode = "Vegallomas_Eszak";
        buszos.addVehicle(busz.name);
        state.putEntity(busz);
    }

    private static void buildNpcCarsMedium(GameState state) {
        Auto a1 = new Auto("Auto1");
        a1.setupRoute("Kertvaros", "Vegallomas_Eszak", "Kertvaros");
        state.putEntity(a1);

        Auto a2 = new Auto("Auto2");
        a2.setupRoute("Vegallomas_Del", "Foter", "Vegallomas_Del");
        state.putEntity(a2);

        Auto a3 = new Auto("Auto3");
        a3.setupRoute("Gyar", "Piac", "Gyar");
        state.putEntity(a3);

        Auto a4 = new Auto("Auto4");
        a4.setupRoute("Iskola", "Templom", "Iskola");
        state.putEntity(a4);

        Auto a5 = new Auto("Auto5");
        a5.setupRoute("Sportter", "Foter", "Sportter");
        state.putEntity(a5);
    }

    // ============================================================
    // HARD (18 csomopont, 24 ut) - ~3x eredeti
    // ============================================================
    private static void buildHardMap(GameState state) {
        GameLayout.setMapSize(960, 640);

        // 0. sor
        GameLayout.register("Posta",            "Posta",              110, 70);
        GameLayout.register("Telephely",        "Telephely",          265, 70);
        GameLayout.register("Foter",            "Főtér",              420, 70);
        GameLayout.register("Park",             "Park",               580, 70);
        GameLayout.register("Vegallomas_Eszak", "Végállomás_Észak",   750, 70);

        // 1. sor
        GameLayout.register("Polgarmesteri",    "Polgármesteri",      110, 215);
        GameLayout.register("Gyar",             "Gyár",               265, 215);
        GameLayout.register("Vasutallomas",     "Vasútállomás",       420, 215);
        GameLayout.register("Piac",             "Piac",               580, 215);
        GameLayout.register("Templom",          "Templom",            750, 215);

        // 2. sor
        GameLayout.register("Konyvtar",         "Könyvtár",           110, 360);
        GameLayout.register("Iskola",           "Iskola",             265, 360);
        GameLayout.register("Kertvaros",        "Kertváros",          420, 360);
        GameLayout.register("Strand",           "Strand",             580, 360);
        GameLayout.register("Korhaz",           "Kórház",             750, 360);

        // 3. sor
        GameLayout.register("Mozi",             "Mozi",               265, 510);
        GameLayout.register("Sportter",         "Sporttér",           420, 510);
        GameLayout.register("Vegallomas_Del",   "Végállomás_Dél",     750, 510);

        // Felso sor utak
        state.putUt(road("PostaUt",       "Posta",         "Telephely",        1));
        state.putUt(road("Fout",          "Telephely",     "Foter",            2));
        state.putUt(road("ParkUt",        "Foter",         "Park",             2));
        state.putUt(road("EszakiUt",      "Park",          "Vegallomas_Eszak", 3));  // foeut

        // Kozepso sor utak
        state.putUt(road("PolgarUt",      "Polgarmesteri", "Gyar",             1));
        state.putUt(road("GyarVasut",     "Gyar",          "Vasutallomas",     2));
        state.putUt(road("VasutPiac",     "Vasutallomas",  "Piac",             2));
        state.putUt(road("PiacTemplom",   "Piac",          "Templom",          1));

        // 2. sor utak
        state.putUt(road("KonyvtarUt",    "Konyvtar",      "Iskola",           1));
        state.putUt(road("IskolaKert",    "Iskola",        "Kertvaros",        2));
        state.putUt(road("KertStrand",    "Kertvaros",     "Strand",           2));
        state.putUt(road("StrandKorhaz",  "Strand",        "Korhaz",           1));

        // Vertikalis utak
        state.putUt(road("PostaPolgar",   "Posta",         "Polgarmesteri",    1));
        state.putUt(road("TelGyarUt",     "Telephely",     "Gyar",             1));
        state.putUt(road("KozpontUt",     "Foter",         "Vasutallomas",     2));
        state.putUt(road("ParkPiac",      "Park",          "Piac",             2));
        state.putUt(road("EszakTemplom",  "Vegallomas_Eszak", "Templom",       2));
        state.putUt(road("PolgarKonyvtar","Polgarmesteri", "Konyvtar",         1));
        state.putUt(road("GyarIskola",    "Gyar",          "Iskola",           1));
        state.putUt(road("PiacStrand",    "Piac",          "Strand",           2));

        // 3. sor utak
        state.putUt(road("IskolaMozi",    "Iskola",        "Mozi",             1));
        state.putUt(road("MoziSport",     "Mozi",          "Sportter",         1));
        state.putUt(road("SportDel",      "Sportter",      "Vegallomas_Del",   2));
        state.putUt(road("KorhazDel",     "Korhaz",        "Vegallomas_Del",   3));  // foeut

        // Feluljarok (2 darab a Hard terkepen)
        state.putUt(overpass("TelKert",   "Telephely", "Kertvaros", 2));
        state.putUt(overpass("ParkKorhaz", "Park", "Korhaz", 1));
    }

    private static void buildPlayersHard(GameState state, FejTipus startingHead) {
        TakaritoJatekos takarito = new TakaritoJatekos("Takarito1");
        BuszosJatekos buszos = new BuszosJatekos("Buszos1");
        state.putEntity(takarito);
        state.putEntity(buszos);

        Hokotro hokotro = new Hokotro("Hokotro1");
        hokotro.setAktivFej(startingHead);
        hokotro.owner = takarito.name();
        hokotro.currentNode = "Telephely";
        takarito.addVehicle(hokotro.name);
        state.putEntity(hokotro);

        Busz busz = new Busz("Busz1");
        busz.owner = buszos.name();
        busz.currentNode = "Vegallomas_Eszak";
        buszos.addVehicle(busz.name);
        state.putEntity(busz);
    }

    private static void buildNpcCarsHard(GameState state) {
        Auto a1 = new Auto("Auto1");
        a1.setupRoute("Posta", "Vegallomas_Del", "Posta");
        state.putEntity(a1);

        Auto a2 = new Auto("Auto2");
        a2.setupRoute("Kertvaros", "Templom", "Kertvaros");
        state.putEntity(a2);

        Auto a3 = new Auto("Auto3");
        a3.setupRoute("Gyar", "Strand", "Gyar");
        state.putEntity(a3);

        Auto a4 = new Auto("Auto4");
        a4.setupRoute("Konyvtar", "Park", "Konyvtar");
        state.putEntity(a4);

        Auto a5 = new Auto("Auto5");
        a5.setupRoute("Mozi", "Vegallomas_Eszak", "Mozi");
        state.putEntity(a5);

        Auto a6 = new Auto("Auto6");
        a6.setupRoute("Sportter", "Foter", "Sportter");
        state.putEntity(a6);
    }
}
