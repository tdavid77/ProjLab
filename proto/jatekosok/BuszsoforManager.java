package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import skeletonprogram.SzkeletonProgram;

import jarmuvek.Busz;
import jarmuvek.Jarmu;

/**
 * A buszokat irányító szerepkör reprezentációja.
 * Célja a vagyon növelése a menetrendek betartásával.
 */
public class BuszsoforManager extends JatekosManager {
    private List<Busz> buszok = new ArrayList<>();

    /**
     * Mozgatja a megadott járművet a megadott célpontra.
     * @param cel a célpontra
     * @param j a jármű
     */
    @Override
    public void mozgatJarmuvet(char cel, Jarmu j) {
        SzkeletonProgram.logCall("bsm", "BuszsoforManager", "mozgatJarmuvet", "cel, j");
        j.lep(cel);

        Scanner s = new Scanner(System.in);
        System.out.println("A cel csomopont vegallomas? (I/N)");
        String valasz = s.nextLine().trim();

        if (valasz.equalsIgnoreCase("I")) {
            j.korRegisztralasa();
            System.out.println("BuszsoforManager.vagyon novelve.");
        } else {
            System.out.println("Nem vegallomas, kor nem lett regisztralva, fizetes nem jart.");
        }
        SzkeletonProgram.logReturn("void");
    }
}
