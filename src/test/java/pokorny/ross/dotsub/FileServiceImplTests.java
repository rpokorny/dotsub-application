package pokorny.ross.dotsub;

import java.util.Collection;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Iterator;

import java.sql.Timestamp;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

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

/**
 * Unit tests for FileServiceImpl
 */
public class FileServiceImplTests {
    private static final String MOCK_DB_TEXT_NAME = "/MockDatabase.txt";

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

        //unfortunately the jooq mocking tools do not appear to support dates
        assertNull(metadata.getCreationDate());

        metadata = iterator.next();

        assertEquals(UUID.fromString("5a9b5011-3e2b-4155-be47-bdf1c03a0447"), metadata.getId());
        assertEquals("Test2Title", metadata.getTitle());
        assertNull(metadata.getDescription());
        assertEquals("application/json", metadata.getMediaType());
        assertNull(metadata.getCreationDate());
    }
}
