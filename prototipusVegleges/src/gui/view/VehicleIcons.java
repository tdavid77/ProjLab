package gui.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Betolti a jarmu-ikonokat (PNG fajlok) a `icons/` mappabol relativan a futtatasi konyvtarhoz.
 *
 * Hasznalat:
 *   - hokotro.png, auto.png, busz.png fajlokat a `prototipusVegleges/icons/` alá helyezd el
 *   - ha valamelyik fajl nem talalhato, a MapPanel a beepitett geometrikus alakzatra esik vissza
 *
 * A betoltes statikus inicializaciokor egyszer megtortenik (osszesen 3 fajlolvasas indulaskor).
 */
public final class VehicleIcons {

    private static final BufferedImage HOKOTRO = loadOptional("hokotro.png", "Hokotro.png");
    private static final BufferedImage AUTO    = loadOptional("auto.png", "Auto.png");
    private static final BufferedImage BUSZ    = loadOptional("busz.png", "Busz.png");

    private VehicleIcons() {
    }

    public static BufferedImage getHokotro() {
        return HOKOTRO;
    }

    public static BufferedImage getAuto() {
        return AUTO;
    }

    public static BufferedImage getBusz() {
        return BUSZ;
    }

    /**
     * Megprobalja betolteni a megadott nevű PNG-t. Tobb relatv eleresi utat es
     * fajlnev-varianst is kiprobal (kis es nagy kezdobetus), mert a CWD a
     * futtatasi modtol fuggoen valtozhat (run-gui.bat vs. VS Code), es Linuxon
     * a fajlnevek case-sensitive-ak.
     */
    private static BufferedImage loadOptional(String... filenames) {
        String[] dirs = {
                "icons/",
                "prototipusVegleges/icons/",
                "../icons/",
                "../prototipusVegleges/icons/",
        };
        for (String dir : dirs) {
            for (String filename : filenames) {
                File f = new File(dir + filename);
                if (f.exists() && f.isFile()) {
                    try {
                        return ImageIO.read(f);
                    } catch (IOException ignored) {
                        // probaljuk a kovetkezo path-ot
                    }
                }
            }
        }
        return null; // nincs fajl, a MapPanel fallback alakzatot rajzol
    }
}
