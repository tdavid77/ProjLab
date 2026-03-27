package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import takaritofejek.Fej;

/**
 * A hókotró flottát üzemeltető menedzser.
 * Felelőssége a gépek karbantartása, vásárlása és mozgatása.
 */
public class TakaritoManager extends JatekosManager {
    private List<Hokotro> hokotrok = new ArrayList<>();
    private List<Fej> fejek = new ArrayList<>();

    /**
     * Mozgatja a megadott járművet a megadott célpontra.
     * @param cel a célpontra
     * @param j a jármű
     */
    @Override
    public void mozgatJarmuvet(char cel, Jarmu j) {
        System.out.println("TakaritoManager.mozgatJarmuvet() meghivva.");
    }

    /**
     * Vásárol egy új hókotró gépet, ha a játékos telephelyen van és elegendő vagyonnal rendelkezik.
     * @param h a megvásárolni kívánt hókotró
     * @return true, ha a vásárlás sikeres volt, false egyébként
     */
    public boolean hokotroVasarlas(Hokotro h) {
        System.out.println("TakaritoManager.hokotroVasarlas() meghivva.");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            return false;
        }

        hokotrok.add(h);
        System.out.println("Uj hokotro hozzaadva a takarito manager hokotro listajahoz.");
        return true;
    }

    /**
     * Vásárol egy új fejet a hókotró gépekhez, ha a játékos telephelyen van és elegendő vagyonnal rendelkezik.
     * @param k a megvásárolni kívánt fej
     * @return true, ha a vásárlás sikeres volt, false egyébként
     */
    public boolean fejVasarlas(Fej k) {
        System.out.println("TakaritoManager.fejVasarlas() meghivva.");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            return false;
        }

        fejek.add(k);
        System.out.println("Uj fej hozzaadva a takarito manager fejek listajahoz.");
        return true;
    }

    /**
     * Leszereli a fejet a hókotró gépről, ha a játékos telephelyen van és a gépen van felszerelve fej.
     * @param h a hókotró, amelyről le szeretnénk venni a fejet
     * @return true, ha a leveszélés sikeres volt, false egyébként
     */
    public boolean fejLevetel(Hokotro h) {
        System.out.println("TakaritoManager.fejLevetel() meghivva.");
        return true;
    }

    /**
     * Felteveszi a fejet a hókotró gépre, ha a játékos telephelyen van, a fej a takarító manager készletében van és a gépre nincs már felszerelve fej.
     * @param h a hókotró, amelyre fel szeretnénk tenni a fejet
     * @param f a fej, amit fel szeretnénk tenni a hókotróra
     * @return true, ha a feltevés sikeres volt, false egyébként
     */
    public boolean fejFelteves(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.fejFelteves() meghivva.");
        return true;
    }

    /**
     * Kicseréli a hókotró gép fejét egy új fejre, ha a játékos telephelyen van, a gép a takarító manager készletében van, az új fej a takarító manager készletében van és a gépre van felszerelve fej.
     * @param h a hókotró, amelynek a fejét cserélni szeretnénk
     * @param ujFej az új fej, amit fel szeretnénk tenni a hókotróra
     * @return true, ha a cserélsikeres volt, false egyébként
     */
    public boolean fejCsere(Hokotro h, Fej ujFej) {
        System.out.println("TakaritoManager.fejCsere() meghivva.");
        System.out.println("A hókotró telephelyen van? (I/N)");
        Scanner s = new Scanner(System.in);
        String valasz = s.nextLine().trim();

        if (!valasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert a hokotro nincs a telephelyen.");
            return false;
        }

        Fej regiFej = h.getAktualisFej();
        boolean sikeresLevetel = fejLevetel(h);
        if (!sikeresLevetel) {
            return false;
        }

        if (regiFej != null) {
            fejek.add(regiFej);
        }
        fejek.remove(ujFej);

        return fejFelteves(h, ujFej);
    }

    /**
     * Sót tölt a hókotróba, ha a játékos telephelyen van.
     * @param h a hókotró, amelyet fel szeretnénk tölteni
     * @return a feltöltött so mennyisége, ha sikeres volt, 0 egyébként
     */
    public int soToltes(Hokotro h) {
        System.out.println("TakaritoManager.soToltes() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return 0;
        }

        return h.soToltesAktualisFejbe();
    }

    /**
     * Kerozint tölt a hókotróba, ha a játékos telephelyen van.
     * @param h a hókotró, amelyet fel szeretnénk tölteni
     * @return a feltöltött kerozin mennyisége, ha sikeres volt, 0 egyébként
     */
    public int kerozinToltes(Hokotro h) {
        System.out.println("TakaritoManager.kerozinToltes() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return 0;
        }

        return h.kerozinToltesAktualisFejbe();
    }
}
