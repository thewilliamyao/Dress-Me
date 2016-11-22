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
import RecommendationScreen from './app/screens/RecommendationScreen'
import ClosetScreen from './app/screens/ClosetScreen'

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
          title="Recommendation"
          selected={this.state.selectedTab === "tab1"}
          onPress={() => {
            this.setState({
              selectedTab: "tab1",
            });
          }}>

          <RecommendationScreen/>
          {/*<AppNavigator initialRoute={{ident: "Recommendation", index: 0}} />*/}
          {/*this._renderContent("blue")*/}

        </TabBarIOS.Item>

        <TabBarIOS.Item
          title="Closet"
          selected={this.state.selectedTab === "tab2"}
          onPress={() => {
            this.setState({
              selectedTab: "tab2",
            });
          }}>

          <ClosetScreen/>

        </TabBarIOS.Item>

        <TabBarIOS.Item
          title="Laundry"
          selected={this.state.selectedTab === "tab3"}
          onPress={() => {
            this.setState({
              selectedTab: "tab3",
            });
          }}>

          {this._renderContent("green")}

        </TabBarIOS.Item>

      </TabBarIOS>
    )
  }

  changeTheState(){
    console.log(this.state.selectedTab);
  }

  _renderContent = (color: string) => {
    return(
      <View style = {[{flex: 1}, {backgroundColor: color}]}>
            <Text> "Tab 2" </Text>
      </View>
    );
  };
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
