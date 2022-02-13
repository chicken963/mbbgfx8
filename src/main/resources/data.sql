DROP TABLE IF EXISTS tracks;
CREATE TABLE IF NOT EXISTS tracks(
    round_id uuid DEFAULT random_uuid(),
    artist VARCHAR(255) NOT NULL,
    song_title VARCHAR(255) NOT NULL,
    local_path VARCHAR(512) NOT NULL PRIMARY KEY
);

INSERT INTO tracks VALUES
(random_uuid(), 'Виноградный день', 'Дети!Учим алфавит!Буква А', 'Дети!Учим алфавит!Буква А.mp3'),
(random_uuid(), 'Виноградный день', 'Дети!Учим алфавит!Буква И', 'Дети!Учим алфавит!Буква И.mp3'),
(random_uuid(), 'Виноградный день', 'Дети!Учим алфавит!Буква О', 'Дети!Учим алфавит!Буква О.mp3'),
(random_uuid(), 'One republic', 'Right moves', 'All The Right Moves (2009).mp3'),
(random_uuid(), 'armin', 'falling', 'armin van buuren falling.mp3'),
(random_uuid(), 'black sabbath', 'Paranoid', 'Black_Sabbath_-_Paranoid.mp3');