package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

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
        System.out.println("SarkanyFej.takaritHatas() meghivva.");
    }

    /**
     * Tölt kerozint a fejbe.
     * @param mennyiseg a töltendő kerozin mennyisége
     * @return a feltöltött kerozin mennyisége, ha sikeres volt, 0 egyébként
     */
    @Override
    public int kerozinToltes(int mennyiseg) {
        System.out.println("SarkanyFej.kerozinToltes() meghivva.");
        return 0; //TODO
    }

    /**
     * Be- vagy kikapcsolja a fejet, ha a fej használható állapotban van.
     * @param fejAllapota a kívánt fej állapot (true: bekapcsolva, false: kikapcsolva)
     */
    @Override
    public void fejKiBeKapcsolasa(boolean fejAllapota) {
        this.fejAllapota = fejAllapota;

        if (fejAllapota == false) {
            System.out.println("Sarkanyfej kikapcsolva.");
        } else {
            System.out.println("Sarkanyfej bekapcsolva.");
        }
    }
}
