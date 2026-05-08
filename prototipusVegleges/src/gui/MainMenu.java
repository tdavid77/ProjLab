package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import motor.Difficulty;
import takaritofejek.FejTipus;

/**
 * A jatek inditokepernyeje (Zuzmaravaros foemenu).
 *
 * Lehetove teszi a nehezsegi szint kivalasztasat (Konnyu/Kozepes/Nehez) es a
 * hokotro kezdo fejtipusanak kivalasztasat (Sopro vagy Jegtoro fej, lasd a
 * 2.2.2 specifikaciobol PLAYERS5 kovetelmenyt).
 *
 * A "Jatek Inditasa" gomb megnyitja a GameWindow-t es bezarja a foemenut.
 */
public final class MainMenu extends JFrame {

    public MainMenu() {
        super("Zúzmaraváros");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(220, 235, 250));

        add(buildTitle(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JLabel buildTitle() {
        JLabel title = new JLabel("ZÚZMARAVÁROS", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 38));
        title.setForeground(new Color(35, 60, 110));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        return title;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Nehezsegi szint
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        diffPanel.setOpaque(false);
        diffPanel.add(new JLabel("Nehézség:"));
        final JComboBox<String> difficultyBox = new JComboBox<>(new String[] { "Könnyű", "Közepes", "Nehéz" });
        difficultyBox.setSelectedIndex(0);
        diffPanel.add(difficultyBox);
        center.add(diffPanel);

        center.add(Box.createVerticalStrut(20));

        // Kezdo fej valaszto
        JLabel headLabel = new JLabel("Hókotró kezdő feje:", SwingConstants.CENTER);
        headLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.PLAIN, 13f));
        center.add(headLabel);

        JPanel headPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        headPanel.setOpaque(false);
        ButtonGroup headGroup = new ButtonGroup();
        final JRadioButton soproRadio = new JRadioButton("Söprő fej", true);
        final JRadioButton jegtoroRadio = new JRadioButton("Jégtörő fej");
        soproRadio.setOpaque(false);
        jegtoroRadio.setOpaque(false);
        headGroup.add(soproRadio);
        headGroup.add(jegtoroRadio);
        headPanel.add(soproRadio);
        headPanel.add(jegtoroRadio);
        center.add(headPanel);

        center.add(Box.createVerticalStrut(40));

        // Inditas gomb
        JButton startBtn = new JButton("Játék Indítása");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(240, 50));
        center.add(startBtn);

        startBtn.addActionListener(e -> launchGame(difficultyBox.getSelectedIndex(),
                soproRadio.isSelected() ? FejTipus.SOPROFEJ : FejTipus.JEGTOROFEJ));

        return center;
    }

    private void launchGame(int difficultyIndex, FejTipus startingHead) {
        Difficulty difficulty;
        switch (difficultyIndex) {
            case 0: difficulty = Difficulty.EASY; break;
            case 2: difficulty = Difficulty.HARD; break;
            default: difficulty = Difficulty.MEDIUM;
        }
        GameWindow window = new GameWindow(difficulty, startingHead);
        window.setVisible(true);
        dispose();
    }
}
