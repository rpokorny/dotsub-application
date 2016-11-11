package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.UUID;

import java.io.File;
import java.io.IOException;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;

public interface FileService {
    /**
     * @return metadata about every file in the system
     */
    public Collection<IFileMetadata> list();

    /**
     * Save a file.
     * @param metadata The file's metadata
     * @param file The contents of the file
     */
    public void save(IFileMetadata metadata, byte[] file) throws IOException;


    /**
     * Gets a File object for a file with the referenced id
     * @param id The id of the file
     * @return a File
     * @throws IllegalArgumentException if the id does not match an existing file
     */
    public File getFileById(UUID id);

    /**
     * Gets the metadata for a file with the referenced id
     * @param id The id of the file
     * @return metadata for the file
     * @throws IllegalArgumentException if the id does not match any existing metadata
     */
    public IFileMetadata getMetadataById(UUID id);
}
