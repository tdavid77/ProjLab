package jarmuvek;

import skeletonprogram.SzkeletonProgram;

/**
 * Előre megadott végállomások között közlekedő eszköz.
 * Felelőssége a menetrend szerinti haladás és a bevétel generálása.
 */
public class Busz extends Jarmu {
    private int jaratSzam;
    private int korokSzama;

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    @Override
    public void lep(char cel) {
        SzkeletonProgram.logCall("b", "Busz", "lep", "cel");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Baleset történik.
     */
    @Override
    public void balesetezik() {
        SzkeletonProgram.logCall("b", "Busz", "balesetezik", "");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Növeli a megtett körök számát, ha a busz elérte valamelyik végállomását és kifizeti a sofőrt.
     */
    public void korRegisztralasa() {
        SzkeletonProgram.logCall("b", "Busz", "korRegisztralasa", "");
        SzkeletonProgram.logReturn("void");
    }
}
