import React, { Component } from 'react';
//var RootNav = require('./laundry.ios');
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView,
  TextInput
} from 'react-native';

var ClosetItem = require('./components/closet-item')

var itemTypes = ['boots', 'hoodie', 'long_pants', 'long_sleeve', 'rain_jacket', 'sandals', 'scarf', 'shoes', 'shorts', 'sweater', 't_shirt', 'tank_top', 'umbrella', 'windbreaker', 'winter_coat']

// var this.state.itemNum = []

class ClosetScreen extends Component{
	static closetJson = null;

	constructor(props) {
	    super(props)
	    this.state = {
	      ClosetList: null,
	      itemNum: ['...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...'],
	      text: '0'
	    };
  	}

  	// componentWillMount() {
  	// 	this.getCloset();
  	// }

	render(){
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
	}

	closet() {
		this.getCloset();
		var closetItems = [];

		for (var i = 0; i < 15; i++){
			var itemType = itemTypes[i];
			var number = this.state.itemNum[i];
			// console.log(number);
			console.log(this.state.itemNum[i]);
			closetItems.push(
				<ClosetItem key={i} type={itemType} amount={this.state.itemNum[i]}/>
			)
		}
		return closetItems;
	}


	getCloset() {
		if (this.state.ClosetList == null) {
	  		fetch('https://dry-beyond-51182.herokuapp.com/api/v1/closet/' + this.props.id, {
	        	method: 'GET',
	        	headers: {
	          		'Content-Type': 'application/json',
	          		'token': this.props.token
	        	}
	        
	      	}).then((response) => response.json())
	        	.then((responseJson) => {
	          		console.log(responseJson.boots);
	          		this.setState({ClosetList: responseJson});
	          		console.log(this.state.ClosetList.boots);
	          		console.log(this.state.ClosetList);
	          		// console.log(itemTypes[0]);
	          		// parseClosetJson()
	          		// console.log(closetJson);
	          		{this.getNums()}
	          		// var tempArray = this.closet();
	          		// console.log("time to print");
	          		// return tempArray;
	        	})
        }	
	}

	getNums(){
		// var itemNum = this.state.itemNum.slice();
		// itemTypes.map((item) => {
		// 	itemNum.push(this.state.ClosetList[item]);
		// 	this.setState({ itemNum: itemNum })
		// })
		// console.log("here");
		// this.state.itemNum.map((item) => {
		// 	console.log(item);
		// })
		for (var i = 0; i < 15; i++) {
			var itemNum = this.state.itemNum.slice();
			itemNum[i] = (this.state.ClosetList[itemTypes[i]]);
			this.setState({ itemNum: itemNum });
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
		marginTop: 30,
	},
	amount: {
		height:40, 
		width: 40, 
		borderColor: 'gray', 
		color:'white', 
		borderWidth: 1,
		justifyContent: 'center',
		alignItems: 'center',
	}
})

module.exports = ClosetScreen;
