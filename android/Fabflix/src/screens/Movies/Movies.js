import React, { Component } from 'react';
import { View, StyleSheet } from 'react-native';
import { Navigation } from 'react-native-navigation';
import { connect } from 'react-redux';
import { addMovie, resetMovies, searchMovie, nextPage } from '../../store/actions/index';
import MovieList from '../../components/MovieList/MovieList';
import SearchMovie from '../../components/SearchMovie/SearchMovie';

class Movies extends Component {
	state = {
		page : 1
	};

	fetchMovies = () => {
		let title = this.props.currentMovie;
		let url = `http://fabflix.fun:8080/project1/api/movies?sort=titleasc&limit=10&page=1&title=${title}`;
		fetch(url)
			.then((response) => response.json())
			.then((responseJson) => {
				this.props.resetMovies();
				responseJson.forEach((movieData) => {
					let newStars = [];
					movieData['movie_stars'].forEach((info) => {
						newStars.push(info['star_name']);
					});
					let movieProps = {
						key      : movieData['movie_id'],
						rating   : movieData['movie_rating'],
						title    : movieData['movie_title'],
						year     : movieData['movie_year'],
						genres   : movieData['movie_genres'],
						director : movieData['movie_director'],
						stars    : newStars
					};
					this.props.onAddMovie(movieProps);
				});
			})
			.catch((e) => {
				alert('Error while fetching from server: ' + e);
			});
	};
	// 192.168.0.38:8080
	fetchMoreMovies = () => {
		let title = this.props.currentMovie;
		let nextPage = this.state.page + 1;
		this.setState({ page: nextPage });
		let url = `http://fabflix.fun:8080/project1/api/movies?sort=titleasc&limit=10&page=${nextPage}&title=${title}`;
		fetch(url)
			.then((response) => response.json())
			.then((responseJson) => {
				responseJson.forEach((movieData) => {
					let newStars = [];
					movieData['movie_stars'].forEach((info) => {
						newStars.push(info['star_name']);
					});
					let movieProps = {
						key      : movieData['movie_id'],
						rating   : movieData['movie_rating'],
						title    : movieData['movie_title'],
						year     : movieData['movie_year'],
						genres   : movieData['movie_genres'],
						director : movieData['movie_director'],
						stars    : newStars
					};
					this.props.onAddMovie(movieProps);
				});
			})
			.catch((e) => {
				alert('Error while fetching from server: ' + e);
			});
	};

	onNextPage = () => {
		this.props.onNextPage(this.props.page + 1);
		this.fetchMoreMovies();
	};

	onSearchHandler = () => {
		this.setState({ page: 1 });
		this.fetchMovies();
	};

	onChangeHandler = (val) => {
		this.props.onSearch(val);
	};

	onTouchHandler = (id) => {
		const movieObject = this.props.movies.find((movie) => {
			return movie.key === id;
		});

		Navigation.push(this.props.componentId, {
			component : {
				name      : 'fabflix.MovieScreen',
				passProps : {
					movie : movieObject
				},
				options   : {
					topBar : {
						title : {
							text : movieObject.title
						}
					}
				}
			}
		});
	};

	render() {
		return (
			<View>
				<SearchMovie
					title={this.props.currentMovie}
					onChangeHandler={this.onChangeHandler.bind(this)}
					onSearchHandler={this.onSearchHandler.bind(this)}
				/>
				<MovieList
					movies={this.props.movies}
					nextPageHandler={this.onNextPage.bind(this)}
					touchHandler={this.onTouchHandler.bind(this)}
				/>
			</View>
		);
	}
}

const mapStateToProps = (state) => {
	return {
		movies       : state.movies.movies,
		currentMovie : state.movies.currentMovie,
		page         : state.movies.page
	};
};

const mapDispatchToProps = (dispatch) => {
	return {
		onAddMovie  : (movieProps) => dispatch(addMovie(movieProps)),
		onSearch    : (title) => dispatch(searchMovie(title)),
		resetMovies : () => dispatch(resetMovies()),
		onNextPage  : (page) => dispatch(nextPage(page))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Movies);
