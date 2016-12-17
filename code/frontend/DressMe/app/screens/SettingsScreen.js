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
  Navigator
} from 'react-native';

class SettingsScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password', id: this.props.id, token: this.props.token}
    }
    render() {
        return (<View style = {styles.container}>
            <View style={styles.settingsTitle}>
                <Text> Settings </Text>
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
            
            
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleBackPress()}
                  style={styles.loginButton}>
                    <Text>
                      Back
                    </Text>
                </TouchableHighlight>
            </View>
        </View>
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
        this.props.navigator.pop()
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
    textColor: {
        color: 'white'
    },
    input: {
        flex: 1,
        color: '#FFFFFF',
        height:40, 
        width: 40, 
        borderColor: 'gray', 
        color:'white', 
        borderWidth: 1,
        justifyContent: 'center',
        alignItems: 'center'
    }

})

module.exports = SettingsScreen;