package skeletonprogram;

import java.util.ArrayList;
import java.util.List;

/**
 * Ket halozati csomopont (nodeA, nodeB) kozotti utszakaszt modellez.
 * Tartalmazza az uttipust (NORMAL, HID, ALAGUT), a hosszat es az uton
 * talalhato savok listajat (List<Sav>).
 * A hasNode() es opposite() segitsegevel a szomszedossagi logika
 * hatekonyan navigalhat a grafban.
 * Az alkalmazHoEses() az osszes savon egyszerre vegzi el a ho-novelest,
 * figyelembe veve az uttipust (alagut vedettseg).
 */
public final class Ut {
    /** Az ut azonositoneve. */
    private String name;
    /** Az ut egyik vegpontjanak neve. */
    final String nodeA;
    /** Az ut masik vegpontjanak neve. */
    final String nodeB;
    /** Az ut hossza (tetszoleges egyseg, terkep-megjeleníteshez). */
    final int length;
    /** Az ut tipusa (NORMAL, HID, ALAGUT); befolyasolja a havazas hatasat. */
    final UtTipus type;
    /** Az ut savjainak listaja (index: 0-tol savSzam-1-ig). */
    final List<Sav> savok;

    public Ut(String name, String nodeA, String nodeB, int length, UtTipus type, int savSzam) {
        this.name = name;
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.length = length;
        this.type = type;
        this.savok = new ArrayList<>();
        for (int i = 0; i < savSzam; i++) {
            savok.add(new Sav(i));
        }
    }

    public String name() {
        return name;
    }

    public void renameTo(String newName) {
        this.name = newName;
    }

    /** Igaz, ha a megadott csomopont az ut valamelyik vegpontja (nodeA vagy nodeB). */
    public boolean hasNode(String node) {
        return nodeA.equalsIgnoreCase(node) || nodeB.equalsIgnoreCase(node);
    }

    /** Visszaadja az ut masik vegpontjat a megadott csomopont alapjan, vagy null-t. */
    public String opposite(String node) {
        if (nodeA.equalsIgnoreCase(node)) {
            return nodeB;
        }
        if (nodeB.equalsIgnoreCase(node)) {
            return nodeA;
        }
        return null;
    }

    public int savSzam() {
        return savok.size();
    }

    public Sav sav(int idx) {
        return savok.get(idx);
    }

    /** Osszeszamlalja az ut osszes savjan levo ho-egysegeket. */
    public int sumHo() {
        int total = 0;
        for (Sav sav : savok) {
            total += sav.ho;
        }
        return total;
    }

    /**
     * Egykor-nyi havazas hatasat alkalmazza az ut osszes savjara.
     * Visszaadja a ho-szint valtozasat (delta); alagut eseten ez 0.
     */
    public int alkalmazHoEses() {
        int before = sumHo();
        for (Sav sav : savok) {
            sav.hoingOneUnit(type == UtTipus.ALAGUT);
        }
        int after = sumHo();
        return after - before;
    }

    /** Rovid szoveges leirast ad az utrol es savjairol a konzolos kijelzo szamara. */
    public String describeCompact() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ut ").append(name).append(" (").append(type.name()).append(")")
            .append(" | Szomszedok:[").append(nodeA).append(", ").append(nodeB).append("]")
            .append(" | Savok: ");
        List<String> savText = new ArrayList<>();
        for (Sav sav : savok) {
            savText.add(sav.describeCompact());
        }
        sb.append(String.join(", ", savText));
        return sb.toString();
    }
}
