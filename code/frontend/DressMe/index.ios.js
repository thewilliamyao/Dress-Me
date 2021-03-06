'use strict'
import React, { Component } from 'react';
import {
  AppRegistry,
  TabBarIOS,
  StyleSheet,
  Text,
  Navigator,
  TouchableHighlight,
  View,
  StatusBar
} from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome'
import RecommendationScreen from './app/screens/RecommendationScreen'
import ClosetSetup from './app/screens/ClosetSetup'
import ClosetScreen from './app/screens/ClosetScreen'
import LaundryScreen from './app/screens/LaundryScreen'
import LoginScreen from './app/screens/LoginScreen'
import RegisterScreen from './app/screens/RegisterScreen'
import SettingsScreen from './app/screens/SettingsScreen'

export default class DressMe extends Component {

  componentWillMount() {
    StatusBar.setHidden(true);
  }

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
          <RecommendationScreen
            id = {route.id} token = {route.token} {...globalProps}/>
        )
      case "Register" :
        return(
            <RegisterScreen
              {...globalProps}/>
        )
      case "Setup" :
        return(
          <ClosetSetup
            id = {route.id} token = {route.token} {...globalProps}/>
        )
      case "Settings" :
        return(
          <SettingsScreen
            id = {route.id} token = {route.token} {...globalProps}/>
        )
      case "Closet" :
        return(
          <ClosetScreen
            id = {route.id} token = {route.token} {...globalProps}/>
        )
      case "Laundry" :
        return(
          <LaundryScreen
            id = {route.id} token = {route.token} loc = {route.loc} {...globalProps}/>
        )
    }
  }

  render() {
    return (
      <Navigator
        initialRoute={{ident: "Login", statusBarHidden: true}}
        renderScene={this._renderScene}
        configureScene={(route, routeStack) => Navigator.SceneConfigs.FloatFromLeft}/>
    )
  } 
}
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
