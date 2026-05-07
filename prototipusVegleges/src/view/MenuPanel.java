package view;

import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private ZuzmaravarosApp mainApp;

    public MenuPanel(ZuzmaravarosApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new GridBagLayout()); // Hogy középen legyen minden
        setBackground(new Color(220, 240, 255)); // Kellemes havas/jeges háttérszín

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Cím
        JLabel titleLabel = new JLabel("ZÚZMARAVÁROS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nehézség
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setOpaque(false);
        difficultyPanel.add(new JLabel("Nehézség:"));
        String[] difficulties = {"Könnyű", "Közepes", "Nehéz"};
        JComboBox<String> difficultyBox = new JComboBox<>(difficulties);
        difficultyPanel.add(difficultyBox);

        // Játékos típus választó
        JPanel playerTypePanel = new JPanel();
        playerTypePanel.setOpaque(false);
        JRadioButton rbTakarito = new JRadioButton("Takarító Vállalat (Hókotrók)");
        JRadioButton rbBusz = new JRadioButton("Tömegközlekedés (Buszok)");
        rbTakarito.setSelected(true); // Alapértelmezett
        rbTakarito.setOpaque(false);
        rbBusz.setOpaque(false);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbTakarito);
        bg.add(rbBusz);
        playerTypePanel.add(rbTakarito);
        playerTypePanel.add(rbBusz);

        // Indítás gomb
        JButton startButton = new JButton("Játék Indítása");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            // Itt majd át kell adni a beállításokat a modellnek!
            // pl.: mainApp.initGame(difficultyBox.getSelectedIndex(), ...);
            mainApp.showScreen("GAME");
        });

        // Elemek összerakása kis térközökkel
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(difficultyPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(playerTypePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(startButton);

        add(formPanel);
    }
}