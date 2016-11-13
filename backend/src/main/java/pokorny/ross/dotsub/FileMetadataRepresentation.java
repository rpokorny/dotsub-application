package pokorny.ross.dotsub;

import java.lang.reflect.Method;

import java.util.UUID;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;
import pokorny.ross.dotsub.jooq.tables.pojos.FileMetadata;

/**
 * This class defines the JSON that is returned from the REST API
 * for File Metadata.  It has all of the fields of the jooq FileMetadata
 * class plus an href field holding a link to the file contents
 */
public class FileMetadataRepresentation extends FileMetadata {
    private static final long serialVersionUID = 19823458723l;

    private static final Class<FileResource> RESOURCE_CLASS = FileResource.class;
    private static final Method FILE_CONTENTS_METHOD;

    static {
        try {
            FILE_CONTENTS_METHOD = RESOURCE_CLASS.getMethod("getFileContents", UUID.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final URI href;

    public FileMetadataRepresentation(IFileMetadata metadata, UriInfo uriInfo) {
        super(
            metadata.getId(),
            metadata.getTitle(),
            metadata.getDescription(),
            metadata.getMediaType(),
            metadata.getFilename(),
            metadata.getCreationDate()
        );

        href = uriInfo.getBaseUriBuilder()
            .path(RESOURCE_CLASS)
            .path(FILE_CONTENTS_METHOD)
            .build(this.getId());
    }

    public URI getHref() { return href; }


}
