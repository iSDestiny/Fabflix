import { ADD_MOVIE, RESET_MOVIES, SEARCH_MOVIE, NEXT_PAGE } from './actionTypes';

export const addMovie = (properties) => {
	return {
		type       : ADD_MOVIE,
		movieProps : properties
	};
};

export const resetMovies = () => {
	return {
		type : RESET_MOVIES
	};
};

export const searchMovie = (title) => {
	return {
		type       : SEARCH_MOVIE,
		movieTitle : title
	};
};

export const nextPage = (nextPage) => {
	return {
		type     : NEXT_PAGE,
		nextPage : nextPage
	};
};
