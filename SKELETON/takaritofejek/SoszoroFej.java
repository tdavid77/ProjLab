package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Só szórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
 */
public class SoszoroFej extends Fej {
    private int sokeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SoszoroFej.takaritHatas() meghivva.");
    }

    @Override
    public int soToltes(int mennyiseg) {
        System.out.println("SoszoroFej.soToltes() meghivva.");
        return 0;
    }

    @Override
    public void setFejAllapota(boolean fejAllapota) {
        this.fejAllapota = fejAllapota;

        if (fejAllapota = false) {
            System.out.println("Sószórófej kikapcsolva.");
        } else {
            System.out.println("Sószórófej bekapcsolva.");
        }
    }
}
