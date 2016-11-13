package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.ArrayList;
import java.util.UUID;

import java.sql.Timestamp;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.*;
import static org.junit.Assert.*;

import org.mockito.*;
import static org.mockito.Mockito.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;
import pokorny.ross.dotsub.jooq.tables.pojos.FileMetadata;

/**
 * Unit tests for FileResource
 */
public class FileResourceTests {
    @Test
    public void testListFiles() {
        UUID id = UUID.fromString("e3ca88f1-4d11-40d4-8d07-0676680588d6");

        ArrayList<IFileMetadata> collection = new ArrayList<IFileMetadata>();
        collection.add(new FileMetadata(id, "title", "desc", "text/plain", new Timestamp(0)));

        FileService mockService = mock(FileService.class);
        when(mockService.list()).thenAnswer(i -> collection);

        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getBaseUriBuilder())
            .thenReturn(UriBuilder.fromPath("https://test-domain:8000/asdf/test"));

        FileResource resource = new FileResource(mockService);

        Collection<FileMetadataRepresentation> retval = resource.listFiles(mockUriInfo);

        //check that the returned collection has the same elements as the
        //collection returned by the service
        assertEquals(retval.size(), 1);

        FileMetadataRepresentation rep = retval.iterator().next();

        assertEquals(id, rep.getId());
        assertEquals("title", rep.getTitle());
        assertEquals("desc", rep.getDescription());
        assertEquals("text/plain", rep.getMediaType());
        assertEquals(new Timestamp(0), rep.getCreationDate());
        assertEquals(
            "https://test-domain:8000/asdf/test/file/e3ca88f1-4d11-40d4-8d07-0676680588d6",
            rep.getHref().toString()
        );
    }

    @Test
    public void testUploadFile() throws IOException {
        byte[] deadbeef = {(byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF};
        String creationDateStr = "2016-11-12T00:31:50Z";
        Timestamp expectedTimestamp = new Timestamp(1478910710000l);
        String title = "test title";
        String description = "test description";
        MediaType mediaType = new MediaType("image", "png");
        UUID id = UUID.fromString("ca51227c-695d-4564-a8af-34f6e01d28c8");

        FormDataBodyPart formDataMock = mock(FormDataBodyPart.class);
        when(formDataMock.getValueAs(byte[].class)).thenReturn(deadbeef);
        when(formDataMock.getMediaType()).thenReturn(mediaType);

        FileService mockService = mock(FileService.class);

        when(mockService.save(any(FileMetadata.class), any(byte[].class))).thenReturn(
                new FileMetadata(id, title, description, "image/png", expectedTimestamp));

        UriInfo mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getBaseUriBuilder())
            .thenReturn(UriBuilder.fromPath("http://test-domain/test"));


        FileResource resource = new FileResource(mockService);

        FileMetadataRepresentation retval =
            resource.uploadFile(mockUriInfo, formDataMock, title, description, creationDateStr);

        assertEquals(title, retval.getTitle());
        assertEquals(description, retval.getDescription());
        assertEquals("image/png", retval.getMediaType());
        assertEquals(expectedTimestamp, retval.getCreationDate());
        assertEquals("http://test-domain/test/file/ca51227c-695d-4564-a8af-34f6e01d28c8",
                retval.getHref().toString());

        ArgumentCaptor<IFileMetadata> metadataCaptor =
            ArgumentCaptor.forClass(IFileMetadata.class);

        //verify that the save function is called once with the expected arguments
        verify(mockService)
            .save(metadataCaptor.capture(), eq(deadbeef));

        IFileMetadata captured = metadataCaptor.getValue();

        assertEquals(title, captured.getTitle());
        assertEquals(description, captured.getDescription());
        assertEquals("image/png", captured.getMediaType());
        assertEquals(expectedTimestamp, captured.getCreationDate());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUploadFileInvalidDate() throws IOException {
        byte[] deadbeef = {(byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF};
        String creationDateStr = "2016-11-asdf12T00:31:50Z";
        String title = "test title";
        String description = "test description";
        MediaType mediaType = new MediaType("image", "png");

        FormDataBodyPart formDataMock = mock(FormDataBodyPart.class);
        when(formDataMock.getValueAs(byte[].class)).thenReturn(deadbeef);
        when(formDataMock.getMediaType()).thenReturn(mediaType);

        FileService mockService = mock(FileService.class);
        UriInfo mockUriInfo = mock(UriInfo.class);

        FileResource resource = new FileResource(mockService);

        //should throw exception due to invalid date string
        resource.uploadFile(mockUriInfo, formDataMock, title, description, creationDateStr);
    }

    @Test
    public void testGetFileContents() {
        UUID id = UUID.fromString("d7b5dd3e-55f7-4553-8fb0-3da252179255");
        FileMetadata metadata =
            new FileMetadata(id, "title", "desc", "text/csv", new Timestamp(1478910710000l));

        File mockFile = mock(File.class);

        FileService mockService = mock(FileService.class);

        when(mockService.getMetadataById(id)).thenReturn(metadata);
        when(mockService.getFileById(id)).thenReturn(mockFile);

        FileResource resource = new FileResource(mockService);
        Response retval = resource.getFileContents(id);

        assertSame(retval.getEntity(), mockFile);
        assertEquals(retval.getMediaType(), new MediaType("text", "csv"));
    }
}
