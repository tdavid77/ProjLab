package takaritofejek;

import jarmuvek.Hokotro;
import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 * Sopro hokotrofej: a savon levo havat a szomszedos (magasabb indexu) savra tereli.
 * Nem hasznal keszletet. Ha nincs jobbra szomszedos sav, az anyag elveszik.
 * Az alapertelmezett fej: minden uj Hokotro ezzel indul.
 * Hatekony, de a szomszedsavot megterheli; ott szükseges lehet ujabb takaritas.
 */
public final class SoproFej extends Fej {
    public SoproFej() {
        super(FejTipus.SOPROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        int movedHo = sav.ho + sav.feltortJeg;
        sav.ho = 0;
        sav.feltortJeg = 0;
        int rightIndex = savIndex + 1;
        if (movedHo > 0 && rightIndex < ut.savSzam()) {
            Sav rightSav = ut.sav(rightIndex);
            rightSav.ho += movedHo;
        }
        state.enqueueEvent("Soprofej atterelte a csapadekot.");
    }
}
