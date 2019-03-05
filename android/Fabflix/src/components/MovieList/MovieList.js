import React from 'react';
import { FlatList, StyleSheet } from 'react-native';
import MovieListItem from './MovieListItem';

const movieList = (props) => {
	return (
		<FlatList
			data={props.movies}
			renderItem={(item) => (
				<MovieListItem
					title={item.item.title}
					year={item.item.year}
					director={item.item.director}
					rating={item.item.rating}
					genres={item.item.genres}
					stars={item.item.stars}
					touchHandler={() => props.touchHandler(item.item.key)}
				/>
			)}
			onEndReached={props.nextPageHandler}
			onEndReachedThreshold={0.1}
		/>
	);
};

export default movieList;
