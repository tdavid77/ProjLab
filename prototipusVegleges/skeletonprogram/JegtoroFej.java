package skeletonprogram;

/**
 * Jegtoro hokotrofej: a savon levo jeget feltort jeg-jelleggel alakitja at,
 * majd azt honak szamitja el, ami ezutan seprhetove, kiszorhatova valik.
 * Nem hasznal keszletet. Hasznalata: jeges utakon a HanyoFej vagy SoproFej
 * elott alkalmazando, mert azok a feltort jeget is el tudjak tüntetni.
 */
public final class JegtoroFej extends Fej {
    public JegtoroFej() {
        super(FejTipus.JEGTOROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        sav.feltortJeg += sav.ice;
        sav.ice = 0;
        sav.ho += sav.feltortJeg;
        sav.feltortJeg = 0;
        state.enqueueEvent("Jegtorofej feltorte a jeget, ho jellegu retegre valtott.");
    }
}
