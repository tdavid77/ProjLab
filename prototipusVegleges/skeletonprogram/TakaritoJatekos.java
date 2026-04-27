package skeletonprogram;

/**
 * Hokotrokat uzemelteto jatekos szerepkor, a takaritasi muveletek gazdaja.
 */
public final class TakaritoJatekos extends Jatekos {
    public TakaritoJatekos(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "TakaritoJatekos";
    }
}
