ALTER TABLE moviedb.movies
ADD COLUMN price FLOAT NOT NULL;

UPDATE moviedb.movies
SET price = ROUND(5.00 + (RAND() * (20.00 - 5.00)), 2);