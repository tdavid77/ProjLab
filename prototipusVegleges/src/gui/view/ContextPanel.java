package gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
 * Dinamikus tartalom:
 *  - Ha nincs kijelolve semmi: ures uzenetet mutat.
 *  - Ha jarmu van kijelolve: adatok + akciogombok (lepes a szomszedos utakra,
 *    varakozas, takaritas a hokotrok eseten).
 *
 * Az actiongombok az ActionController publikus metodusait hivjak. A model
 * fireStateChanged()-en keresztul ujrarajzolja magat.
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

        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createTitledBorder("Információk"));
        setLayout(new BorderLayout());

        // Felso resz: cim + adatok
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

        // Action panel a kozepes reszben (gorgethetoen, ha sok gomb van)
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

        addMovementButtons(vehicle);
        addCommonActionButtons(vehicle, sel);

        // Telephelyi akciók: csak hokotrohoz, csak telephelyen
        Hokotro depotHokotro = sel.asHokotro();
        if (depotHokotro != null && depotHokotro.currentUt == null) {
            addDepotActionButtons();
        }

        actionPanel.revalidate();
        actionPanel.repaint();
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

        if (vehicle.currentUt == null) {
            sb.append("Pozíció: Telephely\n");
        } else {
            sb.append("Pozíció: ").append(vehicle.currentUt).append('\n');
            sb.append("Sáv: ").append(vehicle.savIndex).append('\n');
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

    private void addMovementButtons(Jarmu vehicle) {
        JLabel header = sectionLabel("Mozgás");
        actionPanel.add(header);

        List<Ut> targets = collectAdjacentRoads(vehicle);
        if (targets.isEmpty()) {
            JLabel none = new JLabel("(nincs szomszedos ut)");
            none.setFont(new Font("SansSerif", Font.ITALIC, 11));
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            none.setBorder(BorderFactory.createEmptyBorder(2, 6, 4, 6));
            actionPanel.add(none);
        } else {
            for (Ut target : targets) {
                String label = "→ " + target.name() + " (" + describeOpposite(vehicle, target) + ")";
                JButton btn = makeButton(label);
                btn.addActionListener(e -> actions.onMoveTo(target.name()));
                actionPanel.add(btn);
            }
        }
        actionPanel.add(Box.createVerticalStrut(8));
    }

    private void addCommonActionButtons(Jarmu vehicle, NamedEntity sel) {
        JLabel header = sectionLabel("Akciók");
        actionPanel.add(header);

        // Varakozas mindig van
        JButton waitBtn = makeButton("Várakozás");
        waitBtn.addActionListener(e -> actions.onWait());
        actionPanel.add(waitBtn);

        // Hokotro takaritas
        Hokotro h = sel.asHokotro();
        if (h != null) {
            JButton takBtn = buildTakaritButton(h);
            actionPanel.add(takBtn);
        }
    }

    /**
     * A takaritas-gomb cimkeje es engedélyezett/letiltott állapota az aktiv fejtol fugg.
     * Hokotrot kell kivalasztani es az kell hogy uton legyen + nyersanyag eleg legyen.
     */
    private JButton buildTakaritButton(Hokotro h) {
        Fej fej = h.getAktivFej();
        FejTipus tipus = fej == null ? FejTipus.SOPROFEJ : fej.tipus();

        String label;
        boolean enabled = h.currentUt != null && h.canMove();
        switch (tipus) {
            case SOPROFEJ:
                label = "Söprés";
                break;
            case HANYOFEJ:
                label = "Hányás";
                break;
            case JEGTOROFEJ:
                label = "Jégtörés";
                break;
            case SOSZOROFEJ:
                label = "Sózás (10 só)";
                if (h.so < 10) enabled = false;
                break;
            case SARKANYFEJ:
                label = "Sárkány aktiválás (10 kerozin)";
                if (h.kerozin < 10) enabled = false;
                break;
            case ZUZOTTKOSZOROFEJ:
                label = "Zúzottkő szórás (10)";
                if (h.zuzottko < 10) enabled = false;
                break;
            default:
                label = "Takarítás";
        }

        JButton btn = makeButton(label);
        btn.setEnabled(enabled);
        btn.addActionListener(e -> actions.onTakarit());
        if (h.currentUt == null) {
            btn.setToolTipText("A hókotró telephelyen van — akciók csak úton.");
        } else if (!enabled) {
            btn.setToolTipText("Nincs elég nyersanyag a fej működtetéséhez.");
        }
        return btn;
    }

    /**
     * Osszegyujti azokat az utakat, amelyek a jelenlegi pozicioBOL elerhetok.
     * Telephelyen alló jarmu eseten: a Telephely csomopontnak szomszedos utak.
     * Uton lévő jarmu eseten: a jelenleg utat tartalmazo csomopontok mindkettojebol szomszedos utak,
     * de a jelenlegi utat kihagyjuk.
     */
    private List<Ut> collectAdjacentRoads(Jarmu vehicle) {
        List<Ut> result = new ArrayList<>();
        if (vehicle.currentUt == null) {
            // Telephelyen: minden ut, ami a "Telephely" csomopontot erinti
            for (Ut ut : state.getAllUtak()) {
                if (ut.hasNode("Telephely")) {
                    result.add(ut);
                }
            }
            return result;
        }
        Ut current = state.getUt(vehicle.currentUt);
        if (current == null) return result;
        for (Ut ut : state.getAllUtak()) {
            if (ut.name().equalsIgnoreCase(current.name())) continue;
            if (ut.hasNode(current.nodeA) || ut.hasNode(current.nodeB)) {
                result.add(ut);
            }
        }
        return result;
    }

    /** Visszaad egy szöveget, ami azt mondja meg, melyik csomopontba érne be a jarmu az adott uton. */
    private String describeOpposite(Jarmu vehicle, Ut target) {
        if (vehicle.currentUt == null) {
            return target.opposite("Telephely");
        }
        Ut current = state.getUt(vehicle.currentUt);
        if (current == null) return target.nodeA + " / " + target.nodeB;
        // Mely csomoponton osztozik a celut a jelenlegi uttal?
        String shared = null;
        if (target.hasNode(current.nodeA)) shared = current.nodeA;
        else if (target.hasNode(current.nodeB)) shared = current.nodeB;
        if (shared == null) return target.nodeA + " / " + target.nodeB;
        return target.opposite(shared);
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
