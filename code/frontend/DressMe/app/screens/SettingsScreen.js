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
                  onPress={() => this.handleClosetPress()}
                  style={styles.optionButton}>
                    <View style={styles.optionButtonView}>
                        <Image source={require('../../img/icon/hanger-thin.png')} style = {styles.optionButtonImage} />
                        <Text style={styles.optionButtonText}>
                        Closet
                        </Text>
                    </View>
                </TouchableHighlight>
                <TouchableHighlight
                  onPress={() => this.handleLaundryPress()}
                  style={styles.optionButton}>
                  <View style={styles.optionButtonView}>
                    <Image source={require('../../img/icon/laundry.png')} style = {styles.optionButtonImage} />
                    <Text style={styles.optionButtonText}>
                      Laundry
                    </Text>
                </View>
                </TouchableHighlight>
            </View>

            <View style={styles.backButtonContainer}>
                <TouchableHighlight
                  onPress={() => this.handleLogOutPress()}
                  style={styles.logoutButton}>
                    <Text style={styles.optionButtonText}>
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

var styles = StyleSheet.create({
    backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        justifyContent: 'center',
        alignItems: 'center'
    },
    container: {
        flex: 1,
        backgroundColor: 'blue'
    },
    optionButton: {
        width: 100,
        height: 80,
        justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: 'black',
		shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
		borderWidth: 2,
		borderColor: '#FFFFFF',
        marginRight: 50,
        marginLeft: 50
    },
    optionButtonText: {
        fontSize: 14,
        fontWeight: '600',
		color: '#FFFFFF',
		backgroundColor: '#000000',
    },
    optionButtonImage: {
        tintColor: '#FFFFFF',
        height: 40,
        width: 40,
    },
    optionButtonView: {
        alignItems: 'center',
        justifyContent: 'center'
    },
    logoutButton: {
        marginTop: 30,
        width: 100,
        height: 40,
        justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: 'black',
		shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
		borderWidth: 2,
		borderColor: '#FFFFFF',
        marginRight: 50,
        marginLeft: 50
    },
    buttonContainer: {
        flex: 2,
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'row'
    },
    backButtonContainer: {
        flex: 2,
        justifyContent: 'center',
        alignItems: 'flex-start',
        flexDirection: 'row'
    },
    title: {
        flex: 1,
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