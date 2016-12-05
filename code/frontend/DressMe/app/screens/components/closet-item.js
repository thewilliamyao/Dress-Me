import React, { Component } from 'react';
//var RootNav = require('./laundry.ios');
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  Image,
  TouchableHighlight,
  TextInput
} from 'react-native';

class ClosetItem extends Component{
	constructor(props) {
	    super(props)
	    this.state = {
	      text: this.props.amount
	    };
  	}

  	componentWillReceiveProps(nextProps) {
  		if(this.state.text != nextProps.amount) {
  			this.setState({
				text: nextProps.amount
  			})
  		}
  	}

	render() {
		const {text} = this.state;
		//same as const text = this.state.text
		const {amount} = this.props;

		return <View style={styles.container}>
			<View style={styles.icon}>
				<Image style={styles.iconImage} source={require('../../../img/clothes_icon/shirt-2.png')}/>
			</View>
			<View style={styles.type}>
				<Text style={styles.clothes}>
					{this.props.type}
				</Text>
			</View>
			<View style={styles.amount}>
				<TextInput
		        style={styles.amountText}
		        onChangeText={(text) => this.setState({text})}
		        value={text + ''}/>
			</View>
		</View>
	}
};

var styles = StyleSheet.create({
	container: {
		flexDirection: 'row',
		height: 80,
		marginTop: 5,
		marginBottom: 5,
		borderColor: '#000000',
		borderWidth: 1
	},
	clothes: {
		fontSize: 18,
		color: '#000000'
	},
	icon: {
		flex: 1,
		backgroundColor: '#000000',
		justifyContent: 'center',
    	alignItems: 'center'
	},
	iconImage: {
		tintColor: '#FFFFFF',
		height: 50,
		width: 50
	},
	type: {
		flex: 2,
		justifyContent: 'center',
    	alignItems: 'center'
	},
	amount: {
		flex: 1,
		backgroundColor: '#000000',
		justifyContent: 'center',
    	alignItems: 'center'
	},
	amountText: {
		fontSize: 36,
		color: '#FFFFFF',
		height:40, 
		width: 40, 
		borderColor: 'gray', 
		color:'white', 
		borderWidth: 1,
		justifyContent: 'center',
		alignItems: 'center',
	}
});

module.exports = ClosetItem;
