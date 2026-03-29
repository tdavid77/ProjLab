package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;
import skeletonprogram.SzkeletonProgram;

/**
 * Só szórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
 */
public class SoszoroFej extends Fej {
    private int sokeszlet;

    /**
     * Végrehajtja az adott fej takarítási hatását a megadott sávon és hókotrón.
     * @param s a sav, amelyen a takarítás végrehajtásra kerül
     * @param h a hókotró, amely a takarítást végrehajtja
     */
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        SzkeletonProgram.logCall("sf", "SoszoroFej", "takaritHatas", "s, h");
        s.soKihelyezese(3);
        s.hoEltavolit();
        System.out.println("A leszort so ideiglenesen gatolja, hogy a ho megmaradjon a savon.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Tölt sót a fejbe.
     * @param mennyiseg a töltendő só mennyisége
     * @return a feltöltött só mennyisége, ha sikeres volt, 0 egyébként
     */
    @Override
    public int soToltes(int mennyiseg) {
        SzkeletonProgram.logCall("sf", "SoszoroFej", "soToltes", "mennyiseg");
        SzkeletonProgram.logReturn("0");
        return 0;
    }

    /**
     * Be- vagy kikapcsolja a fejet, ha a fej használható állapotban van.
     * @param fejAllapota a kívánt fej állapot (true: bekapcsolva, false: kikapcsolva)
     */
    @Override
    public void fejKiBeKapcsolasa(boolean fejAllapota) {
        SzkeletonProgram.logCall("sf", "SoszoroFej", "fejKiBeKapcsolasa", "fejAllapota");
        this.fejAllapota = fejAllapota;

        if (fejAllapota == false) {
            System.out.println("Sószórófej kikapcsolva.");
        } else {
            System.out.println("Sószórófej bekapcsolva.");
        }
        SzkeletonProgram.logReturn("void");
    }
}
