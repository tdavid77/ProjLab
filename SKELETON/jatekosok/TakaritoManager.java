package jatekosok;
import java.util.ArrayList;
import java.util.List;

import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import takaritofejek.Fej;

/**
 * A hókotró flottát üzemeltető menedzser.
 * Felelőssége a gépek karbantartása, vásárlása és mozgatása.
 */
public class TakaritoManager extends JatekosManager {
    private List<Hokotro> hokotrok = new ArrayList<>();
    private List<Fej> fejek = new ArrayList<>();

    @Override
    public void mozgatJarmuvet(char cel, Jarmu j) {
        System.out.println("TakaritoManager.mozgatJarmuvet() meghivva.");
    }

    public boolean hokotroVasarlas(Hokotro h) {
        System.out.println("TakaritoManager.hokotroVasarlas() meghivva.");
        hokotrok.add(h);
        raktarbaHelyezHokotrot(h);
        return true;
    }

    public boolean fejVasarlas(Fej k) {
        System.out.println("TakaritoManager.fejVasarlas() meghivva.");
        fejek.add(k);
        return true;
    }

    public boolean fejLevetel(Hokotro h) {
        System.out.println("TakaritoManager.fejLevetel() meghivva.");
        h.setAktualisFej(null);
        return true;
    }

    public boolean fejFelteves(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.fejFelteves() meghivva.");
        h.setAktualisFej(f);
        return true;
    }

    public boolean fejCsere(Hokotro h, Fej ujFej) {
        System.out.println("TakaritoManager.fejCsere() meghivva.");
        boolean sikeresLevetel = fejLevetel(h);
        if (!sikeresLevetel) {
            return false;
        }
        return fejFelteves(h, ujFej);
    }

    public int soToltes(Hokotro h) {
        System.out.println("TakaritoManager.soToltes() meghivva.");
        return h.soToltesAktualisFejbe();
    }

    public int soToltes(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.soToltes(Hokotro, Fej) meghivva.");
        fejFelteves(h, f);
        return soToltes(h);
    }

    public int kerozinToltes(Hokotro h) {
        System.out.println("TakaritoManager.kerozinToltes() meghivva.");
        return h.kerozinToltesAktualisFejbe();
    }

    public int kerozinToltes(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.kerozinToltes(Hokotro, Fej) meghivva.");
        fejFelteves(h, f);
        return kerozinToltes(h);
    }

}
