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

class FeedbackScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password'}
    }
    render() {
        return (<View style = {styles.container}>
            <View style={styles.settingsTitle}>
                <Text> Settings </Text>
            </View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleRatings(-10)}
                  style={styles.loginButton}>
                    <Text>
                      Too Cold
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleRatings(0)}
                  style={styles.loginButton}>
                    <Text>
                      Perfect
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleRatings(10)}
                  style={styles.loginButton}>
                    <Text>
                      Too Hot
                    </Text>
                </TouchableHighlight>
            </View>
            <View style={styles.backButtonContainer}>
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

    handleDoLaundryPress() {

    }

    handleRatings(rate) {
        console.log(rate)
        {/*
        fetch('https://mywebsite.com/endpoint/', {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            firstParam: {rate}
          })
        })*/}
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

module.exports = FeedbackScreen;