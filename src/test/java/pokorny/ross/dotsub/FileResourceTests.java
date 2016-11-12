package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.ArrayList;
import java.util.UUID;

import java.sql.Timestamp;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        ArrayList<IFileMetadata> collection = new ArrayList();
        collection.add(new FileMetadata(null, null, null, null, null));

        FileService mockService = mock(FileService.class);
        when(mockService.list()).thenAnswer(i -> collection);

        FileResource resource = new FileResource(mockService);

        Collection<IFileMetadata> retval = resource.listFiles();

        //check that the returned collection has the same elements as the
        //collection returned by the service
        assertEquals(retval.size(), 1);
        assertSame(retval.iterator().next(), collection.get(0));
    }

    @Test
    public void testUploadFile() throws IOException {
        byte[] deadbeef = {(byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF};
        String creationDateStr = "2016-11-12T00:31:50Z";
        Timestamp expectedTimestamp = new Timestamp(1478910710000l);
        String title = "test title";
        String description = "test description";
        MediaType mediaType = new MediaType("image", "png");

        FormDataBodyPart formDataMock = mock(FormDataBodyPart.class);
        when(formDataMock.getValueAs(byte[].class)).thenReturn(deadbeef);
        when(formDataMock.getMediaType()).thenReturn(mediaType);

        FileService mockService = mock(FileService.class);

        FileResource resource = new FileResource(mockService);

        IFileMetadata retval =
            resource.uploadFile(formDataMock, title, description, creationDateStr);

        assertEquals(retval.getTitle(), title);
        assertEquals(retval.getDescription(), description);
        assertEquals(retval.getMediaType(), "image/png");
        assertEquals(retval.getCreationDate(), expectedTimestamp);

        ArgumentCaptor<IFileMetadata> metadataCaptor =
            ArgumentCaptor.forClass(IFileMetadata.class);

        //verify that the save function is called once with the expected arguments
        verify(mockService)
            .save(metadataCaptor.capture(), eq(deadbeef));

        IFileMetadata captured = metadataCaptor.getValue();
        assertEquals(captured.getTitle(), title);
        assertEquals(captured.getDescription(), description);
        assertEquals(captured.getMediaType(), "image/png");
        assertEquals(captured.getCreationDate(), expectedTimestamp);
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

        FileResource resource = new FileResource(mockService);

        //should throw exception due to invalid date string
        resource.uploadFile(formDataMock, title, description, creationDateStr);
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
