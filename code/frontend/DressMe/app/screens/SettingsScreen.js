import React, { Component, PropTypes } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Navigator,
  Image
} from 'react-native';

class SettingsScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password', id: this.props.id, token: this.props.token}
    }
    render() {
        return (<Image source={require('../../img/background/bg-blue.png')} style = {styles.backgroundImage}>
            <View style={styles.title}>
				{this.backButton()}
				<Text style={styles.titleText}>
					S e t t i n g s
				</Text>
				{this.invisBlock()}
			</View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleClosetPress()}
                  style={styles.loginButton}>
                    <Text>
                      Closet
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleLaundryPress()}
                  style={styles.loginButton}>
                    <Text>
                      Laundry
                    </Text>
                </TouchableHighlight>
            </View>

            <View style={styles.backButtonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleLogOutPress()}
                  style={styles.loginButton}>
                    <Text>
                      Logout
                    </Text>
                </TouchableHighlight>
            </View>
        </Image>
        );
    }

    handleLaundryPress() {
        this.props.navigator.push({
            ident: "Laundry",
            id: this.state.id,
            token: this.state.token
        })
    }

    handleClosetPress() {
        this.props.navigator.push({
            ident: "Closet",
            id: this.state.id,
            token: this.state.token
        })
    }

    handleLogOutPress() {
        this.props.navigator.push({
            ident: "Login"
        })
    }

    handleBackPress() {
        {/*this.props.navigator.pop()*/}
        this.props.navigator.push({
            ident: "Recommendation",
            id: this.state.id,
            token: this.state.token
        })
    }

    backButton() {
		return <TouchableHighlight
		underlayColor='transparent'
		onPress={() => this.handleBackPress()}
		style={styles.backButton}
		>
			<Image source={require('../../img/icon/left-arrow.png')} style = {styles.backImage} />

		</TouchableHighlight>
	}

    invisBlock() {
		return <TouchableHighlight
		style={styles.invisBlock}
		>
			<Text style={styles.updateButtonText}>
			INVIS
			</Text>

		</TouchableHighlight>
	}
}

{/*}
LoginScreen.propTypes = {
    title: PropTypes.string.isRequired,
    onForward: PropTypes.func.isRequired,
    onBack: PropTypes.func.isRequired
};
*/}

var styles = StyleSheet.create({
    backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        justifyContent: 'center',
        alignItems: 'center'
        //resizeMode: 'cover', // or 'stretch'
    },
    container: {
        flex: 1,
        backgroundColor: 'blue'
    },
    titleContainer: {
        flex: 1,
        backgroundColor: 'yellow',
        justifyContent: 'center',
        alignItems: 'center'
    },
    loginContainer: {
        flex: 1,
        backgroundColor: 'red',
        justifyContent: 'center',
        alignItems: 'center'
    },
    loginButton: {
        width: 100,
        height: 50,
        borderWidth: 2,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'red'
    },
    settingsTitle: {
        flex: 2,
        backgroundColor: 'white',
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'column'
    },
    buttonContainer: {
        flex: 13,
        backgroundColor: 'white',
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'row'
    },
    backButtonContainer: {
        flex: 13,
        backgroundColor: 'white',
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'column'
    },
    buttonWrapper: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center'
    },
    title: {
    paddingTop: 20,
    paddingBottom: 20,
    flexDirection: 'row',
	},
	titleText: {
		flex: 1,
		fontSize: 36,
		fontWeight: '700',
		color: '#FFFFFF',
		textAlign:'center',
	},
    backImage: {
		height: 30,
		width: 30,
		opacity: 0.7,
		tintColor: '#FFFFFF'
    },
	backButton: {
		width: 70,
		height: 30,
		justifyContent: 'center',
		alignItems: 'center',
		paddingLeft: 5,
		paddingTop: 15
	},
	invisBlock: {
		width: 70,
		height: 30,
		opacity: 0
	}
})

module.exports = SettingsScreen;