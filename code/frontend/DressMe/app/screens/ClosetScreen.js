import React, { Component } from 'react';
//var RootNav = require('./laundry.ios');
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView
} from 'react-native';

var ClosetItem = require('./components/closet-item')

var itemTypes = ['Tank Top', 'T Shirt', 'Long Sleeve', 'Shorts', 'Long Pants', 'Hoodie', "Windbreaker", "Sweater", "Winter Coat", "Rain Jacket" ]

// var ClosetScreen = React.createClass({
// 	render: function() {
// 		return <View style={styles.container}>
// 			<Text style={styles.title}>
// 				Closet
// 			</Text>
// 			<View style={styles.closetItems}>
// 				{this.closet()}
// 			</View>
// 		</View>
// 	},
var ClosetScreen = React.createClass({
	render: function() {
		return <ScrollView style={styles.container}>
			<View style={styles.title}>
				<Text style={styles.titleText}>
					Closet
				</Text>
			</View>
			<View style={styles.closetItems}>
				{this.closet()}
			</View>
		</ScrollView>
	},
	closet: function() {
		var closetItems = [];

		for (var i = 0; i < 10; i++){
			var itemType = itemTypes[i];
			closetItems.push(
				<ClosetItem key={i} type={itemType} amount='30'/>
			)
		}
		return closetItems;
	}
});

var styles = StyleSheet.create({
	container: {
		flex: 1,
	},
	title: {
		justifyContent: 'center',
    	alignItems: 'center'
	},
	titleText: {
		fontSize: 36,
	},
	closetItems: {
		marginTop: 30
	}
})

module.exports = ClosetScreen;
