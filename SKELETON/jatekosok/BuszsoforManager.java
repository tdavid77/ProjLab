package jatekosok;
import java.util.ArrayList;
import java.util.List;

import jarmuvek.Busz;
import jarmuvek.Jarmu;

/**
 * A buszokat irányító szerepkör reprezentációja.
 * Célja a vagyon növelése a menetrendek betartásával.
 */
public class BuszsoforManager extends JatekosManager {
    private List<Busz> buszok = new ArrayList<>();

    @Override
    public void mozgatJarmuvet(char cel, Jarmu j) {
        System.out.println("BuszsoforManager.mozgatJarmuvet() meghivva.");
        j.lep(cel);
        j.korRegisztralasa();
    }
}
