package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

/**
 * "Database" for uploaded files consisting of basic in-memory data structures for
 * metadata storage and filesystem persistence for file data
 */
@Repository
public class FileRepositoryImpl implements FileRepository {
    //where the metadata is stored
    private Map<UUID, FileMetadata> metadataMap = new ConcurrentHashMap();

    private final String filePersistenceDirectory;

    @Inject
    public FileRepositoryImpl(
            @Value("${filePersistenceDirectory}") String filePersistenceDirectory) {
        if (filePersistenceDirectory == null) {
            throw new NullPointerException("Missing configuration: filePersistenceDirectory");
        }

        this.filePersistenceDirectory = filePersistenceDirectory;
    }

    private Path getPathById(UUID id) {
        return Paths.get(filePersistenceDirectory, id.toString());
    }

    /**
     * @return metadata about every file in the system
     */
    public Collection<FileMetadata> list() {
        return Collections.unmodifiableCollection(metadataMap.values());
    }

    /**
     * Save a file.
     * @param metadata The file's metadata
     * @param file The contents of the file
     */
    public void save(FileMetadata metadata, byte[] bytes) throws IOException {
        UUID id = metadata.getId();

        //save the file first in case there's an error.
        //Use CREATE_NEW to ensure we don't somehow overwrite an existing file
        //(extremely unlikely considering random UUIDs, so I won't worry about
        //special error handling for that case)
        Path filePath = getPathById(id);
        Files.write(filePath, bytes,
            StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

        metadataMap.put(id, metadata);
    }


    /**
     * Gets a File object for a file with the referenced id
     * @param id The id of the file
     * @return a File
     * @throws IllegalArgumentException if the id does not match an existing file
     */
    public File getFileById(UUID id) {
        File file = getPathById(id).toFile();

        //checks for existance and readability
        if (!file.canRead()) {
            throw new IllegalArgumentException("No readable file found for id: " + id);
        }

        return file;
    }

    /**
     * Gets the metadata for a file with the referenced id
     * @param id The id of the file
     * @return metadata for the file
     * @throws IllegalArgumentException if the id does not match any existing metadata
     */
    public FileMetadata getMetadataById(UUID id) {
        FileMetadata metadata = metadataMap.get(id);

        if (metadata == null) {
            throw new IllegalArgumentException("No metadata found for id: " + id);
        }
        else {
            return metadata;
        }
    }
}
