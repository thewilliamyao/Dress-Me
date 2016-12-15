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

export default class LoginScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'blah', text1: 'MEERP'}
    }
    render() {

        return (<View style = {styles.container}>
            <View style={styles.titleContainer}>
                <Text> DressMe </Text>
            </View>
            <View style={styles.loginContainer}>
                <TextInput
                    style={{height: 40, borderColor: 'gray', borderWidth: 1}}
                    onChangeText={(text) => this.setState({text})}
                    value={this.state.text}/>
                <TextInput
                    style={{height: 40, borderColor: 'gray', borderWidth: 1}}
                    onChangeText={(text1) => this.setState({text1})}
                    value={this.state.text1}/>
            </View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleLoginPress()}
                  style={styles.loginButton}>
                    <Text>
                      Login
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleRegisterPress()}
                  style={styles.loginButton}>
                    <Text>
                      Register
                    </Text>
                </TouchableHighlight>
            </View>
        
        </View>
        );
    }

    handleLoginPress() {
        this.props.navigator.push({
            ident: "Recommendation",
            tabbing: "tab1"
        })
    }

    handleRegisterPress() {
       this.props.navigator.push({
            ident: "Register"
        }) 
    }



}{/*This is the end of class brack*/}


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
    buttonContainer: {
        flex: 1,
        backgroundColor: 'white',
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'row',
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

module.exports = LoginScreen;