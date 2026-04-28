package jatekosok;

/**
 * Buszokat kezelő jatekos szerepkor, a Jatekos specializacioja.
 * Feladata a buszok mozgatasa es a buszokkal kapcsolatos jatekmenet megvalositasa.
 * Penzt gyujt a kozos kasszaba, amelyet minden egyes teljesitett kor utan kap.
 * Nem ad hozza uj viselkedest a Jatekos alaposztályhoz; a bovitesek helye
 * a kesöbbi iteraciokban itt lesz.
 */
public final class BuszosJatekos extends Jatekos {
    public BuszosJatekos(String name) {
        super(name);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "BuszosJatekos";
    }
}
