import React, { Component, PropTypes } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Image,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Navigator
} from 'react-native';

export default class LoginScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'blah', text1: 'MEERP', id: -1, token: 'Blah'}
    }
    render() {

        return (<Image source={require('../../img/background/bg.jpg')} style = {styles.backgroundImage}>
            
            <View style={styles.titleContainer}>
                <Text style={styles.dressMe}> Dress Me </Text>
            </View>
            <View style={styles.loginContainer}>
                <TextInput
                    style={styles.loginField}
                    onChangeText={(text) => this.setState({text})}
                    value={this.state.text}/>
                <TextInput
                    style={styles.loginField}
                    onChangeText={(text1) => this.setState({text1})}
                    value={this.state.text1}/>
            </View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleLoginPress()}
                  style={styles.loginButton}>
                    <Text style={styles.loginButtonText}>
                      LOG IN
                    </Text>
                </TouchableHighlight>
                <Text 
                    style={styles.createAccount}
                    onPress={() => this.handleRegisterPress()}>
                    Create Account
                </Text>
            </View>
        
        </Image>
        );
    }

    handleLoginPress() {

        fetch('https://dry-beyond-51182.herokuapp.com/api/v1/login', {
          method: 'PUT',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: this.state.text,
            password: this.state.text1,
          })
        }).then((response) => response.json())
        .then((responseJson) => {
          this.recommendationJson = responseJson;
          this.choiceInt = 1;
          this.setState({id: responseJson.id});
          this.setState({token: responseJson.token});
        })

        
        if(this.state.id != -1) {
            this.props.navigator.push({
                ident: "Recommendation",
                tabbing: "tab1"
            })
        }
        else {
            console.log("Failed Login")
        }
    
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
    },
    dressMe: {
        fontSize: 24,
        fontWeight: '800'
    },
    backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        //resizeMode: 'cover', // or 'stretch'
    },
    titleContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },
    loginContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        flexDirection: 'column',
    },
    loginButton: {
        width: 280,
        height: 70,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
        shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
    },
    loginButtonText: {
        fontSize: 12,
        fontWeight: '600',
    },
    buttonContainer: {
        flex: 1,
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'column',
    },
    loginField: {
        height: 70,
        width: 280, 
        shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
    },
    createAccount: {
        color: '#DDDDDD',
        textDecorationLine: 'underline',
    }
})

module.exports = LoginScreen;