-- SQL script to create the database
CREATE TABLE file_metadata (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    title VARCHAR(256),
    description VARCHAR(4000),
    media_type VARCHAR(256),
    creation_date TIMESTAMP
);

-- For fast alphabetical sorting
CREATE INDEX title_idx ON file_metadata (title);
