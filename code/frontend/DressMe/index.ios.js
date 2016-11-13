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
  View
} from 'react-native';
import AppNavigator from './app/navigation/AppNavigator'
import Icon from 'react-native-vector-icons/FontAwesome'

export default class DressMe extends Component {
  constructor(props) {
    super(props)
    this.state = {
      selectedTab: "tab1"
    }
  }

  render() {
    return (

      <TabBarIOS
        selectedTab = {this.state.selectedTab}>

        <TabBarIOS.Item
          selectedTab = {this.state.selectedTab === "tab1"}
          title={"Recommendation"}
          onPress={() => this.setState({selectedTab: "tab1"})}>
          <AppNavigator
            initialRoute={{ident: "Recommendation"}} />
        </TabBarIOS.Item>

        <TabBarIOS.Item
          selectedTab = {this.state.selectedTab === "tab2"}
          title={"Closet"}
          onPress={() => this.setState({selectedTab: "tab2"})}>
          console.log('Closet was Pressed');
          {this.changeTheState()}
        </TabBarIOS.Item>

        <TabBarIOS.Item
          selectedTab = {this.state.selectedTab === "tab3"}
          title={"Laundry"}
          onPress={() => this.setState({selectedTab: "tab3"})}>
          console.log('Laundry was Pressed');
          {this.changeTheState()}
        </TabBarIOS.Item>

      </TabBarIOS>
    )
  }

  changeTheState(){
    console.log(this.state.selectedTab);
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
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
