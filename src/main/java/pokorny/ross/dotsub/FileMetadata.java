package pokorny.ross.dotsub;

import java.util.UUID;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

/**
 * Immutable domain class representing metadata about a particular file
 */
public class FileMetadata {
    private final UUID id = UUID.randomUUID();
    private final String title;
    private final String description;
    private final MediaType mediaType;
    private final Instant creationDate;

    public FileMetadata(
            String title,
            String description,
            MediaType mediaType,
            Instant creationDate) {
        if (title == null || description == null || mediaType == null || creationDate == null) {
            throw new NullPointerException("Null parameter passed to FileMetadata");
        }

        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.creationDate = creationDate;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public MediaType getMediaType() { return mediaType; }
    public Instant getCreationDate() { return creationDate; }
}
