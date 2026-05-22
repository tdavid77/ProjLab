package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Segédosztály a map konfigurációk fileból való beolvasásához.
 * A map fileok egyszerű szöveg alapú formátumot használnak.
 */
public class MapLoader {

    public static class MapConfig {
        public int mapWidth;
        public int mapHeight;
        public Map<String, NodeInfo> nodes = new HashMap<>();
        public Map<String, RoadInfo> roads = new HashMap<>();
        public Map<String, PlayerInfo> players = new HashMap<>();
        public Map<String, NpcInfo> npcs = new HashMap<>();
    }

    public static class NodeInfo {
        public String id;
        public String displayName;
        public int x;
        public int y;

        public NodeInfo(String id, String displayName, int x, int y) {
            this.id = id;
            this.displayName = displayName;
            this.x = x;
            this.y = y;
        }
    }

    public static class RoadInfo {
        public String name;
        public String nodeA;
        public String nodeB;
        public int length;
        public String type;  // NORMAL, HID
        public int lanes;

        public RoadInfo(String name, String nodeA, String nodeB, int length, String type, int lanes) {
            this.name = name;
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.length = length;
            this.type = type;
            this.lanes = lanes;
        }
    }

    public static class PlayerInfo {
        public String type;      // TAKARITO, BUSZOS
        public String name;
        public String startNode;

        public PlayerInfo(String type, String name, String startNode) {
            this.type = type;
            this.name = name;
            this.startNode = startNode;
        }
    }

    public static class NpcInfo {
        public String name;
        public String otthon;    // home node
        public String munkahely; // work node
        public String startNode; // starting node

        public NpcInfo(String name, String otthon, String munkahely, String startNode) {
            this.name = name;
            this.otthon = otthon;
            this.munkahely = munkahely;
            this.startNode = startNode;
        }
    }

    /**
     * Beolvassa a megadott .map fájlt az src/maps mappából.
     * Az aktuális munkakönyvtár a projekt gyökere (prototipusVegleges).
     */
    public static MapConfig loadMap(String mapName) {
        MapConfig config = new MapConfig();
        
        // Próbáljuk meg megtalálni a map fájlt különböző helyekről
        String[] possiblePaths = {
            "src/maps/" + mapName + ".map",
            "prototipusVegleges/src/maps/" + mapName + ".map",
            "maps/" + mapName + ".map",
            mapName + ".map"
        };
        
        File mapFile = null;
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                mapFile = f;
                break;
            }
        }
        
        if (mapFile == null) {
            throw new RuntimeException("Map file not found: " + mapName + ".map");
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(mapFile, StandardCharsets.UTF_8))) {

            String line;
            String section = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Üres sorok és megjegyzések kihagyása
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Szakasz headerek
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1).toUpperCase();
                    continue;
                }

                // Parse sorok a jelenlegi szakasz alapján
                if (section != null) {
                    parseConfigLine(config, section, line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error loading map config: " + e.getMessage(), e);
        }

        return config;
    }

    private static void parseConfigLine(MapConfig config, String section, String line) {
        String[] parts = line.split("\\|");

        switch (section) {
            case "MAPSIZE":
                if (parts.length >= 2) {
                    config.mapWidth = Integer.parseInt(parts[0].trim());
                    config.mapHeight = Integer.parseInt(parts[1].trim());
                }
                break;

            case "NODES":
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String displayName = parts[1].trim();
                    int x = Integer.parseInt(parts[2].trim());
                    int y = Integer.parseInt(parts[3].trim());
                    config.nodes.put(id, new NodeInfo(id, displayName, x, y));
                }
                break;

            case "ROADS":
                if (parts.length >= 6) {
                    String name = parts[0].trim();
                    String nodeA = parts[1].trim();
                    String nodeB = parts[2].trim();
                    int length = Integer.parseInt(parts[3].trim());
                    String type = parts[4].trim();
                    int lanes = Integer.parseInt(parts[5].trim());
                    config.roads.put(name, new RoadInfo(name, nodeA, nodeB, length, type, lanes));
                }
                break;

            case "PLAYERS":
                if (parts.length >= 3) {
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    String startNode = parts[2].trim();
                    config.players.put(name, new PlayerInfo(type, name, startNode));
                }
                break;

            case "NPCS":
                if (parts.length >= 4) {
                    String name = parts[0].trim();
                    String otthon = parts[1].trim();
                    String munkahely = parts[2].trim();
                    String startNode = parts[3].trim();
                    config.npcs.put(name, new NpcInfo(name, otthon, munkahely, startNode));
                }
                break;
        }
    }
}
