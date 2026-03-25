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
        return true; //TODO
    }

    public void hoEltavolit() {
        System.out.println("Sav.hoEltavolit() meghivva.");
        
    }

    public void havatUtSzelereSzor() {
        System.out.println("Sav.havatUtSzelereSzor() meghivva.");
    }

    public void jegFeltorese() {
        System.out.println("Sav.jegFeltorese() meghivva.");
    }

    public void hoAtsepreseJobbra() {
        System.out.println("Sav.hoAtsepreseJobbra() meghivva.");
    }

    public void soKihelyezese(int korokSzama) {
        System.out.println("Sav.soKihelyezese() meghivva.");
    }

    public void autoAthalad() {
        System.out.println("Sav.autoAthalad() meghivva.");
    }

    public boolean jarhatoEAutoknak() {
        System.out.println("Sav.jarhatoEAutoknak() meghivva.");
        return true; //TODO
    }
}
