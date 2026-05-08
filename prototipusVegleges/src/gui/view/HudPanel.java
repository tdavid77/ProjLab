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
 *  - mindkét jatekos vagyona fityingben
 *  - balesetszam
 *
 * Jobb oldalon van a "Kesz vagyok" gomb, ami a Pass'N'Play handoff-ot inditja.
 * A gomb cimkeje az aktiv jatekostol fugg:
 *   - Takarito aktiv: "Takarító kész → Buszos jön"
 *   - Buszos aktiv:   "Buszos kész → Új kör"
 */
public final class HudPanel extends JPanel implements GameStateListener {
    private final JLabel info;
    private final JButton doneButton;

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
        doneButton.addActionListener(e -> actions.onPlayerDone());
        right.add(doneButton);
        add(right, BorderLayout.EAST);
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
    }
}
