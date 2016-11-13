package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.sql.Timestamp;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import org.junit.*;
import static org.junit.Assert.*;

import org.mockito.*;
import static org.mockito.Mockito.*;

import org.jooq.impl.DSL;
import org.jooq.DSLContext;
import org.jooq.tools.jdbc.MockFileDatabase;
import org.jooq.tools.jdbc.MockConnection;

import pokorny.ross.dotsub.jooq.tables.interfaces.IFileMetadata;
import pokorny.ross.dotsub.jooq.tables.pojos.FileMetadata;
import pokorny.ross.dotsub.jooq.tables.records.FileMetadataRecord;
import static pokorny.ross.dotsub.jooq.Tables.FILE_METADATA;

/**
 * Unit tests for FileServiceImpl
 */
public class FileServiceImplTests {
    private static final String MOCK_DB_TEXT_NAME = "/MockDatabase.txt";
    private static final String MOCK_EMPTY_DB_TEXT_NAME = "/MockEmptyDatabase.txt";

    @Test
    public void testList() throws IOException {
        InputStream mockDatabaseTextInput =
            this.getClass().getResourceAsStream(MOCK_DB_TEXT_NAME);

        DSLContext mockJooqContext =
            DSL.using(new MockConnection(new MockFileDatabase(mockDatabaseTextInput)));

        FileService service = new FileServiceImpl(null, mockJooqContext, null);

        Collection<? extends IFileMetadata> retval = service.list();

        assertEquals(retval.size(), 2);

        Iterator<? extends IFileMetadata> iterator = retval.iterator();

        IFileMetadata metadata = iterator.next();

        assertEquals(UUID.fromString("ed6f7aae-4714-45ca-981f-938ff42718f6"), metadata.getId());
        assertEquals("Test Title", metadata.getTitle());
        assertEquals("descrsrsrsr", metadata.getDescription());
        assertEquals("text/csv", metadata.getMediaType());
        assertEquals("test.csv", metadata.getFilename());

        //unfortunately the jooq mocking tools do not appear to support dates
        assertNull(metadata.getCreationDate());

        metadata = iterator.next();

        assertEquals(UUID.fromString("5a9b5011-3e2b-4155-be47-bdf1c03a0447"), metadata.getId());
        assertEquals("Test2Title", metadata.getTitle());
        assertNull(metadata.getDescription());
        assertEquals("application/json", metadata.getMediaType());
        assertEquals("test.json", metadata.getFilename());
        assertNull(metadata.getCreationDate());
    }

    @Test
    public void testSave() throws IOException {
        String filePersistenceDirectory = "/dir/to/persist/files";

        IFileMetadata metadata = new FileMetadata(
            null,
            "title",
            "description",
            "text/plain",
            "bio.txt",
            null);

        //file contents
        byte[] deadbeef = {(byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF};

        //id created by the "database"
        String generatedUUIDString = "a25a0408-3526-4de8-82ee-7a74be2c3314";
        UUID generatedId = UUID.fromString(generatedUUIDString);

        //mock NIO
        NioWrapper mockNioWrapper = mock(NioWrapper.class);
        when(mockNioWrapper.getPath(filePersistenceDirectory, generatedUUIDString))
            .thenReturn(
                Paths.get("/dir/to/persist/files/a25a0408-3526-4de8-82ee-7a74be2c3314")
            );

        ArgumentCaptor<OpenOption[]> openOptionsCaptor =
            ArgumentCaptor.forClass(OpenOption[].class);

        //mocking JOOQ
        InputStream mockDatabaseTextInput =
            this.getClass().getResourceAsStream(MOCK_DB_TEXT_NAME);

        DSLContext mockJooq = spy(
            DSL.using(new MockConnection(new MockFileDatabase(mockDatabaseTextInput))));

        //mocking the record is really tricky since it has final methods
        doAnswer(inv -> {
            FileMetadataRecord record = spy((FileMetadataRecord)inv.callRealMethod());

            doReturn(generatedId).when(record).getId();

            return record;
        }).when(mockJooq).newRecord(FILE_METADATA, metadata);

        FileService service =
            new FileServiceImpl(filePersistenceDirectory, mockJooq, mockNioWrapper);

        IFileMetadata retval = service.save(metadata, deadbeef);

        verify(mockNioWrapper).write(
            eq(Paths.get("/dir/to/persist/files/a25a0408-3526-4de8-82ee-7a74be2c3314")),
            eq(deadbeef),
            openOptionsCaptor.capture());

        assertTrue(openOptionsCaptor.getAllValues().contains(StandardOpenOption.WRITE));

        assertEquals(generatedId, retval.getId());
        assertEquals("title", retval.getTitle());
        assertEquals("description", retval.getDescription());
        assertEquals("text/plain", retval.getMediaType());
        assertEquals("bio.txt", retval.getFilename());
    }

    @Test
    public void testGetFileById() {
        String uuidStr = "7cf40ee6-d509-490b-a25a-6a16f304ad5e";
        UUID id = UUID.fromString(uuidStr);
        String filePersistenceDirectory = "/dir/to/persist/files";
        String expectedPath = "/dir/to/persist/files/7cf40ee6-d509-490b-a25a-6a16f304ad5e";

        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);
        when(mockFile.canRead()).thenReturn(true);
        when(mockPath.toFile()).thenReturn(mockFile);

        NioWrapper mockNioWrapper = mock(NioWrapper.class);
        when(mockNioWrapper.getPath(filePersistenceDirectory, uuidStr))
            .thenReturn(mockPath);

        FileService service =
            new FileServiceImpl(filePersistenceDirectory, null, mockNioWrapper);

        File retval = service.getFileById(id);

        assertSame(retval, mockFile);
    }

    @Test(expected=NoSuchElementException.class)
    public void testGetFileByIdCannotRead() {
        String uuidStr = "7cf40ee6-d509-490b-a25a-6a16f304ad5e";
        UUID id = UUID.fromString(uuidStr);
        String filePersistenceDirectory = "/dir/to/persist/files";
        String expectedPath = "/dir/to/persist/files/7cf40ee6-d509-490b-a25a-6a16f304ad5e";

        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);
        when(mockFile.canRead()).thenReturn(false);
        when(mockPath.toFile()).thenReturn(mockFile);

        NioWrapper mockNioWrapper = mock(NioWrapper.class);
        when(mockNioWrapper.getPath(filePersistenceDirectory, uuidStr))
            .thenReturn(mockPath);

        FileService service =
            new FileServiceImpl(filePersistenceDirectory, null, mockNioWrapper);

        service.getFileById(id);
    }

    @Test
    public void testGetMetadataById() throws IOException {
        UUID id = UUID.fromString("bd257178-7827-476a-91fb-e1ab1e8400f0");

        InputStream mockDatabaseTextInput =
            this.getClass().getResourceAsStream(MOCK_DB_TEXT_NAME);

        DSLContext mockJooq = spy(
            DSL.using(new MockConnection(new MockFileDatabase(mockDatabaseTextInput))));

        FileService service = new FileServiceImpl(null, mockJooq, null);

        IFileMetadata retval = service.getMetadataById(id);

        assertEquals(id, retval.getId());
        assertEquals("titletitle", retval.getTitle());
        assertEquals("description", retval.getDescription());
        assertEquals("application/xhtml+xml", retval.getMediaType());
        assertEquals("test.xhtml", retval.getFilename());
    }

    @Test(expected=NoSuchElementException.class)
    public void testGetMetadataByIdNoMatch() throws IOException {
        UUID id = UUID.fromString("bd257178-7827-476a-91fb-e1ab1e8400f0");

        InputStream mockDatabaseTextInput =
            this.getClass().getResourceAsStream(MOCK_EMPTY_DB_TEXT_NAME);

        DSLContext mockJooq = spy(
            DSL.using(new MockConnection(new MockFileDatabase(mockDatabaseTextInput))));

        FileService service = new FileServiceImpl(null, mockJooq, null);

        IFileMetadata retval = service.getMetadataById(id);
    }
}
