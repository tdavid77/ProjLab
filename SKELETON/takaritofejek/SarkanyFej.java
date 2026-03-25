package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Prémium takarítóeszköz, amely gázturbinájával azonnal elolvasztja a havat és a jeget.
 */
public class SarkanyFej extends Fej {
    private int biokerozinKeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SarkanyFej.takaritHatas() meghivva.");
    }

    @Override
    public int kerozinToltes(int mennyiseg) {
        System.out.println("SarkanyFej.kerozinToltes() meghivva.");
        return 0; //TODO
    }

    @Override
    public void setFejAllapota(boolean fejAllapota) {
        this.fejAllapota = fejAllapota;

        if (fejAllapota = false) {
            System.out.println("Sarkanyfej kikapcsolva.");
        } else {
            System.out.println("Sarkanyfej bekapcsolva.");
        }
    }
}
