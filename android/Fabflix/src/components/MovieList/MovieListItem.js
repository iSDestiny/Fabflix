import React from 'react';
import { View, Text, StyleSheet, TouchableNativeFeedback } from 'react-native';

const construct = (arr) => {
	combined = '';
	arr.forEach((item) => {
		combined += item + ', ';
	});
	return combined;
};

const movieListItem = (props) => {
	return (
		<TouchableNativeFeedback onPress={props.touchHandler}>
			<View style={styles.listItem}>
				<Text style={styles.bold}>
					{props.title} ({props.year})
				</Text>
				<Text>Director: {props.director}</Text>
				<Text>Rating: {props.rating}</Text>
				<Text>Genre(s): {construct(props.genres)}</Text>
				<Text>Stars: {construct(props.stars)}</Text>
			</View>
		</TouchableNativeFeedback>
	);
};

const styles = StyleSheet.create({
	bold     : {
		fontWeight : 'bold'
	},

	listItem : {
		width           : '90%',
		padding         : 10,
		marginBottom    : 5,
		backgroundColor : '#eee',
		alignSelf       : 'center'
	}
});

export default movieListItem;
