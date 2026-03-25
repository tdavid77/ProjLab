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
        return false;
    }

    public void autoAthalad() {
        System.out.println("Sav.autoAthalad() meghivva.");
    }

    public boolean jarhatoEAutoknak() {
        System.out.println("Sav.jarhatoEAutoknak() meghivva.");
        return jarhatoE;
    }
}
