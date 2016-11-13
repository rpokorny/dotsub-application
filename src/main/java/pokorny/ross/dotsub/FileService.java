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
    public Collection<? extends IFileMetadata> list();

    /**
     * Save a file.
     * @param metadata The file's metadata.  This metadata should not have an id set
     * @param file The contents of the file
     * @return a IFileMetadata containing the file's information including an id as
     * set by the database.
     */
    public IFileMetadata save(IFileMetadata metadata, byte[] file) throws IOException;


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
