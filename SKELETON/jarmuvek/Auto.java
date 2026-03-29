package jarmuvek;
import java.util.ArrayList;
import java.util.List;
import skeletonprogram.SzkeletonProgram;

/**
 * A város lakóit szállító önműködő (NPC) jármű.
 * Súlyával tömöríti a havat, elakadás esetén várakozik.
 */
public class Auto extends Jarmu {
    private char otthon;
    private char munkahely;
    private List<Character> utvonal = new ArrayList<>();

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    @Override
    public void lep(char cel) {
        SzkeletonProgram.logCall("a", "Auto", "lep", "cel");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Baleset történik.
     */
    @Override
    public void balesetezik() {
        SzkeletonProgram.logCall("a", "Auto", "balesetezik", "");
        SzkeletonProgram.logReturn("void");
    }
}
