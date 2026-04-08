package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import takaritofejek.Fej;
import skeletonprogram.SzkeletonProgram;

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
        SzkeletonProgram.logCall("tm", "TakaritoManager", "mozgatJarmuvet", "cel, j");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Vásárol egy új hókotró gépet, ha a játékos telephelyen van és elegendő vagyonnal rendelkezik.
     * @param h a megvásárolni kívánt hókotró
     * @return true, ha a vásárlás sikeres volt, false egyébként
     */
    public boolean hokotroVasarlas(Hokotro h) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "hokotroVasarlas", "h");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            SzkeletonProgram.logReturn("false");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            SzkeletonProgram.logReturn("false");
            return false;
        }

        hokotrok.add(h);
        System.out.println("Uj hokotro hozzaadva a takarito manager hokotro listajahoz.");
        SzkeletonProgram.logReturn("true");
        return true;
    }

    /**
     * Vásárol egy új fejet a hókotró gépekhez, ha a játékos telephelyen van és elegendő vagyonnal rendelkezik.
     * @param k a megvásárolni kívánt fej
     * @return true, ha a vásárlás sikeres volt, false egyébként
     */
    public boolean fejVasarlas(Fej k) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "fejVasarlas", "k");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            SzkeletonProgram.logReturn("false");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            SzkeletonProgram.logReturn("false");
            return false;
        }

        fejek.add(k);
        System.out.println("Uj fej hozzaadva a takarito manager fejek listajahoz.");
        SzkeletonProgram.logReturn("true");
        return true;
    }

    /**
     * Leszereli a fejet a hókotró gépről, ha a játékos telephelyen van és a gépen van felszerelve fej.
     * @param h a hókotró, amelyről le szeretnénk venni a fejet
     * @return true, ha a leveszélés sikeres volt, false egyébként
     */
    public boolean fejLevetel(Hokotro h) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "fejLevetel", "h");
        if (h == null) {
            System.out.println("A hokotro nem letezik.");
            SzkeletonProgram.logReturn("false");
            return false;
        }
        if (h.getAktualisFej() == null) {
            System.out.println("A hokotron nincs feju" + "zes.");
            SzkeletonProgram.logReturn("false");
            return false;
        }
        Fej levettFej = h.getAktualisFej();
        h.setAktualisFej(null);
        fejek.add(levettFej);
        System.out.println("A fej leszerelve a hokotrorol és a keszletbe helyezve.");
        SzkeletonProgram.logReturn("true");
        return true;
    }

    /**
     * Felteveszi a fejet a hókotró gépre, ha a játékos telephelyen van, a fej a takarító manager készletében van és a gépre nincs már felszerelve fej.
     * @param h a hókotró, amelyre fel szeretnénk tenni a fejet
     * @param f a fej, amit fel szeretnénk tenni a hókotróra
     * @return true, ha a feltevés sikeres volt, false egyébként
     */
    public boolean fejFelteves(Hokotro h, Fej f) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "fejFelteves", "h, f");
        if (h == null || f == null) {
            System.out.println("Hibas bemenet a fej felhelyezese soran.");
            SzkeletonProgram.logReturn("false");
            return false;
        }
        if (h.getAktualisFej() != null) {
            System.out.println("A hokotro mar tartalmaz egy fejet.");
            SzkeletonProgram.logReturn("false");
            return false;
        }
        if (!fejek.contains(f)) {
            fejek.add(f);
        }
        h.setAktualisFej(f);
        System.out.println("A fej sikeresen felkerult a hokotro fejehez.");
        SzkeletonProgram.logReturn("true");
        return true;
    }

    /**
     * Kicseréli a hókotró gép fejét egy új fejre, ha a játékos telephelyen van, a gép a takarító manager készletében van, az új fej a takarító manager készletében van és a gépre van felszerelve fej.
     * @param h a hókotró, amelynek a fejét cserélni szeretnénk
     * @param ujFej az új fej, amit fel szeretnénk tenni a hókotróra
     * @return true, ha a cserélsikeres volt, false egyébként
     */
    public boolean fejCsere(Hokotro h, Fej ujFej) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "fejCsere", "h, ujFej");
        System.out.println("A hókotró telephelyen van? (I/N)");
        Scanner s = new Scanner(System.in);
        String valasz = s.nextLine().trim();

        if (!valasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert a hokotro nincs a telephelyen.");
            SzkeletonProgram.logReturn("false");
            return false;
        }

        Fej regiFej = h.getAktualisFej();
        if (regiFej != null) {
            boolean sikeresLevetel = fejLevetel(h);
            if (!sikeresLevetel) {
                SzkeletonProgram.logReturn("false");
                return false;
            }
        }

        if (!fejek.contains(ujFej)) {
            fejek.add(ujFej);
        }

        boolean ret = fejFelteves(h, ujFej);
        SzkeletonProgram.logReturn(ret ? "true" : "false");
        return ret;
    }

    /**
     * Sót tölt a hókotróba, ha a játékos telephelyen van.
     * @param h a hókotró, amelyet fel szeretnénk tölteni
     * @return a feltöltött so mennyisége, ha sikeres volt, 0 egyébként
     */
    public int soToltes(Hokotro h) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "soToltes", "h");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            SzkeletonProgram.logReturn("0");
            return 0;
        }

        int ret = h.soToltesAktualisFejbe();
        SzkeletonProgram.logReturn("int");
        return ret;
    }

    /**
     * Kerozint tölt a hókotróba, ha a játékos telephelyen van.
     * @param h a hókotró, amelyet fel szeretnénk tölteni
     * @return a feltöltött kerozin mennyisége, ha sikeres volt, 0 egyébként
     */
    public int kerozinToltes(Hokotro h) {
        SzkeletonProgram.logCall("tm", "TakaritoManager", "kerozinToltes", "h");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            SzkeletonProgram.logReturn("0");
            return 0;
        }

        int ret = h.kerozinToltesAktualisFejbe();
        SzkeletonProgram.logReturn("int");
        return ret;
    }
}
