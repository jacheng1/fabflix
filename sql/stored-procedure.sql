USE moviedb;
DROP procedure IF EXISTS add_movie;

DELIMITER $$
USE moviedb $$
CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100),
    IN movieYear INTEGER,
    IN movieDirector VARCHAR(100),
    IN starName VARCHAR(100),
    IN genreName VARCHAR(32))
    add_movie_block: BEGIN
        -- If movie exists, make no changes.
        IF ((SELECT COUNT(*) FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) > 0) THEN
            SELECT 'Error: Already exists. Movie not added.' as message;
            LEAVE add_movie_block;
        END IF;
        -- Generate and set movieID.
        SET @movieID = concat("tt",(select max(substring(id, 3)) from movies) + 1);
        INSERT INTO movies(id, title, year, director, price) VALUES (@movieID, movieTitle, movieYear, movieDirector, ROUND(RAND() * 9 + 1, 2));

-- genre
-- If genre does not exist, generate and set genreID.
        IF ((SELECT COUNT(*) FROM genres WHERE name = genreName) = 0) THEN
                    SET @genreID = (SELECT MAX(id) FROM genres) + 1;
        INSERT INTO genres(id, name) VALUES (@genreID, genreName);
        END IF;

        -- Link genre to movie.
        INSERT INTO genres_in_movies(genreID, movieID) VALUES ((SELECT id FROM genres WHERE name = genreName), @movieID);

-- star
        -- If star does not exist, generate and set starID.
        IF ((SELECT COUNT(*) FROM stars WHERE name = starName) = 0) THEN
                    SET @starID = CONCAT("nm",(select max(substring(id, 3)) from stars) + 1);
        INSERT INTO stars(id, name, birthYear) VALUES (@starID, starName, null);
        END IF;

        -- Link star to movie, and limit it to 1 for those with names identical to other another star.
        INSERT INTO stars_in_movies(starID, movieID) VALUES ((SELECT id FROM stars WHERE name = starName LIMIT 1), @movieID);

-- rating
        -- Add random rating for movie.
        INSERT INTO ratings(movieId, rating, numVotes)
        VALUES (@movieID, ROUND(RAND() * 10, 1), 0);

    SET @movieID = (SELECT id FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector LIMIT 1);
    SET @starID = (SELECT id FROM stars WHERE name = starName LIMIT 1);
    SET @genreID = (SELECT id FROM genres WHERE name = genreName LIMIT 1);
    SELECT concat('Success! Movie ID: ', @movieID, ', Star ID: ', @starID, ', Genre ID: ', @genreID) as message;
END
$$
DELIMITER ;