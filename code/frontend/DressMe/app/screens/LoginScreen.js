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
  Navigator,
  Animated,
  Easing,
  LayoutAnimation,
} from 'react-native';

import dismissKeyboard from 'dismissKeyboard';

var CustomLayoutSpring = {
    duration: 180,
    create: {
      type: LayoutAnimation.Types.spring,
      property: LayoutAnimation.Properties.scaleXY,
      springDamping: 0.5,
    },
    update: {
      type: LayoutAnimation.Types.spring,
      springDamping: 0.5,
    },
  };

var CustomReturnSpring = {
    duration: 150,
    create: {
        type: LayoutAnimation.Types.spring,
        property: LayoutAnimation.Properties.scaleXY,
        springDamping: 0.3,
    },
    update: {
        type: LayoutAnimation.Types.spring,
        springDamping: 0.3,
    },
};

export default class LoginScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password', id: -1, token: 'Blah', userBoxWidth: 280, userBoxHeight: 70, userZ: 0, passBoxWidth: 280, passBoxHeight: 70, passZ: -10}
    }

    //Need to figure this out later
    _onSomethingElse() {
        dismissKeyboard();
    }

    componentWillMount() {
        // Animate creation
        //LayoutAnimation.configureNext(CustomReturnSpring);
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

    onFocus() {
        this.setState({
        userBoxWidth: 300,
        userBoxHeight: 90,
    })
    }

    render() {

        return (<Image source={require('../../img/background/bg.jpg')} style = {styles.backgroundImage}>
            
            <View style={styles.titleContainer}>
                <Image style={styles.dressMeImage} source={require('../../img/clothes_icon/shirt-2.png')}/>
                <Text style={styles.dressMe}> D r e s s   M e </Text>
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
        }).then(() => {
            console.log(this.state.id);
            if(this.state.id != -1) {
                this.props.navigator.push({
                    ident: "Recommendation",
                    id: this.state.id,
                    token: this.state.token
                })
                this.forceUpdate();
            }
            else {
                console.log("Failed Login")
            }
        })
    
    }

    handleRegisterPress() {
       this.props.navigator.push({
            ident: "Register",
            transition: 1,
            sceneConfig: Navigator.SceneConfigs.floatFromBottom
        }) 
    }
}


var styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    dressMeImage: {
        height: 80,
		width: 80,
        marginBottom: 30,
    },
    dressMe: {
        fontSize: 24,
        fontWeight: '700'
    },
    backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        justifyContent: 'center',
        alignItems: 'center'
        //resizeMode: 'cover', // or 'stretch'
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
    buttonContainer: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        flexDirection: 'column',
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

module.exports = LoginScreen;