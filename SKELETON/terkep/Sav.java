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

    public void hoEsJegAzonnaliOlvasztas() {
        System.out.println("Sav.hoEsJegAzonnaliOlvasztas() meghivva.");
        hovastagsag = 0;
        jegvastagsag = 0;
    }

    public void havatUtSzelereSzor() {
        System.out.println("Sav.havatUtSzelereSzor() meghivva.");
        hovastagsag = 0;
    }

    public void jegFeltorese() {
        System.out.println("Sav.jegFeltorese() meghivva.");
        hovastagsag += jegvastagsag;
        jegvastagsag = 0;
    }

    public void hoAtsepreseJobbra() {
        System.out.println("Sav.hoAtsepreseJobbra() meghivva.");
        hovastagsag = 0;
    }

    public void soKihelyezese(int korokSzama) {
        System.out.println("Sav.soKihelyezese() meghivva.");
        legutoljaraLeszortSoKore = korokSzama;
    }

    public void autoAthalad() {
        System.out.println("Sav.autoAthalad() meghivva.");
        athaladtAutokSzama++;
        if (athaladtAutokSzama % 5 == 0) {
            hovastagsag = 0;
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
