'use strict'
import React, { Component } from 'react';
import  { Navigator, Text, StyleSheet} from 'react-native'
import RecommendationScreen from '../screens/RecommendationScreen'
import ClosetScreen from '../screens/ClosetScreen'
import LaundryScreen from '../screens/LaundryScreen'

class AppNavigator extends Component {

    const routes = [
        {ident: 'Recommendation', index: 0},
        {ident: 'Closet', index: 1},
        {ident: 'Laundry', index: 2}
    ];

    renderScene(route, navigator) {
        var globalNavigatorProps = { navigator }

        switch(route.ident) {
            case "Recommendation":
                return (
                    <RecommendationScreen
                        {...globalNavigatorProps} />
                )

            case "Closet":
                return (
                    <ClosetScreen
                        {...globalNavigatorProps} />
                )

            case "Laundry":
                return (
                    <LaundryScreen
                        {...globalNavigatorProps} />
                )
        }
    }


    render() {
        return (
          <Navigator
            initialRoute={this.props.initialRoute}
            ref="appNavigator"
            style={styles.navigatorStyles}
            renderScene={this.renderScene(initialRoute, navigator)}
            configureScene={(route) => ({
              ...route.sceneConfig || Navigator.SceneConfigs.FloatFromRight })} />
        )
    }
}

const styles = StyleSheet.create({

  navigatorStyles: {
    fontSize: 36
  }

})

module.exports = AppNavigator

