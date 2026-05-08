package gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * GUI valtozat belepesi pontja.
 *
 * A meglevo SzkeletonProgram (motor csomag) a konzolos parancs-interpretert
 * indítja. Ezen osztály főfeladata: a Swing keretrendszer kepzetes szalan
 * (Event Dispatch Thread) elinditja a Zuzmaravaros foemenut
 */
public final class Main {
    private Main() { 
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // ha nem sikerul a system L&F, marad a default
        }
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
