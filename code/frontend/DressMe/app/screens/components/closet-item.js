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
  TextInput,
  Animated,
  LayoutAnimation,
  Easing
} from 'react-native';

var CustomLayoutSpring = {
    duration: 150,
    create: {
      type: LayoutAnimation.Types.spring,
      property: LayoutAnimation.Properties.scaleXY,
      springDamping: 0.3,
    },
    update: {
      type: LayoutAnimation.Types.spring,
      springDamping: 0.3,
    },
  };

var CustomReturnSpring = {
    duration: 150,
    create: {
        type: LayoutAnimation.Types.spring,
        property: LayoutAnimation.Properties.scaleXY,
        springDamping: 0.3,
    },
    update: {
        type: LayoutAnimation.Types.spring,
        springDamping: 0.3,
    },
};

class ClosetItem extends Component{
	constructor(props) {
	    super(props)
	    this.state = {
	      text: this.props.amount,
		  amountLeft: 0,
		  amountRight: 0,
	    };
  	}

	_onFocus() {
        LayoutAnimation.configureNext(CustomLayoutSpring);
        this.setState({amountLeft: 10, amountRight: 20})
    }

    _onBlur() {
        LayoutAnimation.configureNext(CustomReturnSpring);
        this.setState({amountLeft: 0, amountRight: 0})
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
			<View style={{
				flex: 1,
				backgroundColor: '#000000',
				justifyContent: 'center',
    			alignItems: 'center',
				paddingLeft: this.state.amountLeft,
				paddingRight: this.state.amountRight}}>
				<TextInput
					onFocus = {() => this._onFocus()}
					onBlur = {() => this._onBlur()}
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
		shadowOffset:{
            width: 2,
            height: 2,
        },
        shadowColor: 'black',
        shadowOpacity: 0.5,
	},
	clothes: {
		fontSize: 18,
		color: '#000000'
	},
	icon: {
		flex: 1,
		backgroundColor: '#FFFFFF',
		justifyContent: 'center',
    	alignItems: 'center'
	},
	iconImage: {
		height: 50,
		width: 50
	},
	type: {
		flex: 2,
		justifyContent: 'center',
    	alignItems: 'center',
		backgroundColor: '#FFFFFF'
	},
	amount: {
		flex: 1,
		backgroundColor: '#000000',
		justifyContent: 'center',
    	alignItems: 'center'
	},
	amountText: {
		marginLeft: 20,
		fontSize: 36,
		color: '#FFFFFF',
		height: 40, 
		width: 40, 
		justifyContent: 'center',
		alignItems: 'center',
	}
});

module.exports = ClosetItem;
