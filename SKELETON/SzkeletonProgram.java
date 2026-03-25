/**
 * A Szkeleton fő programja, amely belépési pontként szolgál.
 * Ebből a fájlból tesztelhető a modell alapvető működése.
 */
public class SzkeletonProgram {
    public static void main(String[] args) {
        System.out.println("Szkeleton program inditasa...");
        
        Uthalozat halozat = new Uthalozat();
        Auto auto = new Auto();
        Busz busz = new Busz();
        Hokotro hokotro = new Hokotro();
        
        SoproFej sopro = new SoproFej();
        hokotro.fejKiBeKapcsolas(sopro);
        
        auto.lep('A');
        busz.lep('B');
        hokotro.lep('C');
        
        System.out.println("Szkeleton tesztfutas befejezodott.");
    }
}
