package takaritofejek;

import jarmuvek.Hokotro;
import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 * Hokotrokra szerelhetö takaritofejek absztrakt alaposztalya.
 * Minden fejhez tartozik egy FejTipus enum-ertek es egy takaritHatas() absztrakt
 * metódus, amely a konkret takaritasi viselkedest valositja meg.
 * A fejek keszleteket hasznalhatnak a Hokotro-bol (so, kerozin, zuzottko),
 * es esemenyeket helyezhetnek az esemenysorba.
 */
public abstract class Fej {
    private final FejTipus tipus;

    protected Fej(FejTipus tipus) {
        this.tipus = tipus;
    }

    /** Visszaadja a fej tipusat (FejTipus enum). */
    public FejTipus tipus() {
        return tipus;
    }

    /**
     * A fej konkret takaritasi hatasat hajtja vegre a megadott savon.
     * A parametereken keresztul hozzaferheto a hokotro keszleteihez es az esemenysorhoz.
     */
    public abstract void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state);
}
