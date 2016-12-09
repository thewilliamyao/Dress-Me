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

export default class DressMe extends Component {
  render() {
    const routes = [
      {title: 'LoginScreen', index: 0},
      {title: 'Second Scene', index: 1},
    ];
    return (
      <Navigator
        initialRoute={{title: 'My Initial Scene', index: 0}}
        renderScene={(route, navigator) => 
           <LoginScreen 
            title={route.title}

            onForward={() => {
              const nextIndex = route.index + 1;
              navigator.push({
                title:'Scene ' + nextIndex,
                component: RecommendationScreen,
                index: nextIndex,
              });
            }}

            onBack={() => {
              if (route.index > 0) {
                navigator.pop();
              }
            }}
          />
        }
      />
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
