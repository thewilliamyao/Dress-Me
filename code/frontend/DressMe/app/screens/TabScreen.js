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
//import AppNavigator from './app/navigation/AppNavigator'
import Icon from 'react-native-vector-icons/FontAwesome'
import RecommendationScreen from './RecommendationScreen'
import ClosetScreen from './ClosetScreen'
import LaundryScreen from './LaundryScreen'

class TabScreen extends Component {
  constructor(props) {
    super(props)
    this.state = {  selectedTab: this.props.tabbing, userId: this.props.id, token: this.props.token}
  }

  render() {
    return (
      <TabBarIOS
        selectedTab = {this.state.selectedTab}>

        <TabBarIOS.Item
          title="Recommendation"
          selected={this.state.selectedTab === "tab1"}
          onPress={() => {
            this.setState({
              selectedTab: "tab1",
            });
          }}>

          <RecommendationScreen id = {this.state.userId} token = {this.state.token} navigator={this.props.navigator}/>

        </TabBarIOS.Item>

        <TabBarIOS.Item
          title="Closet"
          selected={this.state.selectedTab === "tab2"}
          onPress={() => {
            this.setState({
              selectedTab: "tab2",
            });
          }}>

          <ClosetScreen id = {this.state.userId} token = {this.state.token}/>

        </TabBarIOS.Item>

        <TabBarIOS.Item
          title="Laundry"
          selected={this.state.selectedTab === "tab3"}
          onPress={() => {
            this.setState({
              selectedTab: "tab3",
            });
          }}>

          <LaundryScreen id = {this.state.userId} token = {this.state.token}/>

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

module.exports = TabScreen;
