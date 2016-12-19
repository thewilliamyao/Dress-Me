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
  View,
  StatusBar
} from 'react-native';
//import AppNavigator from './app/navigation/AppNavigator'
import Icon from 'react-native-vector-icons/FontAwesome'
import RecommendationScreen from './app/screens/RecommendationScreen'
import ClosetScreen from './app/screens/ClosetScreen'
import LaundryScreen from './app/screens/LaundryScreen'
import LoginScreen from './app/screens/LoginScreen'
import RegisterScreen from './app/screens/RegisterScreen'
import SettingsScreen from './app/screens/SettingsScreen'
import FeedbackScreen from './app/screens/FeedbackScreen'
//import TabScreen from './app/screens/TabScreen'

export default class DressMe extends Component {
  _renderScene(route, navigator) {
    var globalProps = {navigator}

    switch(route.ident) {
      case "Login" :
        return (
          <LoginScreen
            {...globalProps}/>
        )
      {/*case "TabScreen" :
        return(
          <TabScreen
            id = {route.id} token = {route.token} tabbing = {route.tabbing} {...globalProps}/>
        )*/}
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
      case "Settings" :
        return(
          <SettingsScreen
            id = {route.id} token = {route.token} {...globalProps}/>
        )
      case "Rate" :
        return(
          <FeedbackScreen
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
            id = {route.id} token = {route.token} {...globalProps}/>
        )
    }
  }

  render() {
    return (
      <Navigator
        initialRoute={{ident: "Login", statusBarHidden: true}}
        renderScene={this._renderScene}
        configureScene={(route, routeStack) => Navigator.SceneConfigs.FloatFromRight}/>
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
