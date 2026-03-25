package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
        h.getAktualisHelyzet().getKezdopont();
        System.out.println("A hókotró telephelyen van? (I/N)");
        Scanner s = new Scanner(System.in);
        String valasz = s.nextLine();
        boolean sikeresLevetel = fejLevetel(h);
        if (valasz.equals("I")) {
            if (!sikeresLevetel) {
            return false;
            }
            fejek.add(h.getAktualisFej());
            fejek.remove(ujFej);
            return fejFelteves(h, ujFej);
        }
        return false;
    }

    public int soToltes(Hokotro h) {
        System.out.println("TakaritoManager.soToltes() meghivva.");
        return h.soToltesAktualisFejbe();
    }

    public int kerozinToltes(Hokotro h) {
        System.out.println("TakaritoManager.kerozinToltes() meghivva.");
        return h.kerozinToltesAktualisFejbe();
    }
}
