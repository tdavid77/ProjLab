package terkep;
import jarmuvek.Jarmu;

/**
 * Az utak belső, egy járműnyi szélességű felosztása.
 * Tárolja az időjárási elemek állapotát és a forgalmat.
 */
public class Sav {
    private char kezdopont;
    private char vegpont;
    private int hossz;
    private int hovastagsag;
    private int jegvastagsag;
    private int legutoljaraLeszortSoKore;
    private int athaladtAutokSzama;
    private boolean jarhatoE;

    public boolean balesetKalkulacio(int nehezseg) {
        System.out.println("Sav.balesetKalkulacio() meghivva.");
        return nehezseg >= 5;
    }

    public void autoAthalad() {
        System.out.println("Sav.autoAthalad() meghivva.");
        athaladtAutokSzama++;
        if (athaladtAutokSzama % 5 == 0) {
            jegvastagsag++;
        }
    }

    public boolean jarhatoEAutoknak() {
        System.out.println("Sav.jarhatoEAutoknak() meghivva.");
        return jarhatoE;
    }

    public void jarmuAtlepesKockazatVizsgalattal(Jarmu j, int nehezseg) {
        System.out.println("Sav.jarmuAtlepesKockazatVizsgalattal() meghivva.");
        if (balesetKalkulacio(nehezseg)) {
            j.balesetezik();
        }
    }

    public void forgalomEsBalesetSzimulacio(int autokSzama, int nehezseg) {
        System.out.println("Sav.forgalomEsBalesetSzimulacio() meghivva.");
        for (int i = 0; i < autokSzama; i++) {
            autoAthalad();
        }
        balesetKalkulacio(nehezseg);
    }
}
