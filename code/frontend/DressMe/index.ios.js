/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
'use strict'
import React, { Component } from 'react';
import {
  AppRegistry,
  TabBarIOS,
  StyleSheet,
  Text,
  Navigator,
  TouchableHighlight,
  View
} from 'react-native';
//import AppNavigator from './app/navigation/AppNavigator'
import Icon from 'react-native-vector-icons/FontAwesome'
import RecommendationScreen from './app/screens/RecommendationScreen'
import ClosetScreen from './app/screens/ClosetScreen'
import LaundryScreen from './app/screens/LaundryScreen'
import LoginScreen from './app/screens/LoginScreen'
import RegisterScreen from './app/screens/RegisterScreen'
import SettingsScreen from './app/screens/SettingsScreen'
import TabScreen from './TabScreen'

export default class DressMe extends Component {
  _renderScene(route, navigator) {
    var globalProps = {navigator}

    switch(route.ident) {
      case "Login" :
        return (
          <LoginScreen
            {...globalProps}/>
        )
      case "Recommendation" :
        return(
          <TabScreen
            {...globalProps}/>
        )
      case "Register" :
        return(
            <RegisterScreen
              {...globalProps}/>
        )
      case "Settings" :
        return(
          <SettingsScreen
            {...globalProps}/>
        )
    }
  }

  render() {
    const routes = [
      {title: 'LoginScreen', index: 0},
      {title: 'Recommendation', index: 1},
    ];
    return (
      <Navigator
        initialRoute={{ident: "Login"}}
        renderScene={this._renderScene}
        configureScene={this._configureScene}/>
    )
  } 
}
/*
<View style={styles.container}>
        <LoginScreen/>
      </View>
      */
const styles = StyleSheet.create({
  container: {
    flex: 1,
  
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('DressMe', () => DressMe);
