package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;
import skeletonprogram.SzkeletonProgram;

/**
 * A hókotrókra szerelhető, cserélhető munkaeszközök absztrakt fogalma.
 * Definiálja az egységes takarítási interfészt.
 */
public abstract class Fej {
    protected String azonosito;
    protected boolean fejAllapota;

    /**
     * Be- vagy kikapcsolja a fejet, ha a fej használható állapotban van.
     * @param fejAllapota a kívánt fej állapot (true: bekapcsolva, false: kikapcsolva)
     */
    public void fejKiBeKapcsolasa(boolean fejAllapota) {
        SzkeletonProgram.logCall("f", "Fej", "fejKiBeKapcsolasa", "fejAllapota");
        System.out.println("Nem tortent semmi, mert nem fogyoeszkozos fejt kapcsoltal.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Abstract függvény, amelyet a konkrét fejek implementálnak a saját takarítási hatásuk végrehajtásához.
     * @param s a sav, amelyen a takarítás végrehajtásra kerül
     * @param h a hókotró, amely a takarítást végrehajtja
     */
    public abstract void takaritHatas(Sav s, Hokotro h);

    /**
     * Tölt sót a fejbe, ha a fej támogatja a só töltést.
     * @param mennyiseg a töltendő só mennyisége
     * @return a feltöltött só mennyisége, ha sikeres volt, 0 egyébként
     */
    public int soToltes(int mennyiseg) {
        SzkeletonProgram.logCall("f", "Fej", "soToltes", "mennyiseg");
        System.out.println("Fej.soToltes() meghivva (ez a fej nem tamogat so toltest).");
        SzkeletonProgram.logReturn("0");
        return 0;
    }

    /**
     * Tölt kerozint a fejbe, ha a fej támogatja a kerozin töltést.
     * @param mennyiseg a töltendő kerozin mennyisége
     * @return a feltöltött kerozin mennyisége, ha sikeres volt, 0 egyébként
     */
    public int kerozinToltes(int mennyiseg) {
        SzkeletonProgram.logCall("f", "Fej", "kerozinToltes", "mennyiseg");
        System.out.println("Fej.kerozinToltes() meghivva (ez a fej nem tamogat kerozin toltest).");
        SzkeletonProgram.logReturn("0");
        return 0;
    }
}
