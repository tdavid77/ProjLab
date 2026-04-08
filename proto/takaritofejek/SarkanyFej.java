package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;
import skeletonprogram.SzkeletonProgram;

/**
 * Prémium takarítóeszköz, amely gázturbinájával azonnal elolvasztja a havat és a jeget.
 */
public class SarkanyFej extends Fej {
    private int biokerozinKeszlet;

    /**
     * Végrehajtja az adott fej takarítási hatását a megadott sávon és hókotrón.
     * @param s a sav, amelyen a takarítás végrehajtásra kerül
     * @param h a hókotró, amely a takarítást végrehajtja
     */
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        SzkeletonProgram.logCall("sf", "SarkanyFej", "takaritHatas", "s, h");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Tölt kerozint a fejbe.
     * @param mennyiseg a töltendő kerozin mennyisége
     * @return a feltöltött kerozin mennyisége, ha sikeres volt, 0 egyébként
     */
    @Override
    public int kerozinToltes(int mennyiseg) {
        SzkeletonProgram.logCall("sf", "SarkanyFej", "kerozinToltes", "mennyiseg");
        if (mennyiseg <= 0) {
            System.out.println("Nem lehet nulla vagy negativ kerozin mennyiseget tolteni.");
            SzkeletonProgram.logReturn("0");
            return 0;
        }
        biokerozinKeszlet = Math.min(100, mennyiseg);
        System.out.println("A sárkány fej feltöltve: " + biokerozinKeszlet + " egység kerozinnal.");
        SzkeletonProgram.logReturn(String.valueOf(biokerozinKeszlet));
        return biokerozinKeszlet;
    }

    /**
     * Be- vagy kikapcsolja a fejet, ha a fej használható állapotban van.
     * @param fejAllapota a kívánt fej állapot (true: bekapcsolva, false: kikapcsolva)
     */
    @Override
    public void fejKiBeKapcsolasa(boolean fejAllapota) {
        SzkeletonProgram.logCall("sf", "SarkanyFej", "fejKiBeKapcsolasa", "fejAllapota");
        this.fejAllapota = fejAllapota;

        if (fejAllapota == false) {
            System.out.println("Sarkanyfej kikapcsolva.");
        } else {
            System.out.println("Sarkanyfej bekapcsolva.");
        }
        SzkeletonProgram.logReturn("void");
    }
}
