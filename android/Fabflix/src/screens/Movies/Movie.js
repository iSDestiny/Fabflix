import React, { Component } from 'react';
import { View, Text, TextInput, Button, StyleSheet } from 'react-native';
import { Navigation } from 'react-native-navigation';

class Movie extends Component {
	construct = (arr) => {
		combined = '';
		arr.forEach((item) => {
			combined += item + ', ';
		});
		return combined;
	};

	render() {
		return (
			<View>
				<Text style={styles.bold}>
					{this.props.movie.title} ({this.props.movie.year})
				</Text>
				<Text>Director: {this.props.movie.director}</Text>
				<Text>Rating: {this.props.movie.rating}</Text>
				<Text>Genre(s): {this.construct(this.props.movie.genres)}</Text>
				<Text>Stars: {this.construct(this.props.movie.stars)}</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	bold : {
		fontWeight : 'bold'
	}
});

export default Movie;
