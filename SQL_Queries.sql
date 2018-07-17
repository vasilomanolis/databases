-- CREATE TABLES

-- Create artists table
CREATE TABLE IF NOT EXISTS artists (
  id   INT PRIMARY KEY NOT NULL,
  name TEXT            NOT NULL
);

-- Create albums table
CREATE TABLE IF NOT EXISTS albums (
  id         INT PRIMARY KEY              NOT NULL,
  name       TEXT                         NOT NULL,
  artists_id INT REFERENCES artists (id)  NOT NULL
);

-- Create songs table
CREATE TABLE IF NOT EXISTS songs (
  id         INT PRIMARY KEY             NOT NULL,
  name       TEXT                        NOT NULL,
  artists_id INT REFERENCES artists (id) NOT NULL,
  albums_id  INT REFERENCES albums (id)  NOT NULL
);

-- Create songstags table
CREATE TABLE IF NOT EXISTS songstags (
  songs_id INT REFERENCES songs (id),
  tags     TEXT NOT NULL
);

--How many albums are listed?
SELECT count(id)
FROM albums;
--Answer: 492

--How many albums are funk rock ones?
SELECT count(DISTINCT albums_id) FROM songs WHERE id IN (
SELECT songs_id
           FROM songstags
WHERE tags = 'funk rock');
--Answer: 24

--List, in alphabetical order, all the artists who have tracks regarded as 'new wave'

SELECT name FROM artists WHERE id IN(
SELECT artists_id FROM songs WHERE id IN (
SELECT songs_id
           FROM songstags
WHERE tags = 'new wave')) ORDER BY name ASC;
-- Answer
/*
BEASTIE BOYS
BLONDIE
CLASH
COLDPLAY
CURE
DEBORAH HARRY
DEEP PURPLE
DEPECHE MODE
EURYTHMICS
FRANZ FERDINAND
JACKSON FIVE
JAM
JEFF BUCKLEY
LOU REED
NEW ORDER
NIRVANA
PATTI SMITH
PIXIES
POLICE
RADIOHEAD
R.E.M.
VELVET UNDERGROUND
VERVE
WEEZER
YEAH YEAH YEAHS */


--What is the numerical difference between songs with "love" in the title and
-- those that are tagged with "love" that don't have it in the title?

SELECT (
  (SELECT COUNT(DISTINCT songs.id) FROM songs WHERE name
          LIKE '% LOVE' OR name LIKE '% LOVE %' OR name LIKE 'LOVE %'
                                        OR name LIKE '% LOVE/%' OR name LIKE 'LOVE/%')
-
(SELECT COUNT(DISTINCT songs.id) FROM songs FULL JOIN songstags ON id=songs_id WHERE tags = 'love'
AND name NOT LIKE '% LOVE' AND name NOT LIKE '% LOVE %' AND name NOT LIKE 'LOVE %'
                    AND name NOT LIKE '% LOVE/%' AND name NOT LIKE 'LOVE/%'));


-- Songs with "love" in the title: 93
-- Songs tagged with 'love' but the name don't include "LOVE" 151
-- Answer: -58
-- Note 1:  When calculating the number of Songs with "LOVE" in the title, only
-- songs that had the exact word "LOVE" was taken into consideration. For example, the song
-- "LOVESTONED" was considered that it did NOT contain the word "LOVE".
-- Note 2: When calculating the number of Songs with 'LOVE' in the title
-- the song 'SUMMER LOVE/SET THE MOOD (PRELUDE)' was considered as containing the
-- word 'LOVE'
-- The criteria '% LOVE', '% LOVE %', 'LOVE %' ensure that the query will work if the word 'LOVE' is
-- at the beginning, middle or at the end end of the song title.


--How many albums have at least one song with 'dog' in the title?
SELECT COUNT (DISTINCT albums_id) FROM songs WHERE name
          LIKE '% DOG' OR name LIKE '% DOG %' OR name LIKE 'DOG %';
--Answer: 5

--  Are there more rhythmic songs than playful ones?
SELECT (
  (SELECT COUNT(DISTINCT songs_id) FROM songstags WHERE tags ='rhythmic')
   - (SELECT COUNT(DISTINCT songs_id) FROM songstags WHERE tags ='playful'));
-- Rhythmic: 871
-- Playful: 43
-- Answer: Yes

