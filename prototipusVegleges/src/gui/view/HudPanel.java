package gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import gui.controller.ActionController;
import jatekosok.Jatekos;
import motor.GameState;
import motor.GameStateListener;

/**
 * A felso allapotsav (BorderLayout.NORTH).
 *
 * Mutatja:
 *  - aktualis kor sorszama
 *  - aktiv jatekos (Pass'N'Play)
 *  - mindket jatekos vagyona fityingben
 *  - balesetszam
 *
 * Jobb oldalon van a "Kesz vagyok" gomb. Ha az aktiv jatekos mar elhasznalta
 * az osszes elerheto lepest a korben, a gomb narancssargara emelkedik ki.
 */
public final class HudPanel extends JPanel implements GameStateListener {
    private static final Color HIGHLIGHT_BG = new Color(255, 175, 50);
    private static final Color HIGHLIGHT_FG = new Color(40, 30, 20);
    private static final Color HIGHLIGHT_BORDER = new Color(220, 130, 30);

    private final JLabel info;
    private final JButton doneButton;
    private final javax.swing.border.Border defaultButtonBorder;
    private final Color defaultButtonBg;
    private final Color defaultButtonFg;

    public HudPanel(GameState state, ActionController actions) {
        setBackground(new Color(232, 240, 250));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(120, 130, 150)));
        setLayout(new BorderLayout());

        // Bal: info
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 8));
        left.setOpaque(false);
        this.info = new JLabel(" ");
        info.setFont(new Font("SansSerif", Font.BOLD, 13));
        left.add(info);
        add(left, BorderLayout.WEST);

        // Jobb: Pass'N'Play / Kor vege gomb
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 6));
        right.setOpaque(false);
        this.doneButton = new JButton("Kész vagyok");
        doneButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        doneButton.setFocusPainted(false);
        doneButton.addActionListener(e -> actions.onPlayerDone());
        right.add(doneButton);
        add(right, BorderLayout.EAST);

        this.defaultButtonBorder = doneButton.getBorder();
        this.defaultButtonBg = doneButton.getBackground();
        this.defaultButtonFg = doneButton.getForeground();
    }

    @Override
    public void onStateChanged(GameState state) {
        Jatekos takarito = state.getJatekos("Takarito1");
        Jatekos buszos = state.getJatekos("Buszos1");

        boolean takaritoAktiv = "Takarito1".equalsIgnoreCase(state.activePlayerName);
        String activeLabel = takaritoAktiv ? "Takarító" : "Buszos";

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("Kör: <b>").append(state.currentRound).append("</b>");
        sb.append(" &nbsp;|&nbsp; Aktív: <b><font color='#A03000'>")
          .append(activeLabel).append("</font></b>");
        sb.append(" &nbsp;|&nbsp; Nehézség: <b>").append(state.difficulty.name()).append("</b>");
        if (takarito != null) {
            sb.append(" &nbsp;|&nbsp; Takarító: <b>").append(takarito.money).append("</b> fitying");
        }
        if (buszos != null) {
            sb.append(" &nbsp;|&nbsp; Buszos: <b>").append(buszos.money).append("</b> fitying");
        }
        sb.append(" &nbsp;|&nbsp; Balesetek: <b>").append(state.accidents).append("</b>");
        sb.append("</html>");
        info.setText(sb.toString());

        // Gomb cimkeje
        if (takaritoAktiv) {
            doneButton.setText("Takarító kész → Buszos");
        } else {
            doneButton.setText("Buszos kész → Új kör");
        }
        doneButton.setEnabled(state.running);

        // Highlight: ha az aktiv jatekosnak nincs tobb mozgasa
        boolean shouldHighlight = state.running && !state.activePlayerHasMovesLeft();
        if (shouldHighlight) {
            doneButton.setBackground(HIGHLIGHT_BG);
            doneButton.setForeground(HIGHLIGHT_FG);
            doneButton.setOpaque(true);
            doneButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HIGHLIGHT_BORDER, 2),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)));
        } else {
            doneButton.setBackground(defaultButtonBg);
            doneButton.setForeground(defaultButtonFg);
            doneButton.setOpaque(false);
            doneButton.setBorder(defaultButtonBorder);
        }
    }
}
