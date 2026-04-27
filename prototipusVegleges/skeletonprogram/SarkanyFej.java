package skeletonprogram;

/**
 * Kerozint fogyaszto hokotrofej: azonnali teljes olvasztast hajt vegre a savon.
 * Egy hasznalat 10 egyseg kerozint fogyaszt. Hatasara a sav ho, ice es feltortJeg
 * mezöje nullara all. Leggyorsabb, de keszletigenyes takaritasi mod.
 * Ha nincs eleg kerozin (< 10), kivetelt dob.
 */
public final class SarkanyFej extends Fej {
    public SarkanyFej() {
        super(FejTipus.SARKANYFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        if (h.kerozin < 10) {
            throw new IllegalArgumentException("Nincs eleg kerozin a sarkanyfejhez.");
        }
        h.kerozin -= 10;
        sav.ho = 0;
        sav.ice = 0;
        sav.feltortJeg = 0;
        state.enqueueEvent("Sarkanyfej aktiv: azonnali olvasztas vegrehajtva.");
    }
}
