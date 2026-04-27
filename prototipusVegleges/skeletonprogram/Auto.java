package skeletonprogram;

/**
 *  Önműködő (NPC) jármű, amely az otthona és a munkahelye között próbál eljutni a legrövidebb úton. 
 *  Lépéseivel, súlyát felhasználva hozzájárul a hó jéggé tömörítéséhez. Kiemelt felelőssége a várakozási logika: 
 *  ha az út elzáródik, nem tervez újra, nem fordul meg, hanem a legutolsó pontnál feltorlódva várakozik, amíg a hókotrók fel nem szabadítják az utat.
 *  Az Auto semmi jatekos-specifikus viselkedest nem tartalmaz; minden logikaja
 *  az alaposztaly Jarmu-ban van definihalva.
 */
public final class Auto extends Jarmu {
    public Auto(String name) {
        super(name);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Auto";
    }
}
