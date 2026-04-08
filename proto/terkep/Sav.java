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

    public Sav() {
        this('A', 'B', 1, 5, 2);
    }

    public Sav(char kezdopont, char vegpont, int hossz, int hovastagsag, int jegvastagsag) {
        this.kezdopont = kezdopont;
        this.vegpont = vegpont;
        this.hossz = Math.max(1, hossz);
        this.hovastagsag = Math.max(0, hovastagsag);
        this.jegvastagsag = Math.max(0, jegvastagsag);
        this.legutoljaraLeszortSoKore = 0;
        this.athaladtAutokSzama = 0;
        this.jarhatoE = true;
    }

    /**
     * Visszaadja a sáv kezdőpontját.
     * @return a sáv kezdőpontja
     */
    public char getKezdopont() {
        SzkeletonProgram.logCall("sav", "Sav", "getKezdopont", "");
        SzkeletonProgram.logReturn("char");
        return kezdopont;
    }

    /**
     * Visszaadja a sáv végpontját.
     * @return a sáv végpontja
     */
    public char getVegpont() {
        SzkeletonProgram.logCall("sav", "Sav", "getVegpont", "");
        SzkeletonProgram.logReturn("char");
        return vegpont;
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
        if (result) {
            System.out.println("Baleset tortent a savon.");
        } else {
            System.out.println("Nem tortent baleset.");
        }
        SzkeletonProgram.logReturn("boolean");
        return result;
    }

    /**
     * Eltávolítja a hót a sávról.
     */
    public void hoEltavolit() {
        SzkeletonProgram.logCall("sav", "Sav", "hoEltavolit", "");
        hovastagsag = 0;
        System.out.println("A ho eltavolitva a savrol.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Sószórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
     */
    public void havatUtSzelereSzor() {
        SzkeletonProgram.logCall("sav", "Sav", "havatUtSzelereSzor", "");
        hovastagsag = Math.max(0, hovastagsag - 3);
        System.out.println("A havat az ut szelere szoritottuk.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Feltöri a jéget a sávon.
     */
    public void jegFeltorese() {
        SzkeletonProgram.logCall("sav", "Sav", "jegFeltorese", "");
        jegvastagsag = 0;
        System.out.println("A jeg feltorve.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Az adott sáv hava jobbra tolódik.
     */
    public void hoAtsepreseJobbra() {
        SzkeletonProgram.logCall("sav", "Sav", "hoAtsepreseJobbra", "");
        hovastagsag = Math.max(0, hovastagsag - 2);
        System.out.println("A ho atseprese jobbra megtortent.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Az adott sávra só helyezése, amely ideiglenesen gátolja, hogy a hó megmaradjon a sáron.
     * @param korokSzama
     */
    public void soKihelyezese(int korokSzama) {
        SzkeletonProgram.logCall("sav", "Sav", "soKihelyezese", "korokSzama");
        if (korokSzama <= 0) {
            System.out.println("Nem lehet nulla vagy negativ szamu koret megadni.");
            SzkeletonProgram.logReturn("void");
            return;
        }
        legutoljaraLeszortSoKore = korokSzama;
        hovastagsag = Math.max(0, hovastagsag - 4);
        System.out.println("So kihelyezve: a ho intenzitasa csokkent.");
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
            athaladtAutokSzama++;
            jegvastagsag += 2;
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
        boolean result = jarhatoE && hovastagsag < 10 && jegvastagsag < 6;
        System.out.println("A sav jarhato allapota: " + result);
        SzkeletonProgram.logReturn("boolean");
        return result;
    }
}
