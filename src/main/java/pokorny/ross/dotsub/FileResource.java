package pokorny.ross.dotsub;

import java.util.Collections;
import java.util.Collection;
import java.util.UUID;

import java.io.File;
import java.io.IOException;

import java.time.Instant;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

import java.sql.Timestamp;

import javax.inject.Inject;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;
import pokorny.ross.dotsub.jooq.tables.pojos.FileMetadata;

/**
 * This class defines the REST endpoints for the application.
 */
@Component
@Path("/file")
@Produces("application/json")
public class FileResource {

    private final FileService service;

    @Inject
    public FileResource(FileService service) {
        this.service = service;
    }

    @GET
    public Collection<IFileMetadata> listFiles() {
        return Collections.unmodifiableCollection(service.list());
    }

    @POST
    @Consumes("multipart/form-data")
    public IFileMetadata uploadFile(
            @FormDataParam("file") FormDataBodyPart fileData,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("creationDate") String isoDateStr) throws IOException {
        byte[] file = fileData.getValueAs(byte[].class);
        MediaType mediaType = fileData.getMediaType();
        Instant creationDate;

        try {
            creationDate = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(isoDateStr));
        }
        catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid ISO Date: " + isoDateStr, e);
        }

        IFileMetadata fileMetadata = new FileMetadata(
                null, //no id yet; database will assign
                title,
                description,
                mediaType.toString(),
                Timestamp.from(creationDate));

        service.save(fileMetadata, file);

        return fileMetadata;
    }

    /**
     * GET the contents of the file.  This method simply creates and returns a File
     * object and lets Jersey worry about the most efficient way to send the data to the client
     */
    @GET
    @Path("/{uuid}")
    public Response getFileContents(@PathParam("uuid") UUID uuid) {
        //will throw IllegalArgumentException if not found
        IFileMetadata metadata = service.getMetadataById(uuid);
        File file = service.getFileById(uuid);

        return Response.ok(file, metadata.getMediaType()).build();
    }
}
