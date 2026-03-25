package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Prémium takarítóeszköz, amely gázturbinájával azonnal elolvasztja a havat és a jeget.
 */
public class SarkanyFej extends Fej {
    private boolean fejAllapota;
    private int biokerozinKeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SarkanyFej.takaritHatas() meghivva.");
        if (biokerozinKeszlet > 0) {
            s.hoEsJegAzonnaliOlvasztas();
            biokerozinKeszlet--;
        }
    }

    @Override
    public int kerozinToltes(int mennyiseg) {
        System.out.println("SarkanyFej.kerozinToltes() meghivva.");
        biokerozinKeszlet += mennyiseg;
        return biokerozinKeszlet;
    }
}
