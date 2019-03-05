import React from 'react';
import { View, TextInput, Button, StyleSheet } from 'react-native';

const searchMovie = (props) => {
	return (
		<View style={styles.searchBarContainer}>
			<TextInput
				style={styles.searchBar}
				placeholder="Enter Movie Title"
				value={props.title}
				onChangeText={props.onChangeHandler}
			/>
			<Button style={styles.searchButton} title="Go" onPress={props.onSearchHandler} />
		</View>
	);
};

const styles = StyleSheet.create({
	searchBar          : {
		borderRadius      : 20,
		borderColor       : '#edeeef',
		borderWidth       : 1,
		paddingHorizontal : 20,
		backgroundColor   : 'white',
		width             : '80%'
	},

	searchButton       : {
		width           : '20%',
		borderRadius    : 10,
		marginLeft      : '10%',
		backgroundColor : 'red'
	},

	searchBarContainer : {
		flexDirection   : 'row',
		width           : '100%',
		alignSelf       : 'center',
		padding         : 10,
		marginBottom    : 3,
		flexGrow        : 1,
		justifyContent  : 'space-between',
		backgroundColor : '#edeeef'
	}
});

export default searchMovie;
