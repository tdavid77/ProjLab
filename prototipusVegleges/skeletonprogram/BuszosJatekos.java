package skeletonprogram;

/**
 * Buszokat kezelo jatekos szerepkor, a Jatekos specializacioja.
 */
public final class BuszosJatekos extends Jatekos {
    public BuszosJatekos(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "BuszosJatekos";
    }
}
