package terkep;
import java.util.Scanner;
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

    public char getKezdopont() {
        System.out.println("Sav.getKezdopont() meghivva.");
        return 'a'; //TODO
    }

    public char getVegpont() {
        System.out.println("Sav.getVegpont() meghivva.");
        return 'a'; //TODO
    }

    public boolean balesetKalkulacio(int nehezseg) {
        System.out.println("Sav.balesetKalkulacio() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Tortent baleset? (I/N)");
        String valasz = s.nextLine().trim();
        return valasz.equalsIgnoreCase("I");
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
