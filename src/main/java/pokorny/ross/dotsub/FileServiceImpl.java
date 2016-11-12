package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.UUID;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.jooq.DSLContext;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;
import pokorny.ross.dotsub.jooq.tables.records.FileMetadataRecord;
import static pokorny.ross.dotsub.jooq.Tables.FILE_METADATA;

/**
 * "Database" for uploaded files consisting of basic in-memory data structures for
 * metadata storage and filesystem persistence for file data
 */
@Service
public class FileServiceImpl implements FileService {
    private final DSLContext jooq;
    private final NioWrapper nioWrapper;

    private final String filePersistenceDirectory;

    @Inject
    public FileServiceImpl(
            @Value("${filePersistenceDirectory}") String filePersistenceDirectory,
            DSLContext jooq,
            NioWrapper nioWrapper) {
        this.filePersistenceDirectory = filePersistenceDirectory;
        this.jooq = jooq;
        this.nioWrapper = nioWrapper;
    }

    private Path getPathById(UUID id) {
        return nioWrapper.get(filePersistenceDirectory, id.toString());
    }

    /**
     * @return metadata about every file in the system
     */
    public Collection<? extends IFileMetadata> list() {
        return jooq.selectFrom(FILE_METADATA).orderBy(FILE_METADATA.TITLE).fetch();
    }

    /**
     * Save a file.
     * @param metadata The file's metadata
     * @param file The contents of the file
     */
    public void save(IFileMetadata metadata, byte[] bytes) throws IOException {
        //save the metadata in the database
        FileMetadataRecord record = jooq.newRecord(FILE_METADATA, metadata);
        record.store();

        //get the UUID generated by the database
        UUID id = record.getId();

        //save the file itself on a path generated using the UUID
        Path filePath = getPathById(id);
        nioWrapper.write(filePath, bytes,
            StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
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
    public IFileMetadata getMetadataById(UUID id) {
        IFileMetadata metadata = jooq.selectFrom(FILE_METADATA)
            .where(FILE_METADATA.ID.equal(id))
            .fetchOne();

        if (metadata == null) {
            throw new IllegalArgumentException("No metadata found for id: " + id);
        }
        else {
            return metadata;
        }
    }
}
