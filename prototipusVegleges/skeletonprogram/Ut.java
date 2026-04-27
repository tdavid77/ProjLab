package skeletonprogram;

import java.util.ArrayList;
import java.util.List;

public final class Ut {
    private String name;
    final String nodeA;
    final String nodeB;
    final int length;
    final UtTipus type;
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

    public boolean hasNode(String node) {
        return nodeA.equalsIgnoreCase(node) || nodeB.equalsIgnoreCase(node);
    }

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

    public int sumHo() {
        int total = 0;
        for (Sav sav : savok) {
            total += sav.ho;
        }
        return total;
    }

    public int alkalmazHoEses() {
        int before = sumHo();
        for (Sav sav : savok) {
            sav.hoingOneUnit(type == UtTipus.ALAGUT);
        }
        int after = sumHo();
        return after - before;
    }

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
