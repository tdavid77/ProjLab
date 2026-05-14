package gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import gui.GameLayout;
import gui.controller.ActionController;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import motor.GameState;
import motor.GameStateListener;
import motor.NamedEntity;
import takaritofejek.Fej;
import takaritofejek.FejTipus;
import terkep.Ut;

/**
 * A jobb oldali kontextualis panel (BorderLayout.EAST).
 *
 * UJ MODELL:
 *  - Mozgas: a kivalasztott jarmu csomopontjabol szomszedos csomopontokba lephet.
 *    Per szomszedos csomopont egy gomb pl. "→ Foter (Fout)".
 *  - Takaritas (csak hokotro eseten): per szomszedos ut egy gomb pl.
 *    "Söprés: Fout (sáv 0)". A fej tipusa szerinti cimke + nyersanyag-ellenorzes alapján enabled/disabled.
 *  - Telephelyi akciok: ha a hokotro a Telephely csomoponton all.
 */
public final class ContextPanel extends JPanel implements GameStateListener {
    private final GameState state;
    private final ActionController actions;

    private final JLabel title;
    private final JTextArea details;
    private final JPanel actionPanel;

    public ContextPanel(GameState state, ActionController actions) {
        this.state = state;
        this.actions = actions;

        setPreferredSize(new Dimension(310, 0));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createTitledBorder("Információk"));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        this.title = new JLabel(" ");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(title);

        this.details = new JTextArea();
        details.setEditable(false);
        details.setOpaque(false);
        details.setFont(new Font("SansSerif", Font.PLAIN, 12));
        details.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(details);

        this.actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(actionPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void onStateChanged(GameState state) {
        actionPanel.removeAll();

        NamedEntity sel = state.selected();
        if (sel == null) {
            title.setText("Nincs kijelölés");
            details.setText("Kattints valamire a részletekért!");
            actionPanel.revalidate();
            actionPanel.repaint();
            return;
        }

        Jarmu vehicle = sel.asJarmu();
        if (vehicle == null) {
            title.setText(sel.type() + ": " + sel.name());
            details.setText("(nem-jarmu entitas, nincs kozvetlen akcio)");
            actionPanel.revalidate();
            actionPanel.repaint();
            return;
        }

        title.setText(typeLabel(sel) + ": " + vehicle.name);
        details.setText(buildVehicleDetails(vehicle, sel));

        // NPC autoknal sem mozgas-, sem akciogomb nem latszik - a jatekos nem
        // tudja vezerelni oket, igy ezek a kontrollok feleslegesek.
        boolean isNpcAuto = "Auto".equals(sel.type());
        if (!isNpcAuto) {
            addMovementButtons(vehicle);
            addCommonActionButtons(vehicle, sel);
        }

        Hokotro hokotro = sel.asHokotro();
        if (hokotro != null && hokotro.currentNode != null) {
            addCleaningButtons(hokotro);
            if ("Telephely".equalsIgnoreCase(hokotro.currentNode)) {
                addDepotActionButtons();
            }
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private String typeLabel(NamedEntity sel) {
        switch (sel.type()) {
            case "Hokotro": return "Hókotró";
            case "Busz":    return "Busz";
            case "Auto":    return "Autó";
            default:        return sel.type();
        }
    }

    private String buildVehicleDetails(Jarmu vehicle, NamedEntity sel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Kijelölve: ").append(vehicle.name).append('\n');
        sb.append("Típus: ").append(typeLabel(sel)).append('\n');

        if (vehicle.currentNode != null) {
            sb.append("Csomópont: ").append(GameLayout.displayLabel(vehicle.currentNode)).append('\n');
        } else if (vehicle.currentUt != null) {
            sb.append("Elakadt: ").append(vehicle.currentUt).append(" (sáv ").append(vehicle.savIndex).append(")\n");
        }
        // NPC autoknal nem releváns a lepeshatar (Integer.MAX_VALUE-ra van allitva),
        // ezert a lepesek-sort csak a jatekos altal vezerelt jarmuveknel mutatjuk.
        if (!"Auto".equals(sel.type())) {
            sb.append("Lépések: ").append(vehicle.movesThisRound)
              .append("/").append(vehicle.getMaxMovesPerTurn()).append('\n');
        }
        sb.append("Állapot: ")
          .append(vehicle.canMove() ? "Aktív" : "Baleset (" + vehicle.disabledTime + " kör)")
          .append('\n');

        Hokotro h = sel.asHokotro();
        if (h != null) {
            Fej fej = h.getAktivFej();
            sb.append("Aktív fej: ").append(fej == null ? "(nincs)" : fejDisplay(fej.tipus())).append('\n');
            sb.append("Készletek: Só=").append(h.so)
              .append(", Kerozin=").append(h.kerozin)
              .append(", Zúzottkő=").append(h.zuzottko);
        }
        return sb.toString();
    }

    /**
     * Mozgas-gombok: a jarmu csomopontjabol elerheto szomszedos csomopontokba,
     * minden savhoz kulon gomb (a player explicit modon valasztja ki a savot).
     */
    private void addMovementButtons(Jarmu vehicle) {
        JLabel header = sectionLabel("Mozgás");
        actionPanel.add(header);

        if (vehicle.currentNode == null) {
            JLabel info = new JLabel("(elakadt — takarítás szükséges)");
            info.setFont(new Font("SansSerif", Font.ITALIC, 11));
            info.setForeground(new Color(150, 70, 70));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            info.setBorder(BorderFactory.createEmptyBorder(2, 6, 4, 6));
            actionPanel.add(info);
            actionPanel.add(Box.createVerticalStrut(8));
            return;
        }

        Map<String, Ut> neighbors = collectNeighborNodes(vehicle.currentNode);
        if (neighbors.isEmpty()) {
            JLabel none = new JLabel("(nincs szomszedos csomopont)");
            none.setFont(new Font("SansSerif", Font.ITALIC, 11));
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            none.setBorder(BorderFactory.createEmptyBorder(2, 6, 4, 6));
            actionPanel.add(none);
        } else {
            for (Map.Entry<String, Ut> entry : neighbors.entrySet()) {
                String targetNode = entry.getKey();
                Ut road = entry.getValue();
                // Minden savhoz kulon gomb
                for (int laneIdx = 0; laneIdx < road.savSzam(); laneIdx++) {
                    final int lane = laneIdx;
                    String label = "→ " + GameLayout.displayLabel(targetNode)
                            + " (" + road.name() + ", sáv " + lane + ")";
                    JButton btn = makeButton(label);
                    btn.addActionListener(e -> actions.onMoveTo(targetNode, lane));
                    actionPanel.add(btn);
                }
            }
        }
        actionPanel.add(Box.createVerticalStrut(8));
    }

    private void addCommonActionButtons(Jarmu vehicle, NamedEntity sel) {
        JLabel header = sectionLabel("Akciók");
        actionPanel.add(header);

        JButton waitBtn = makeButton("Várakozás");
        waitBtn.addActionListener(e -> actions.onWait());
        actionPanel.add(waitBtn);
    }

    /**
     * Takaritas-gombok: per szomszedos ut, MINDEN savhoz kulon gomb a fej szerinti cimkével.
     * A player explicit modon valasztja ki, hogy melyik savot akarja takaritani.
     */
    private void addCleaningButtons(Hokotro h) {
        actionPanel.add(Box.createVerticalStrut(8));
        actionPanel.add(sectionLabel("Takarítás"));

        Fej fej = h.getAktivFej();
        FejTipus tipus = fej == null ? FejTipus.SOPROFEJ : fej.tipus();

        Map<String, Ut> neighbors = collectNeighborNodes(h.currentNode);
        if (neighbors.isEmpty()) {
            JLabel none = new JLabel("(nincs szomszedos ut)");
            none.setFont(new Font("SansSerif", Font.ITALIC, 11));
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            none.setBorder(BorderFactory.createEmptyBorder(2, 6, 4, 6));
            actionPanel.add(none);
            return;
        }

        for (Map.Entry<String, Ut> entry : neighbors.entrySet()) {
            Ut road = entry.getValue();
            for (int laneIdx = 0; laneIdx < road.savSzam(); laneIdx++) {
                JButton btn = buildCleaningButton(h, tipus, road, laneIdx);
                actionPanel.add(btn);
            }
        }
    }

    private JButton buildCleaningButton(Hokotro h, FejTipus tipus, Ut road, int laneIdx) {
        String actionLabel;
        boolean enoughMaterial = true;
        switch (tipus) {
            case SOPROFEJ: actionLabel = "Söprés"; break;
            case HANYOFEJ: actionLabel = "Hányás"; break;
            case JEGTOROFEJ: actionLabel = "Jégtörés"; break;
            case SOSZOROFEJ:
                actionLabel = "Sózás";
                if (h.so < 10) enoughMaterial = false;
                break;
            case SARKANYFEJ:
                actionLabel = "Sárkány";
                if (h.kerozin < 10) enoughMaterial = false;
                break;
            case ZUZOTTKOSZOROFEJ:
                actionLabel = "Zúzottkő";
                if (h.zuzottko < 10) enoughMaterial = false;
                break;
            default: actionLabel = "Takarítás";
        }
        String fullLabel = actionLabel + ": " + road.name() + " (sáv " + laneIdx + ")";
        JButton btn = makeButton(fullLabel);
        btn.setEnabled(enoughMaterial);
        final int chosenLane = laneIdx;
        btn.addActionListener(e -> actions.onTakarit(road.name(), chosenLane));
        if (!enoughMaterial) {
            btn.setToolTipText("Nincs elég nyersanyag a fej működtetéséhez (10 egység szükséges).");
        }
        return btn;
    }

    private void addDepotActionButtons() {
        actionPanel.add(Box.createVerticalStrut(8));
        actionPanel.add(sectionLabel("Telephelyi akciók"));

        JButton buyHok = makeButton("Új hókotró vásárlása (300)");
        buyHok.addActionListener(e -> actions.onBuyHokotro());
        actionPanel.add(buyHok);

        JButton buyFej = makeButton("Fej vásárlása (80)");
        buyFej.addActionListener(e -> actions.onBuyFej());
        actionPanel.add(buyFej);

        JButton swap = makeButton("Fejcsere (ingyenes)");
        swap.addActionListener(e -> actions.onSwapFej());
        actionPanel.add(swap);

        JButton refSo = makeButton("Sótöltés (50)");
        refSo.addActionListener(e -> actions.onRefillSalt());
        actionPanel.add(refSo);

        JButton refKer = makeButton("Kerozintöltés (60)");
        refKer.addActionListener(e -> actions.onRefillKerozin());
        actionPanel.add(refKer);

        JButton refZuz = makeButton("Zúzottkő töltés (40)");
        refZuz.addActionListener(e -> actions.onRefillZuzottko());
        actionPanel.add(refZuz);
    }

    /**
     * Visszaadja a megadott csomopontbol elerheto szomszedos csomopontokat es az osszekoto utat.
     * Map: szomszed-csomopont -> ut.
     */
    private Map<String, Ut> collectNeighborNodes(String fromNode) {
        Map<String, Ut> result = new LinkedHashMap<>();
        if (fromNode == null) return result;
        for (Ut ut : state.getAllUtak()) {
            if (ut.hasNode(fromNode)) {
                String other = ut.opposite(fromNode);
                if (other != null && !result.containsKey(other)) {
                    result.put(other, ut);
                }
            }
        }
        return result;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(50, 50, 80));
        l.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton makeButton(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setHorizontalAlignment(JButton.LEFT);
        return btn;
    }

    private String fejDisplay(FejTipus tipus) {
        switch (tipus) {
            case SOPROFEJ: return "Söprő fej";
            case HANYOFEJ: return "Hányó fej";
            case JEGTOROFEJ: return "Jégtörő fej";
            case SOSZOROFEJ: return "Sószóró fej";
            case SARKANYFEJ: return "Sárkány fej";
            case ZUZOTTKOSZOROFEJ: return "Zúzottkő szóró fej";
            default: return tipus.name();
        }
    }
}
