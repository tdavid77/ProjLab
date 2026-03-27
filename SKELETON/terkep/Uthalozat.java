package terkep;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jarmuvek.Auto;
import jarmuvek.Jarmu;

/**
 * A város teljes térképét és működését átfogó rendszer motorja.
 * Felelőssége a játékmenet léptetése, az időjárás szimulálása és az autók navigálása.
 */
public class Uthalozat {
    private int jatekNehezsege;
    private char telephely;
    private List<Ut> utak = new ArrayList<>();
    private List<Auto> autok = new ArrayList<>();

    /**
     * Elindítja a játékot a megadott csomópontokkal, létrehozva a térképet és elhelyezve az autókat.
     * @param csomopontok
     */
    public void jatekInditasa(List<Character> csomopontok) {
        System.out.println("Uthalozat.jatekInditasa() meghivva.");
    }

    /**
     * Visszaadja a legrövidebb útvonalat két csomópont között, figyelembe véve a jelenlegi térképet és a járművek helyzetét.
     * @param otthon 
     * @param munkahely
     * @return
     */
    public List<Character> legrovidebbUt(char otthon, char munkahely) {
        System.out.println("Uthalozat.legrovidebbUt() meghivva.");
        return new ArrayList<>();
    }

    /**
     * Elindítja a játék körét, amely során minden autó megpróbál lépni a következő csomópontjára, és szimulálja a havazást, amely befolyásolja a sávok állapotát és a járművek mozgását.
     */
    public void korInditasa() {
        System.out.println("Uthalozat.korInditasa() meghivva.");
    }

    /**
     * Szimulálja a havazást, amely befolyásolja a sávok állapotát és a járművek mozgását.
     * @param mennyiseg
     */
    public void havazas(int mennyiseg) {
        System.out.println("Uthalozat.havazas() meghivva.");
    }

    /**
     * Ellenőrzi, hogy a játék véget ért-e, amely akkor történik, ha a buszok mozgásképtelenek vagy ha a balesetezett autók száma eléri a kritikus határt.
     * @return true, ha a játék véget ért, false egyébként
     */
    public boolean jatekVegeCheck() {
        System.out.println("Uthalozat.jatekVegeCheck() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Mozgaskepesek a buszok? (I/N)");
        String buszokMozgaskepesek = s.nextLine().trim();

        System.out.println("A balesetezett autok maximalis szama elerte a kritikus hatart? (I/N)");
        String elerteKritikusHatar = s.nextLine().trim();

        boolean tomegkozlekedesElakadt = buszokMozgaskepesek.equalsIgnoreCase("N");
        boolean kijarasiTilalom = elerteKritikusHatar.equalsIgnoreCase("I");

        return tomegkozlekedesElakadt || kijarasiTilalom;
    }
}
