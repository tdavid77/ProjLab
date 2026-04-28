package jarmuvek;

import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 *  Önműködő (NPC) jármű, amely az otthona és a munkahelye között próbál eljutni a legrövidebb úton. 
 *  Lépéseivel, súlyát felhasználva hozzájárul a hó jéggé tömörítéséhez. Kiemelt felelőssége a várakozási logika: 
 *  ha az út elzáródik, nem tervez újra, nem fordul meg, hanem a legutolsó pontnál feltorlódva várakozik, amíg a hókotrók fel nem szabadítják az utat.
 *  Az Auto semmi jatekos-specifikus viselkedest nem tartalmaz; minden logikaja
 *  az alaposztaly Jarmu-ban van definihalva.
 */
public final class Auto extends Jarmu {
    public Auto(String name) {
        super(name);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Auto";
    }

    //ÚJ LOGIKA: Hó tömörítése az áthaladáskor
    @Override
    protected void onCelUtElerve(Ut target, GameState state) {
        super.onCelUtElerve(target, state);
        Sav sav = target.sav(this.savIndex);
        
        //Hó tömörítése az áthaladáskor
        if (sav.ho > 0) {
            sav.trafficCount++;
            if (sav.trafficCount >= 5) {
                sav.ice += sav.ho;
                sav.ho = 0;
                state.enqueueEvent(target.name() + " " + this.savIndex + ". savjan a ho jegpancella tomorodott a forgalom miatt.");
                sav.trafficCount = 0; // Visszaállítjuk a számlálót
            }
        }
    }
}
