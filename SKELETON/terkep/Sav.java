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

    /**
     * Visszaadja a sáv kezdőpontját.
     * @return a sáv kezdőpontja
     */
    public char getKezdopont() {
        System.out.println("Sav.getKezdopont() meghivva.");
        return 'a'; //TODO
    }

    /**
     * Visszaadja a sáv végpontját.
     * @return a sáv végpontja
     */
    public char getVegpont() {
        System.out.println("Sav.getVegpont() meghivva.");
        return 'a'; //TODO
    }

    /**
     * A globális balesetkalkulációt végző függvény, amely a sáv aktuális állapotát és a nehézségi szintet figyelembe véve meghatározza, hogy történt-e baleset.
     * @param nehezseg
     * @return true, ha történt baleset, false egyébként
     */
    public boolean balesetKalkulacio(int nehezseg) {
        System.out.println("Sav.balesetKalkulacio() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Tortent baleset? (I/N)");
        String valasz = s.nextLine().trim();
        return valasz.equalsIgnoreCase("I");
    }

    /**
     * Eltávolítja a hót a sávról.
     */
    public void hoEltavolit() {
        System.out.println("Sav.hoEltavolit() meghivva.");
    }

    /**
     * Sószórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
     */
    public void havatUtSzelereSzor() {
        System.out.println("Sav.havatUtSzelereSzor() meghivva.");
    }

    /**
     * Feltöri a jéget a sávon.
     */
    public void jegFeltorese() {
        System.out.println("Sav.jegFeltorese() meghivva.");
    }

    /**
     * Az adott sáv hava jobbra tolódik.
     */
    public void hoAtsepreseJobbra() {
        System.out.println("Sav.hoAtsepreseJobbra() meghivva.");
    }

    /**
     * Az adott sávra só helyezése, amely ideiglenesen gátolja, hogy a hó megmaradjon a sáron.
     * @param korokSzama
     */
    public void soKihelyezese(int korokSzama) {
        System.out.println("Sav.soKihelyezese() meghivva.");
    }

    /**
     * Az adott sávon áthaladó autók számának növelése, amely befolyásolja a sáv állapotát és a balesetkalkulációt.
     */
    public void autoAthalad() {
        System.out.println("Sav.autoAthalad() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Kello mennyisegu auto athaladt a savon? (I/N)");
        String valasz = s.nextLine().trim();

        if (valasz.equalsIgnoreCase("I")) {
            System.out.println("A ho jegge tomorult a savon (jeg hozzaadasa megtortent).");
        } else {
            System.out.println("Nincs eleg auto, a ho nem tomorult jegge.");
        }
    }

    /**
     * Visszaadja, hogy a sáv járható-e autók számára, figyelembe véve a sáv állapotát és a balesetkalkuláció eredményét.
     * @return true, ha járható, false egyébként
     */
    public boolean jarhatoEAutoknak() {
        System.out.println("Sav.jarhatoEAutoknak() meghivva.");
        return true; //TODO
    }
}
