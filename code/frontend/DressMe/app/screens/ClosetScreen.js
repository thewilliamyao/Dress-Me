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

//var itemTypes = ['Tank Top', 'T Shirt', 'Long Sleeve', 'Shorts', 'Long Pants', 'Hoodie', "Windbreaker", "Sweater", "Winter Coat", "Rain Jacket" ]

var itemTypes = ['Umbrella', 'Long Pants', 'Scarf', 'Long Sleeve', 'Tank Top', 'Sandals', 'Rain Jacket', 'Shoes', 'Hoodie', 'Windbreaker', 'Boots', 'Sweater', 'Shorts', 'Winter Coat', 'T-Shirt']

var itemNum = []

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
class ClosetScreen extends Component{
	constructor(props) {
	    super(props)
	    this.state = {
	      ClosetList: null
	    }
  	}

	render(){
		return <ScrollView style={styles.container}>
			<View style={styles.title}>
				<Text style={styles.titleText}>
					Closet
				</Text>
			</View>
			<View style={styles.closetItems}>
				{this.getCloset()}
				{this.closet()}
			</View>
		</ScrollView>
	}

	closet() {
		var closetItems = [];

		for (var i = 0; i < 15; i++){
			var itemType = itemTypes[i];
			var number = itemNum[i];
			console.log({itemNum});
			closetItems.push(
				<ClosetItem key={i} type={itemType} amount={itemNum}/>
			)
		}
		return closetItems;
	}

	getCloset() {
      	fetch('https://dry-beyond-51182.herokuapp.com/api/v1/user/closet/0', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
        
      }).then((response) => response.json())
        .then((responseJson) => {
          this.recommendationJson = responseJson;
          this.choiceInt = 1;
          this.setState({ClosetList: recommendationJson});
          //{this.getNums()}
        })
	}

	getNums(){
		for(var j = 0; j < 15; j++) {
          	var num = this.state.ClosetList[j];
          	itemNum.push(
          		num
          	)
        }
	}
};

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
