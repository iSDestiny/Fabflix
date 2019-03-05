import { ADD_MOVIE, RESET_MOVIES, SEARCH_MOVIE, NEXT_PAGE } from '../actions/actionTypes';

const initialState = {
	currentMovie : null,
	page         : 1,
	movies       : []
};

const reducer = (state = initialState, action) => {
	switch (action.type) {
		case ADD_MOVIE:
			return {
				...state,
				movies : state.movies.concat({
					...action.movieProps
				})
			};
		case RESET_MOVIES:
			return {
				...state,
				page   : 1,
				movies : []
			};
		case SEARCH_MOVIE:
			return {
				...state,
				currentMovie : action.movieTitle
			};
		case NEXT_PAGE:
			return {
				...state,
				page : action.nextPage
			};
		default:
			return state;
	}
};

export default reducer;
