package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        Scanner s = new Scanner(System.in);
        System.out.println("A cel csomopont vegallomas? (I/N)");
        String valasz = s.nextLine().trim();

        if (valasz.equalsIgnoreCase("I")) {
            j.korRegisztralasa();
            System.out.println("BuszsoforManager.vagyon novelve.");
        } else {
            System.out.println("Nem vegallomas, kor nem lett regisztralva, fizetes nem jart.");
        }
    }
}
