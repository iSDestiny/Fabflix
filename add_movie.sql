use moviedb;
DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie(
	IN m_id varchar(10),
	IN m_title varchar(100), 
	IN m_year INTEGER, 
	IN m_director varchar(100), 
	IN g_name varchar(32), 
    IN s_id varchar(10),
	IN s_name varchar(100), 
	IN s_dob INTEGER
)
BEGIN
	SET @success = false;
    SET @movieExists = true;
    SET @starExists = true;
    SET @genreExists = true;
    
	IF ( SELECT NOT EXISTS (SELECT * FROM movies m WHERE m.title = m_title 
		AND m.year = m_year AND m.director = m_director) )  
	THEN
		INSERT INTO movies (id, title, year, director) VALUES (m_id, m_title, m_year, m_director);
        SET @movieExists = false;
        SET @success = true;
	END IF;
    
    IF (NOT @movieExists) THEN
		IF ( SELECT NOT EXISTS ( SELECT * FROM genres WHERE name = g_name ) ) 
		THEN
			INSERT INTO genres (name) VALUES (g_name);
            SET @g_id = (SELECT id FROM genres WHERE name = g_name LIMIT 1);
            INSERT INTO genres_in_movies (genreId, movieId) VALUES (@g_id, m_id);
			SET @genreExists = false;
		ELSE 
			SET @g_id = (SELECT id FROM genres WHERE name = g_name LIMIT 1);
			INSERT INTO genres_in_movies (genreId, movieId) VALUES (@g_id, m_id);
		END IF;
		
		IF ( SELECT NOT EXISTS ( SELECT * FROM stars WHERE name = s_name ) ) 
		THEN
			INSERT INTO stars (id, name, birthYear) VALUES (s_id, s_name, s_dob);
            INSERT INTO stars_in_movies (starId, movieId) VALUES (s_id, m_id);
			SET @starExists = false;
		ELSE
			SET @existing_s_id = (SELECT id FROM stars WHERE name = s_name LIMIT 1);
			INSERT INTO stars_in_movies (starId, movieId) VALUES (@existing_s_id, m_id);
		END IF;
        
        INSERT INTO ratings (movieId, rating, numVotes) VALUES (m_id, 0, 0);
	END IF;
	
    SELECT @success AS success, @starExists AS starExists, @genreExists as genreExists;
END $$
DELIMITER ;

-- Example call of procedure
-- CALL add_movie('tt0499470', 'exampletitle', 2999, 'adirector', 'agenre', 'tt0499479', 'a star', '2001');