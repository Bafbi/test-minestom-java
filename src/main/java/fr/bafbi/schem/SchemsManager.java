package fr.bafbi.schem;

import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.SchematicReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SchemsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemsManager.class);

    private static final String SCHEM_FOLDER = "schems/";
    private static final Path schemFolder = Paths.get(SCHEM_FOLDER);

    public static Path getSchemFolder() {
        return schemFolder;
    }

    public static String[] getSchems() {
        return schemFolder.toFile().list();
    }

    public static Path getSchemPath(String path) {
        return schemFolder.resolve(path);
    }

    public static InputStream getSchemInputStream(String path) throws IOException {
        return getSchemPath(path).toUri().toURL().openStream();
    }

    public static Optional<Schematic> loadSchem(String path) {
        try (var is = getSchemInputStream(path)) {
            return Optional.of(new SchematicReader().read(is));
        } catch (Exception e) {
            LOGGER.warn("Failed to load schem: {}", path);
            return Optional.empty();
        }
    }

    public static Optional<Schematic> loadResourceSchem(String path) {
        try (var is = SchemsManager.class.getClassLoader().getResourceAsStream(schemFolder.resolve(path).toString())) {
            if (is == null) {
                LOGGER.warn("Schem resource {} could not be read", path);
                return Optional.empty();
            }
            return Optional.of(new SchematicReader().read(is));
        } catch (Exception e) {
            LOGGER.warn("Schem resource {} was not found", path);
            return Optional.empty();
        }
    }
}
