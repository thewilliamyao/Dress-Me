'use strict'
import React, { Component } from 'react';
import  { Navigator, Text, StyleSheet} from 'react-native'
import RecommendationScreen from '../screens/RecommendationScreen'
import ClosetScreen from '../screens/ClosetScreen'
import LaundryScreen from '../screens/LaundryScreen'

class AppNavigator extends Component {

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
            renderScene={this.renderScene}
            configureScene={(route) => ({
              ...route.sceneConfig || Navigator.SceneConfigs.FloatFromRight })} />
        )
    }
}

const styles = StyleSheet.create({

  navigatorStyles: {

  }

})

module.exports = AppNavigator

