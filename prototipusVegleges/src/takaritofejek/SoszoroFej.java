package takaritofejek;

import jarmuvek.Hokotro;
import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 * Sot hasznalo hokotrofej: eltavolitja a ho- es jegreget, es 3 kores ideig
 * vedelmet biztosit az uj ho-kepzodes ellen.
 * Egy hasznalat 10 egyseg sot fogyaszt. Ha nincs eleg so (< 10), kivetelt dob.
 * Ideallis hosszabb tavon vedendo, fontos utakra, pl. vegallomas keze lebenek.
 */
public final class SoszoroFej extends Fej {
    public SoszoroFej() {
        super(FejTipus.SOSZOROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        if (h.so < 10) {
            throw new IllegalArgumentException("Nincs eleg so a soszorofejhez.");
        }
        h.so -= 10;
        sav.ho = 0;
        sav.ice = 0;
        sav.soHatralevoIdeje = 3;
        state.enqueueEvent("Soszoro fej aktiv: ho es jeg eltunt, 3 korig vedett a sav.");
    }
}
