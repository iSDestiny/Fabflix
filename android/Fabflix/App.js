import { Navigation } from 'react-native-navigation';
import { Provider } from 'react-redux';

import AuthScreen from './src/screens/Auth/Auth';
import MoviesScreen from './src/screens/Movies/Movies';
import MovieScreen from './src/screens/Movies/Movie';
import configureStore from './src/store/store';

const store = configureStore();

// Register Screens
Navigation.registerComponentWithRedux('fabflix.AuthScreen', () => AuthScreen, Provider, store);
Navigation.registerComponentWithRedux('fabflix.MoviesScreen', () => MoviesScreen, Provider, store);
Navigation.registerComponentWithRedux('fabflix.MovieScreen', () => MovieScreen, Provider, store);

// Start an App
Navigation.setRoot({
	root : {
		stack : {
			children : [
				{
					component : {
						name      : 'fabflix.AuthScreen',
						passProps : {
							text : 'stack with one child'
						}
					}
				}
			],

			options  : {
				topBar : {
					title : {
						text : 'Login'
					}
				}
			}
		}
	}
});
