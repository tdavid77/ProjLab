package skeletonprogram;

/**
 * Olyan hokotrofej, amely a havat es a feltort jeget teljesen eltavolitja a savrol.
 * Nem hasznal keszletet. Hatasara a sav ho es feltortJeg mezieje nullara all.
 * Az eltavolitott anyag az ut melle kerul (terleten marad, de nem akadalyozza
 * a forgalmat). Legegyszerübb es legolcsobb takaritasi megoldas.
 */
public final class HanyoFej extends Fej {
    public HanyoFej() {
        super(FejTipus.HANYOFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        sav.ho = 0;
        sav.feltortJeg = 0;
        state.enqueueEvent("Hanyofej az ut melle tavolitotta a csapadekot.");
    }
}
