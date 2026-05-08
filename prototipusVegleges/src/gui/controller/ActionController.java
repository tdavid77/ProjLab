package gui.controller;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import jatekosok.Jatekos;
import jatekosok.TakaritoJatekos;
import motor.GameActions;
import motor.GameState;
import motor.NamedEntity;
import takaritofejek.Fej;
import takaritofejek.FejTipus;
import terkep.Ut;

/**
 * A GUI gombok altal kivaltott akciok kontrollere.
 *
 * Az MVC kontroller-retegehez tartozik: a felhasznaloi inputot (gombnyomas)
 * model-akciokra forditja le. Tartalmazza:
 *  - Mozgas / varakozas / takaritas akciokat (jarmuvekhez)
 *  - Telephelyi akciokat (vasarlas, fejcsere, feltoltesek)
 *  - Pass'N'Play handoff es kor-vege logikat
 *  - Jatekvege detektalast es dialogust
 *
 * Hibak eseten egyszeru JOptionPane-t mutat a felhasznalonak, igy nem omlik
 * ossze a UI a model exception-jeitol.
 */
public final class ActionController {
    private final GameState state;
    private final GameActions actions;
    /** A foablak referencia, hogy a "Uj jatek" eseten ujrainditassuk. */
    private JFrame ownerFrame;

    public ActionController(GameState state, GameActions actions) {
        this.state = state;
        this.actions = actions;
    }

    /** Beallitja a foablak referenciat (a GameWindow konstruktoraban hivjuk). */
    public void setOwnerFrame(JFrame frame) {
        this.ownerFrame = frame;
    }

    // ===========================================================================
    // JARMU AKCIOK
    // ===========================================================================

    /** Lepes a kivalasztott jarmuvel a megadott szomszedos csomopontba. */
    public void onMoveTo(String targetNode) {
        NamedEntity sel = state.selected();
        if (sel == null) return;
        Jarmu vehicle = sel.asJarmu();
        if (vehicle == null) return;
        if (!ensureControllable(vehicle)) return;
        try {
            vehicle.moveToNode(targetNode, state);
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /** A kivalasztott jarmu varakozik (esemenyt logol, fireStateChanged). */
    public void onWait() {
        NamedEntity sel = state.selected();
        if (sel == null) return;
        Jarmu vehicle = sel.asJarmu();
        if (vehicle == null) return;
        if (!ensureControllable(vehicle)) return;
        state.enqueueEvent(vehicle.name + " varakozik ezt a kort.");
        state.flushEvents();
        state.fireStateChanged();
    }

    /**
     * A kivalasztott hokotro a megadott szomszedos uton (savIndex sav) takaritast vegez.
     * A hokotrot a csomoponton kell allnia es az utnak szomszedosnak kell lennie.
     */
    public void onTakarit(String utName, int savIndex) {
        NamedEntity sel = state.selected();
        Hokotro h = sel == null ? null : sel.asHokotro();
        if (h == null) {
            warn("A takaritashoz hokotrot kell kivalasztani.");
            return;
        }
        if (!ensureControllable(h)) return;
        Ut ut = state.getUt(utName);
        if (ut == null) {
            warn("Ismeretlen ut: " + utName);
            return;
        }
        try {
            h.takaritSav(ut, savIndex, state);
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    // ===========================================================================
    // TELEPHELYI AKCIOK
    // ===========================================================================

    /** Uj hokotro vasarlasa: nev + kezdo fej dialog, majd a model muvelete. */
    public void onBuyHokotro() {
        TakaritoJatekos jatekos = resolveCleanerForDepotAction();
        if (jatekos == null) return;
        if (!state.jatekosAtTelephely(jatekos)) {
            warn("Csak telephelyen vasarolhato hokotro.");
            return;
        }

        // Dialog: nev + kezdo fej
        JTextField nameField = new JTextField(suggestNextHokotroName(jatekos));
        JRadioButton soproRadio = new JRadioButton("Söprő fej", true);
        JRadioButton jegtoroRadio = new JRadioButton("Jégtörő fej");
        ButtonGroup group = new ButtonGroup();
        group.add(soproRadio);
        group.add(jegtoroRadio);

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Új hókotró neve:"));
        panel.add(nameField);
        panel.add(new JLabel("Kezdő fej:"));
        panel.add(soproRadio);
        panel.add(jegtoroRadio);

        int result = JOptionPane.showConfirmDialog(ownerFrame, panel,
                "Új hókotró vásárlása (300 fitying)", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            warn("A név nem lehet üres.");
            return;
        }
        if (state.existsName(name)) {
            warn("Ez a név már foglalt: " + name);
            return;
        }
        FejTipus startingHead = soproRadio.isSelected() ? FejTipus.SOPROFEJ : FejTipus.JEGTOROFEJ;

        try {
            Hokotro h = jatekos.vasarolHokotro(name);
            h.setAktivFej(startingHead);
            h.currentNode = "Telephely";  // uj hokotro a Telephelyen jon letre
            state.putEntity(h);
            state.enqueueEvent("Új hókotró vásárlása: " + name + " (kezdő fej: " + startingHead.name() + ")");
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /** Uj fej vasarlasa a jatekos raktarba (kezdo opcio: ami nincs meg a jatekosnal). */
    public void onBuyFej() {
        TakaritoJatekos jatekos = resolveCleanerForDepotAction();
        if (jatekos == null) return;
        if (!state.jatekosAtTelephely(jatekos)) {
            warn("Csak telephelyen vasarolhato fej.");
            return;
        }

        FejTipus[] options = FejTipus.values();
        String[] labels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labels[i] = fejDisplay(options[i]);
        }
        JComboBox<String> combo = new JComboBox<>(labels);
        combo.setSelectedIndex(1); // alapertelmezetten Hanyofej

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Melyik fejet szeretnéd venni?"));
        panel.add(combo);

        int result = JOptionPane.showConfirmDialog(ownerFrame, panel,
                "Fej vásárlása (80 fitying)", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        FejTipus chosen = options[combo.getSelectedIndex()];
        try {
            jatekos.vasarolFej(chosen);
            state.enqueueEvent("Fej vásárolva a raktárba: " + chosen.name());
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /**
     * Fejcsere: a kivalasztott hokotro aktiv fejet kicsereli a raktarbol valasztott fejre.
     * A regi fej a raktarba kerul.
     */
    public void onSwapFej() {
        NamedEntity sel = state.selected();
        Hokotro h = sel == null ? null : sel.asHokotro();
        if (h == null) {
            warn("Fejcseréhez hókotrót kell kiválasztani.");
            return;
        }
        TakaritoJatekos jatekos = state.getTakaritoJatekos(h.owner);
        if (jatekos == null) {
            warn("Nem találom a hókotró tulajdonosát.");
            return;
        }
        if (!state.jatekosAtTelephely(jatekos)) {
            warn("Fejcsere csak telephelyen lehetséges.");
            return;
        }

        java.util.List<FejTipus> inv = jatekos.getFejInventory();
        if (inv.isEmpty()) {
            warn("Üres a raktár — előbb vegyél új fejet.");
            return;
        }

        Fej current = h.getAktivFej();
        String currentName = current == null ? "(nincs)" : fejDisplay(current.tipus());

        String[] labels = new String[inv.size()];
        for (int i = 0; i < inv.size(); i++) {
            labels[i] = fejDisplay(inv.get(i));
        }
        JComboBox<String> combo = new JComboBox<>(labels);

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Jelenlegi fej: " + currentName));
        panel.add(new JLabel("Cserélj a raktárból:"));
        panel.add(combo);

        int result = JOptionPane.showConfirmDialog(ownerFrame, panel,
                "Fejcsere (ingyenes)", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        FejTipus chosen = inv.get(combo.getSelectedIndex());
        try {
            h.fejCsere(jatekos, chosen);
            state.enqueueEvent("Fejcsere: " + h.name + " -> " + chosen.name());
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /** A kivalasztott hokotro sokeszletet feltolti (50 fitying). */
    public void onRefillSalt() {
        Hokotro h = requireSelectedDepotHokotro();
        if (h == null) return;
        try {
            TakaritoJatekos jatekos = state.getTakaritoJatekos(h.owner);
            h.sotoltes(jatekos);
            state.enqueueEvent(h.name + " sókészlete feltöltve. Ár: 50.");
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /** A kivalasztott hokotro kerozinkeszletet feltolti (60 fitying). */
    public void onRefillKerozin() {
        Hokotro h = requireSelectedDepotHokotro();
        if (h == null) return;
        try {
            TakaritoJatekos jatekos = state.getTakaritoJatekos(h.owner);
            h.kerozintoltes(jatekos);
            state.enqueueEvent(h.name + " kerozinkészlete feltöltve. Ár: 60.");
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    /** A kivalasztott hokotro zuzottko-keszletet feltolti (40 fitying). */
    public void onRefillZuzottko() {
        Hokotro h = requireSelectedDepotHokotro();
        if (h == null) return;
        try {
            TakaritoJatekos jatekos = state.getTakaritoJatekos(h.owner);
            h.zuzalektoltes(jatekos);
            state.enqueueEvent(h.name + " zúzottkő készlete feltöltve. Ár: 40.");
            state.flushEvents();
            state.fireStateChanged();
        } catch (RuntimeException ex) {
            warn(ex.getMessage());
        }
    }

    // ===========================================================================
    // PASS'N'PLAY HANDOFF
    // ===========================================================================

    /**
     * "Kesz vagyok" gomb: ha Takarito aktiv -> vall Buszosra (modallal);
     * ha Buszos aktiv -> kor vege (NPC-k, havazas, stb.) + uj kor + reset Takarito-ra.
     * Jatekvege eseten dialogust mutat.
     */
    public void onPlayerDone() {
        if (!state.running) {
            // Mar veget ert -- mutassuk meg ujra a dialogust
            showGameOverDialog();
            return;
        }

        if ("Takarito1".equalsIgnoreCase(state.activePlayerName)) {
            // Takarito kesz - valts Buszosra
            state.switchActivePlayer();
            state.fireStateChanged();
            JOptionPane.showMessageDialog(ownerFrame,
                    "A Takarító játékos befejezte a körét.\n\nAdd át a gépet a Buszosnak!",
                    "Pass'N'Play",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Buszos kesz - kor vege + reset
            JOptionPane.showMessageDialog(ownerFrame,
                    "A Buszos befejezte a körét.\n\nMost az NPC autók lépnek és havazik.",
                    "Pass'N'Play",
                    JOptionPane.INFORMATION_MESSAGE);
            try {
                actions.endRound();
            } catch (RuntimeException ex) {
                warn("Hiba a kor vege soran: " + ex.getMessage());
            }
            state.resetActivePlayer();
            state.fireStateChanged();

            if (!state.running) {
                showGameOverDialog();
                return;
            }
            JOptionPane.showMessageDialog(ownerFrame,
                    "Új kör (" + state.currentRound + ")!\n\nAdd át a gépet a Takarítónak.",
                    "Pass'N'Play",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===========================================================================
    // JATEKVEGE
    // ===========================================================================

    private void showGameOverDialog() {
        String reason = state.gameOverReason == null ? "Ismeretlen ok." : state.gameOverReason;
        Object[] options = { "Új játék", "Kilépés" };
        int choice = JOptionPane.showOptionDialog(ownerFrame,
                "JÁTÉK VÉGE\n\nOk: " + reason + "\n\nKör: " + state.currentRound + ", balesetek: " + state.accidents,
                "Játék vége",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        if (choice == 0) {
            // Uj jatek - bezarjuk a jelenlegit, foemeunut nyitunk
            if (ownerFrame != null) {
                ownerFrame.dispose();
            }
            javax.swing.SwingUtilities.invokeLater(() -> {
                new gui.MainMenu().setVisible(true);
            });
        } else {
            System.exit(0);
        }
    }

    // ===========================================================================
    // SEGEDMETODUSOK
    // ===========================================================================

    /** Engedelyezi-e az aktiv jatekos, hogy ezzel a jarmuvel cselekedjen? */
    private boolean ensureControllable(Jarmu vehicle) {
        if (state.canControl(vehicle)) return true;
        Jatekos owner = state.getJatekos(vehicle.owner);
        String ownerLabel = owner == null ? "egy másik játékos" : owner.name();
        warn("Ezt a járművet most nem te irányíthatod (tulajdonos: " + ownerLabel
                + "). Aktív játékos: " + state.activePlayerName);
        return false;
    }

    /** Megkeresi a takarito jatekost: a kivalasztott hokotro tulajdonosa, vagy a kivalasztott takarito. */
    private TakaritoJatekos resolveCleanerForDepotAction() {
        NamedEntity sel = state.selected();
        if (sel == null) {
            warn("Először válassz ki egy hókotrót vagy a takarító játékost.");
            return null;
        }
        Hokotro h = sel.asHokotro();
        if (h != null && h.owner != null) {
            TakaritoJatekos jatekos = state.getTakaritoJatekos(h.owner);
            if (jatekos != null) return jatekos;
        }
        TakaritoJatekos asTak = sel.asTakaritoJatekos();
        if (asTak != null) return asTak;
        warn("A kiválasztott entitás nem takarító játékos és nem hókotró.");
        return null;
    }

    /** Visszaadja a kivalasztott telephelyen alló hokotrot, vagy null-t (warn-nal). */
    private Hokotro requireSelectedDepotHokotro() {
        NamedEntity sel = state.selected();
        Hokotro h = sel == null ? null : sel.asHokotro();
        if (h == null) {
            warn("Ehhez az akcióhoz hókotrót kell kiválasztani.");
            return null;
        }
        if (!"Telephely".equalsIgnoreCase(h.currentNode)) {
            warn("A hókotró nincs a Telephelyen.");
            return null;
        }
        return h;
    }

    /** Javasolt nev az uj hokotrohoz: Hokotro2, Hokotro3, ... */
    private String suggestNextHokotroName(TakaritoJatekos jatekos) {
        int idx = jatekos.vehicles.size() + 1;
        while (state.existsName("Hokotro" + idx)) {
            idx++;
        }
        return "Hokotro" + idx;
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

    private void warn(String message) {
        JOptionPane.showMessageDialog(ownerFrame, message, "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
    }
}
