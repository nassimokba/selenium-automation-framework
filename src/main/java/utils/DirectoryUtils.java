package utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryUtils {

    public static Path createDirectory(String name, Path path) {
        try {
            return Files.createDirectories(path.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Could not create directory %s", path.resolve(name)), e);
        }
    }

    public static boolean directoryExists(Path path) {
        return Files.exists(path);
    }
}
