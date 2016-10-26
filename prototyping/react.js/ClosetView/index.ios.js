/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  Image
} from 'react-native';

export default class ClosetView extends Component {
  _onPressButton() {
    console.log("You tapped the button!");
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.toolbar}>
          <Text style={styles.toolbarTitle}>
            Put This On!
          </Text>
        </View>
        <View>
          <Text style={styles.instructions}>
            Choose from these categories {'\n'}
            what's in your closet
          </Text>
        </View>

        <View style={styles.clothing}>
          <Image source={require('./shirt.png')} style={styles.image}/>
          <View style={styles.centerElement}>
            <Text style={styles.clothingName}>
              Shirts
            </Text>
          </View>
          <View style={styles.centerElement}>
            <TouchableHighlight onPress={this._onPressButton}>
              <Text style={styles.counter}>21</Text>
            </TouchableHighlight>
          </View>
        </View>

        <View style={styles.clothing}>
          <Image source={require('./hoodie.png')} style={styles.image}/>
          <View style={styles.centerElement}>
            <Text style={styles.clothingName}>
              Hoodies
            </Text>
          </View>
          <View style={styles.centerElement}>
            <TouchableHighlight onPress={this._onPressButton}>
              <Text style={styles.counter}>21</Text>
            </TouchableHighlight>
          </View>
        </View>

                <View style={styles.clothing}>
          <Image source={require('./coat.png')} style={styles.image}/>
          <View style={styles.centerElement}>
            <Text style={styles.clothingName}>
              Coats
            </Text>
          </View>
          <View style={styles.centerElement}>
            <TouchableHighlight onPress={this._onPressButton}>
              <Text style={styles.counter}>21</Text>
            </TouchableHighlight>
          </View>
        </View>

        <View style={styles.bottomToolbar}>
          <TouchableHighlight onPress={this._onPressButton}>
            <Text style={styles.toolbarButton}>
              Skip
            </Text>
          </TouchableHighlight>
          <Text style={styles.toolbarTitle}>
          </Text>
          <TouchableHighlight onPress={this._onPressButton}>
            <Text style={styles.toolbarButton}>
            Next
            </Text>
          </TouchableHighlight>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  image: {
    flex: 1,
    width: 100,
    height: 100,
    resizeMode: 'contain',
  },
  container: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
    fontSize: 20,
  },
  button: {
    backgroundColor: '#123456',
  },
  toolbar:{
    backgroundColor: '#87CEFA',
    paddingTop: 30,
    paddingBottom: 10,
    flexDirection: 'row',
  },
  bottomToolbar:{
    backgroundColor: '#87CEFA',
    paddingTop: 20,
    paddingBottom: 20,
    flexDirection: 'row',
  },
  toolbarTitle:{
    color: '#fff',
    textAlign: 'center',
    fontWeight: 'bold',
    flex: 1,
  },
  toolbarButton:{
    width: 100,
    fontSize: 20,
    fontWeight: 'bold',
    color:'#fff',
    textAlign:'center'
  },
  clothing:{
    backgroundColor: '#87CEFA',
    flexDirection: 'row',
  },
  clothingName:{
    fontSize: 40,
  },
  centerElement: {
    justifyContent: 'center',
    alignItems: 'center',
    paddingLeft: 20,
    paddingRight: 20,
  },
  counter: {
    paddingTop: 0,
    paddingBottom: 0,
    paddingRight: 0,
    paddingLeft: 0,
    fontSize: 40,
  },
});

AppRegistry.registerComponent('ClosetView', () => ClosetView);
