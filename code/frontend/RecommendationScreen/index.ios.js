/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
//var formatTime = require('minutes-seconds-milliseconds');
import LinearGradient from 'react-native-linear-gradient';
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} from 'react-native';

var Recommendation = React.createClass({
  render: function(){
    return <View style= {styles.container}>
    {/*View Below is Settings Bar*/}  
      <View style= {[styles.settingsContainer, styles.buttonWrapper, this.border('pink')]}>
        {this.settingsButton()}
      </View>
    {/*View Below is Time Bar*/}
      <View style= {[styles.timeContainer, this.border('red')]}>
        <Text>
          Hello there!!!!
        </Text>
      </View>
    {/*View Below is Dress up Guy*/}
      <View style= {[styles.displayContainer, this.border('lime')]}>
        {this.middleDisplay()}
      </View>
    {/*View Below is Dress me Button*/}
      <View style= {[styles.dressMeContainer,styles.buttonWrapper, this.border('cyan')]}>
        {this.dressMeButton()}
        
        {/*<LinearGradient
          start={[0.0, 0.25]} end={[0.5, 1.0]}
          locations={[0,0.5,0.6]}
          colors={['#4c669f', '#3b5998', '#192f6a']}
          style={styles.linearGradient}>
          <Text style={styles.buttonText}>
            Sign in with Facebook
          </Text>
        </LinearGradient>
      */}
      </View>
    </View>
  },

  settingsButton: function() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={this.handleSettingsPress}
      style={styles.settingsButtonS}
      >
        <Text>
          Settings
        </Text>

    </TouchableHighlight>
  },

  handleSettingsPress: function (){
    console.log('Settings was pressed');
  },

  middleDisplay: function() {
    return <View style={[styles.displayContainer,this.border('lime')]}>
      <View style={[styles.midLeft, styles.arrowWrapper, this.border('orange')]}>
        {this.arrowRightButton()}
      </View>

      <View style={[styles.midMid, this.border('orange')]}>
        
      </View>

      <View style={[styles.midRight, styles.arrowWrapper, this.border('orange')]}>
        {this.arrowLeftButton()}
      </View>
    </View>
  },

  arrowLeftButton: function(){
    return <TouchableHighlight
      underlayColor="gray"
      onPress={this.handleArrowLeft}
      style= {[styles.triangle,styles.triangleRotRight]}
      > 
        <Text>
          Back
        </Text>
    </TouchableHighlight>
  }, 

  handleArrowLeft: function(){
    console.log('Left Arrow was pressed');
  },

  arrowRightButton: function(){
    return <TouchableHighlight
      underlayColor="gray"
      onPress={this.handleArrowRight}
      style= {[styles.triangleRotLeft, styles.triangle]}
      > 
        <Text>
          Forward
        </Text>
    </TouchableHighlight>
  },

  handleArrowRight: function(){
    console.log('Right Arrow was pressed');
  },

  dressMeButton: function() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={this.handleDressMePress}
      style={styles.dressMeButton}
      >
        <Text>
          Dress Me!
        </Text>

    </TouchableHighlight>
  },

  handleDressMePress:function() {
    console.log('Dress Me was pressed');
  },

  border: function(color){
    return {
      borderColor: color,
      borderWidth: 4
    }
  }
});

var styles = StyleSheet.create({
  container: {
    flex: 1
  },
  settingsContainer: {
    flex: 1
  },
  timeContainer: {
    flex: 2
  },
  displayContainer: {
    flex: 9,
    flexDirection: 'row'
  },
  dressMeContainer: {
    flex: 3
  },
  midLeft: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  midMid: {
    flex: 3
  },
  midRight: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  dressMeButton: {
    width: 100,
    height: 50,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'red'
  },
  settingsButtonS: {
    width: 100,
    height: 25,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'red'
  },
  buttonWrapper: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center'
  },
  arrowWrapper: {
    flexDirection: 'column',
    justifyContent: 'space-around',
    alignItems: 'center'
  },
  triangle: {
    width: 0,
    height: 0,
    backgroundColor: 'transparent',
    borderStyle: 'solid',
    borderLeftWidth: 25,
    borderRightWidth: 25,
    borderBottomWidth: 50,
    borderLeftColor: 'transparent',
    borderRightColor: 'transparent',
    borderBottomColor: 'red',
    justifyContent: 'center',
    alignItems: 'center',
  },
  triangleRotRight: {
    transform: [
      {rotate: '90deg'}
    ]
  },
  triangleRotLeft: {
    transform: [
      {rotate: '-90deg'}
    ]
  },
  linearGradient: {
    flex: 1,
    paddingLeft: 15,
    paddingRight: 15,
    borderRadius: 5,
    //backgroundColor: 'cyan'
  },
  buttonText: {
    fontSize: 18,
    fontFamily: 'Gill Sans',
    textAlign: 'center',
    margin: 10,
    color: '#ffffff',
    backgroundColor: 'transparent',
  },
});

AppRegistry.registerComponent('Recommendation', () => Recommendation);
