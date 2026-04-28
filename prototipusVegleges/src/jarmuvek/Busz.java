package jarmuvek;

import jatekosok.Jatekos;
import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 *  Megadott állomások között ingázó, a buszsofőr menedzser által irányított jármű.
 * Feladata a pénzteremtés a megtett körök alapján. Az autókhoz hasonlóan súlyával tömöríti a havat. 
 * Baleset esetén mozgásképtelenné válik és eltorlaszolja az utat. * Az onRegistered() hookkal bejelentkezik a GameState buses listajaba,
 * igy a jatekvegezo logika instanceof nelkul tudja szamlalni a buszokat. */
public final class Busz extends Jarmu {
    private int completedTrips;

    public Busz(String name) {
        super(name);
        this.completedTrips = 0;
    }

    /** Regisztralas soran bejelentkezik a GameState busz-listajaba a jatekvege-logikához. */
    @Override
    public void onRegistered(GameState state) {
        state.registerBus(this);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Busz";
    }

    /**
     * Vegallomas elerese eseten noveli a megtett korok szamat es jovaira a jatekos penzenek.
     * A vegallomas csomopont neve "Vegallomas", "Vegallomas_1" vagy "Vegallomas_2" lehet.
     */
    @Override
    protected void onCelUtElerve(Ut target, GameState state) {
        if (target.hasNode("Vegallomas") || target.hasNode("Vegallomas_1") || target.hasNode("Vegallomas_2")) {
            completedTrips += 1;
            Jatekos ownerEntity = state.getJatekos(owner);
            if (ownerEntity != null) {
                ownerEntity.money += 40;
                state.enqueueEvent("Busz kor teljesitve: " + name + ", jovairas +40.");
            }
        }

        // ÚJ LOGIKA: Hó tömörítése az áthaladáskor (ua. mint az autóknál)
        Sav sav = target.sav(this.savIndex);
        if (sav.ho > 0) {
            sav.trafficCount++;
            if (sav.trafficCount >= 5) {
                sav.ice += sav.ho;
                sav.ho = 0;
                state.enqueueEvent(target.name() + " " + this.savIndex + ". savjan a ho jegpancella tomorodott a forgalom miatt.");
                sav.trafficCount = 0;
            }
        }
    }
}
