import java.util.ArrayList;
import java.util.List;

/**
 * Két pontot összekötő, sávokból álló közlekedési vonal.
 * A sávokat összefogja és kezeli az útszakasz hosszát.
 */
public class Ut {
    private List<Character> vegpontok;
    private int hossz;
    private String nev;
    private List<Sav> eloreSavok = new ArrayList<>();
    private List<Sav> visszaSavok = new ArrayList<>();
    private UtTipus tipus;
}
