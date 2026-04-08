package terkep;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jarmuvek.Auto;
import jarmuvek.Jarmu;
import skeletonprogram.SzkeletonProgram;

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
        SzkeletonProgram.logCall("uh", "Uthalozat", "jatekInditasa", "csomopontok");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Visszaadja a legrövidebb útvonalat két csomópont között, figyelembe véve a jelenlegi térképet és a járművek helyzetét.
     * @param otthon 
     * @param munkahely
     * @return
     */
    public List<Character> legrovidebbUt(char otthon, char munkahely) {
        SzkeletonProgram.logCall("uh", "Uthalozat", "legrovidebbUt", "otthon, munkahely");
        SzkeletonProgram.logReturn("List<Character>");
        return new ArrayList<>();
    }

    /**
     * Elindítja a játék körét, amely során minden autó megpróbál lépni a következő csomópontjára, és szimulálja a havazást, amely befolyásolja a sávok állapotát és a járművek mozgását.
     */
    public void korInditasa() {
        SzkeletonProgram.logCall("uh", "Uthalozat", "korInditasa", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Szimulálja a havazást, amely befolyásolja a sávok állapotát és a járművek mozgását.
     * @param mennyiseg
     */
    public void havazas(int mennyiseg) {
        SzkeletonProgram.logCall("uh", "Uthalozat", "havazas", "mennyiseg");
        if (mennyiseg <= 0) {
            System.out.println("A havazas mennyisege nem lehet nulla vagy negativ.");
            SzkeletonProgram.logReturn("void");
            return;
        }
        if (utak.isEmpty()) {
            System.out.println("Nincs jelenleg ut a halozatban, uj szakaszokat adunk hozza.");
            utak.add(new Ut());
        }
        for (Ut ut : utak) {
            System.out.println("A halozat utjain havazas tortenik: " + mennyiseg + " egység.");
        }
        System.out.println("A havazás hatasa megjelent a hálózaton.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Ellenőrzi, hogy a játék véget ért-e, amely akkor történik, ha a buszok mozgásképtelenek vagy ha a balesetezett autók száma eléri a kritikus határt.
     * @return true, ha a játék véget ért, false egyébként
     */
    public boolean jatekVegeCheck() {
        SzkeletonProgram.logCall("uh", "Uthalozat", "jatekVegeCheck", "");
        Scanner s = new Scanner(System.in);
        System.out.println("Mozgaskepesek a buszok? (I/N)");
        String buszokMozgaskepesek = s.nextLine().trim();

        System.out.println("A balesetezett autok maximalis szama elerte a kritikus hatart? (I/N)");
        String elerteKritikusHatar = s.nextLine().trim();

        boolean tomegkozlekedesElakadt = buszokMozgaskepesek.equalsIgnoreCase("N");
        boolean kijarasiTilalom = elerteKritikusHatar.equalsIgnoreCase("I");

        SzkeletonProgram.logReturn("boolean");
        return tomegkozlekedesElakadt || kijarasiTilalom;
    }
}
