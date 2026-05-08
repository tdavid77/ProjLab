package gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import motor.GameState;
import motor.GameStateListener;

/**
 * A jatekeselemek logja (BorderLayout.SOUTH).
 *
 * Push-feliratkozas alapján frissül: ahogy a model esemenyeket generál
 * (lépés, takaritas, baleset, jatekvege), a UI itt az utolso 10-12 esemenyt mutatja.
 *
 * A meglevo flushEvents() (konzolra ir) erintetlen marad - ez a panel
 * kulon forrasbol (recentEvents) olvas, igy a CLI viselkedes nem valtozik.
 */
public final class EventLogPanel extends JPanel implements GameStateListener {
    private final JTextArea log;

    public EventLogPanel(GameState state) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createTitledBorder("Események"));
        setPreferredSize(new Dimension(0, 110));

        this.log = new JTextArea();
        log.setEditable(false);
        log.setOpaque(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        // Halk jelmagyarazat a log mellé
        JLabel hint = new JLabel(
                "<html><font color='#666666' size='2'>"
                + "Szín: <b>fehér</b>=havas, <b>világoskék</b>=jeges, <b>szürke</b>=sózott, "
                + "<b>barna</b>=zúzottkő, <b>fekete</b>=sima út</font></html>");
        hint.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        add(hint, BorderLayout.SOUTH);
    }

    @Override
    public void onStateChanged(GameState state) {
        List<String> events = state.getRecentEvents();
        StringBuilder sb = new StringBuilder();
        // Visszafele: legfrissebb foltesz a tetejen
        for (int i = events.size() - 1; i >= 0; i--) {
            sb.append("• ").append(events.get(i)).append('\n');
        }
        log.setText(sb.toString());
        log.setCaretPosition(0);
    }
}
