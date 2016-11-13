-- SQL script to create the database
CREATE TABLE file_metadata (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY NOT NULL,
    title VARCHAR(256) NOT NULL,
    description VARCHAR(4000),
    media_type VARCHAR(256) NOT NULL,
    filename VARCHAR(256) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- For fast alphabetical sorting
CREATE INDEX title_idx ON file_metadata (title);
