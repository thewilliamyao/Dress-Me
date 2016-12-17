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
  TextInput,
  Image,
  LayoutAnimation
} from 'react-native';

var ClosetItem = require('./components/closet-item')

var itemTypes = ['boots', 'hoodie', 'long_pants', 'long_sleeve', 'rain_jacket', 'sandals', 'scarf', 'shoes', 'shorts', 'sweater', 't_shirt', 'tank_top', 'umbrella', 'windbreaker', 'winter_coat']

var CustomLayoutSpring = {
    duration: 50,
    create: {
        type: LayoutAnimation.Types.spring,
        property: LayoutAnimation.Properties.scaleXY,
        springDamping: 0.5,
    },
    update: {
        type: LayoutAnimation.Types.spring,
        springDamping: 0.5,
    },
};

// var this.state.itemNum = []

class ClosetScreen extends Component{
	static closetJson = null;

	constructor(props) {
	    super(props)
	    this.state = {
	      ClosetList: null,
	      itemNum: ['...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...'],
	      text: '0',
		  buttonWidth: 140,
		  buttonHeight: 40,
		  buttonMarginTop: 0,
		  buttonMarginLeft: 0,
	    };
  	}

  	// componentWillMount() {
  	// 	this.getCloset();
  	// }

	render(){
		return (
		<Image source={require('../../img/background/bg-mahogany.jpg')} style = {styles.backgroundImage}>
			<View style={styles.title}>
				<Text style={styles.titleText}>
					C l o s e t
				</Text>
			</View>
			<View style={styles.container}>
				<ScrollView>
					<View style={styles.closetItems}>
						{this.closet()}
					</View>
				</ScrollView>
			</View>
			<View style={styles.update}>
				{this.resetLaundryButton()}
			</View>
		</Image>
		)
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
				<ClosetItem style={styles.closetElement} key={i} type={itemType} amount={this.state.itemNum[i]}/>
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

	resetLaundryButton() {
		return <TouchableHighlight
		underlayColor="gray"
		onPress={() => this.handleUpdatePress()}
		//onPressIn={() => this.closetButtonAnimation()}
		//onPressOut={() => this.closetButtonReturnAnimation()}
		style={[styles.updateButton,
			 {width: this.state.buttonWidth, 
				 height: this.state.buttonHeight, 
				 marginTop: this.state.buttonMarginTop, 
				 marginLeft: this.state.buttonMarginLeft}]}
		>
			<Text style={styles.updateButtonText}>
			UPDATE CLOSET
			</Text>

		</TouchableHighlight>
	}

	//Animation needs a little work, might take out

	// closetButtonAnimation() {
	// 	LayoutAnimation.configureNext(CustomLayoutSpring);
	// 	this.setState({buttonMarginLeft: -2.5, buttonMarginTop: -2.5, buttonWidth: 145, buttonHeight: 45})
	// }

	// closetButtonReturnAnimation() {
	// 	LayoutAnimation.configureNext(CustomLayoutSpring);
	// 	this.setState({buttonMarginLeft: 0, buttonMarginTop: 0, buttonWidth: 140, buttonHeight: 40})
	// }

	 handleUpdatePress() {
	// 	this.closetButtonAnimation();
	 }
};

var styles = StyleSheet.create({
	container: {
		height: 440
	},
	backgroundImage: {
        flex: 1,
        height: null,
        width: null,
        justifyContent: 'flex-start',
        alignItems: 'center',
		flexDirection: 'column'
		
        //resizeMode: 'cover', // or 'stretch'
    },
	title: {
		paddingTop: 20,
		paddingBottom: 20,
		justifyContent: 'center',
    	alignItems: 'center'
	},
	titleText: {
		fontSize: 36,
		fontWeight: '700',
		color: '#FFFFFF'
	},
	closetItems: {
		width: 300,
	},
	update: {
		marginTop: 30,
		justifyContent: 'center',
		alignItems: 'center'
	},
	updateButton: {
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: 'black',
		shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
		borderWidth: 2,
		borderColor: '#FFFFFF'
	},
	updateButtonText: {
        fontSize: 12,
        fontWeight: '600',
		color: '#FFFFFF'
    },
})

module.exports = ClosetScreen;
