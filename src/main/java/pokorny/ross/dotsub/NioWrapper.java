package pokorny.ross.dotsub;

import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.OpenOption;

import org.springframework.stereotype.Component;

/**
 * A non-static wrapper around two of NIO's static methods that are used in
 * FileServiceImpl.  This wrapper allows FileServiceImpl to be more easily unit tested
 */
@Component
public class NioWrapper {
    public Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        return Files.write(path, bytes, options);
    }

    public Path get(String first, String... more) {
        return Paths.get(first, more);
    }
}
