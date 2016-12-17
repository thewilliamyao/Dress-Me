import React, { Component, PropTypes } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Navigator,
  Image,
  Animated,
  Easing,
  LayoutAnimation,
} from 'react-native';

class RegisterScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password', id: -1, token: 'stringthing', userBoxWidth: 280, userBoxHeight: 70, userZ: 0, passBoxWidth: 280, passBoxHeight: 70, passZ: -10}
    }

    _onFocusUser() {
        LayoutAnimation.configureNext(CustomLayoutSpring);
        this.setState({userBoxHeight: 90, userBoxWidth: 300, userOffset: -10, userZ: 3})
    }

    _onBlurUser() {
        LayoutAnimation.configureNext(CustomReturnSpring);
        this.setState({userBoxHeight: 70, userBoxWidth: 280, userOffset: 0, userZ: 0})
    }

    _onFocusPass() {
        LayoutAnimation.configureNext(CustomLayoutSpring);
        this.setState({passBoxHeight: 90, passBoxWidth: 300, passOffset: -10, passZ: 3})
    }

    _onBlurPass() {
        LayoutAnimation.configureNext(CustomReturnSpring);
        this.setState({passBoxHeight: 70, passBoxWidth: 280, passOffset: 0, passZ: 0})
    }

    render() {
        return (<Image source={require('../../img/background/bg.jpg')} style = {styles.backgroundImage}>
            <View style={styles.titleContainer}>
                <Image style={styles.registerImage} source={require('../../img/clothes_icon/shirt-2.png')}/>
                <Text style={styles.register}> R e g i s t r a t i o n </Text>
            </View>
            <View style={[styles.loginContainer, {zIndex: this.state.userZ}]}>
                <TextInput
                    onFocus = {() => this._onFocusUser()}
                    onBlur = {() => this._onBlurUser()}
                    style={{
                            paddingLeft: 20,
                            marginLeft: this.state.userOffset,
                            fontSize: 12,
                            fontWeight: '600',
                            height: this.state.userBoxHeight,
                            width: this.state.userBoxWidth, 
                            backgroundColor: '#FFFFFF'}}
                    onChangeText={(text) => this.setState({text})}
                    value={this.state.text}/>
            </View>
            <View style={[styles.loginContainer, {zIndex: this.state.passZ}]}>
                <TextInput
                    onFocus = {() => this._onFocusPass()}
                    onBlur = {() => this._onBlurPass()}
                    style={{
                            paddingLeft: 20,
                            marginLeft: this.state.passOffset,
                            fontSize: 12,
                            fontWeight: '600',
                            height: this.state.passBoxHeight,
                            width: this.state.passBoxWidth, 
                            zIndex: this.state.passZ,
                            backgroundColor: '#FFFFFF'}}
                    onChangeText={(text1) => this.setState({text1})}
                    value={this.state.text1}/>
            </View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleSubmitPress()}
                  style={styles.loginButton}>
                    <Text style={styles.loginButtonText}>
                      SUBMIT
                    </Text>
                </TouchableHighlight>
                <Text 
                    style={styles.createAccount}
                    onPress={() => this.handleBackPress()}>
                    Cancel
                </Text>
            </View>
        
        </Image>
        );
    }

    handleSubmitPress() {

        fetch('https://dry-beyond-51182.herokuapp.com/api/v1/user', {
          method: 'POST',
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
                tabbing: "tab2",
                id: this.state.id,
                token: this.state.token
            })
        }
        else {
            console.log("Failed Registration")
        }

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
    backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        justifyContent: 'center',
        alignItems: 'center'
        //resizeMode: 'cover', // or 'stretch'
    },
    registerImage: {
        height: 80,
		width: 80,
        marginBottom: 30,
    },
    register: {
        fontSize: 24,
        fontWeight: '700'
    },
    titleContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
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
        marginBottom: 20
    },
    loginButtonText: {
        fontSize: 12,
        fontWeight: '600',
    },
    loginContainer: {
        justifyContent: 'center',
        alignItems: 'center',
        height: 70,
        width: 280,
        shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
        // backgroundColor: '#FFFFFF' 
    },
    buttonContainer: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        flexDirection: 'column',
    },
    loginField: {
        marginLeft: 20,
        fontSize: 12,
        fontWeight: '600',
        height: 70,
        width: 280, 
        backgroundColor: '#FFFFFF'
    },
    createAccount: {
        color: '#DDDDDD',
        textDecorationLine: 'underline',
    },
})

module.exports = RegisterScreen;