package view;

import javax.swing.*;
import java.awt.*;

public class ZuzmaravarosApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    
    // Itt tárolhatod majd a GameEngine referenciáját, pl:
    // private GameEngine engine;

    public ZuzmaravarosApp() {
        setTitle("Zúzmaraváros - Hóeltakarító Szimulátor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // Induló felbontás
        setLocationRelativeTo(null); // Középre igazítás

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Képernyők inicializálása
        MenuPanel menuPanel = new MenuPanel(this);
        GamePanel gamePanel = new GamePanel(this);

        // Képernyők hozzáadása a CardLayout-hoz
        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(gamePanel, "GAME");

        add(mainContainer);
    }

    // Függvény, amivel panelt válthatunk
    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public static void main(String[] args) {
        // Swing felület indítása a megfelelő szálon
        SwingUtilities.invokeLater(() -> {
            ZuzmaravarosApp app = new ZuzmaravarosApp();
            app.setVisible(true);
        });
    }
}