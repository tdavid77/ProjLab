package terkep;
import java.util.Scanner;
import jarmuvek.Jarmu;
import skeletonprogram.SzkeletonProgram;

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
        SzkeletonProgram.logCall("sav", "Sav", "getKezdopont", "");
        SzkeletonProgram.logReturn("char");
        return 'a'; //TODO
    }

    /**
     * Visszaadja a sáv végpontját.
     * @return a sáv végpontja
     */
    public char getVegpont() {
        SzkeletonProgram.logCall("sav", "Sav", "getVegpont", "");
        SzkeletonProgram.logReturn("char");
        return 'a'; //TODO
    }

    /**
     * A globális balesetkalkulációt végző függvény, amely a sáv aktuális állapotát és a nehézségi szintet figyelembe véve meghatározza, hogy történt-e baleset.
     * @param nehezseg
     * @return true, ha történt baleset, false egyébként
     */
    public boolean balesetKalkulacio(int nehezseg) {
        SzkeletonProgram.logCall("sav", "Sav", "balesetKalkulacio", "nehezseg");
        Scanner s = new Scanner(System.in);
        System.out.println("Tortent baleset? (I/N)");
        String valasz = s.nextLine().trim();
        boolean result = valasz.equalsIgnoreCase("I");
        SzkeletonProgram.logReturn("boolean");
        return result;
    }

    /**
     * Eltávolítja a hót a sávról.
     */
    public void hoEltavolit() {
        SzkeletonProgram.logCall("sav", "Sav", "hoEltavolit", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Sószórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
     */
    public void havatUtSzelereSzor() {
        SzkeletonProgram.logCall("sav", "Sav", "havatUtSzelereSzor", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Feltöri a jéget a sávon.
     */
    public void jegFeltorese() {
        SzkeletonProgram.logCall("sav", "Sav", "jegFeltorese", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Az adott sáv hava jobbra tolódik.
     */
    public void hoAtsepreseJobbra() {
        SzkeletonProgram.logCall("sav", "Sav", "hoAtsepreseJobbra", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Az adott sávra só helyezése, amely ideiglenesen gátolja, hogy a hó megmaradjon a sáron.
     * @param korokSzama
     */
    public void soKihelyezese(int korokSzama) {
        SzkeletonProgram.logCall("sav", "Sav", "soKihelyezese", "korokSzama");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Az adott sávon áthaladó autók számának növelése, amely befolyásolja a sáv állapotát és a balesetkalkulációt.
     */
    public void autoAthalad() {
        SzkeletonProgram.logCall("sav", "Sav", "autoAthalad", "");
        Scanner s = new Scanner(System.in);
        System.out.println("Kello mennyisegu auto athaladt a savon? (I/N)");
        String valasz = s.nextLine().trim();

        if (valasz.equalsIgnoreCase("I")) {
            System.out.println("A ho jegge tomorult a savon (jeg hozzaadasa megtortent).");
        } else {
            System.out.println("Nincs eleg auto, a ho nem tomorult jegge.");
        }
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Visszaadja, hogy a sáv járható-e autók számára, figyelembe véve a sáv állapotát és a balesetkalkuláció eredményét.
     * @return true, ha járható, false egyébként
     */
    public boolean jarhatoEAutoknak() {
        SzkeletonProgram.logCall("sav", "Sav", "jarhatoEAutoknak", "");
        SzkeletonProgram.logReturn("boolean");
        return true; //TODO
    }
}
