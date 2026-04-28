package jatekosok;

/**
 * Hokotrokat uzemeltetö jatekos szerepkor, a takaritasi muveletek gazdaja.
 * Feladata hokotrok vasarlasa, fejek beserzetese, keszletek feltoltese
 * es a hokotrok iranyitasa a savok takaritasahoz.
 * Konkret uj metodus nincs benne: a specializalt viselkedes (pl. vasarlasok)
 * a Jatekos alaposztályban es a Hokotro-ban talalhato.
 */
public final class TakaritoJatekos extends Jatekos {
    public TakaritoJatekos(String name) {
        super(name);
    }

    @Override
    public TakaritoJatekos asTakaritoJatekos() { return this; }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "TakaritoJatekos";
    }
}
