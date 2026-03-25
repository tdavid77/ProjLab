import java.util.ArrayList;
import java.util.List;

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
        return true;
    }

    public boolean fejVasarlas(Fej k) {
        System.out.println("TakaritoManager.fejVasarlas() meghivva.");
        return true;
    }

    public boolean fejLevetel(Hokotro h) {
        System.out.println("TakaritoManager.fejLevetel() meghivva.");
        return true;
    }

    public boolean fejFelteves(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.fejFelteves() meghivva.");
        return true;
    }

    public int soToltes(Hokotro h) {
        System.out.println("TakaritoManager.soToltes() meghivva.");
        return 100;
    }

    public int kerozinToltes(Hokotro h) {
        System.out.println("TakaritoManager.kerozinToltes() meghivva.");
        return 100;
    }
}
