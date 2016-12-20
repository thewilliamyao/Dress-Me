import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Image
} from 'react-native';

var ClosetItem = require('./components/closet-item')

var itemTypes = ['boots', 'hoodie', 'long_pants', 'long_sleeve', 'rain_jacket', 'sandals', 'scarf', 'shoes', 'shorts', 'sweater', 't_shirt', 'tank_top', 'umbrella', 'windbreaker', 'winter_coat']

class LaundryScreen extends Component{
  constructor(props) {
      super(props)
      this.state = {
        LaundryList: null,
        itemNum: ['...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...'],
        text: '0',
        id: this.props.id,
        token: this.props.token
      };
    }

  componentWillMount() {
    this.setState();
  }

  render(){
    return (
      <Image source={require('../../img/background/bg-mahogany.jpg')} style = {styles.backgroundImage}>
        <View style={styles.title}>
          {this.backButton()}
          <Text style={styles.titleText}>
            D i r t y   P i l e
          </Text>
          {this.invisBlock()}
        </View>
        <View style={styles.container}>
          <ScrollView>
            <View style={styles.closetItems}>
              {this.closet()}
            </View>
          </ScrollView>
        </View>
        <View style={styles.reset}>
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
        <ClosetItem key={i} type={itemType} amount={this.state.itemNum[i]} id={this.state.id} token={this.state.token} which={'laundry/'}/>
      )
    }
    return closetItems;
  }


  getCloset() {
    if (this.state.LaundryList == null) {
        fetch('https://dry-beyond-51182.herokuapp.com/api/v1/laundry/' + this.props.id, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'token': this.props.token
            }
          
          }).then((response) => response.json())
            .then((responseJson) => {
                this.setState({LaundryList: responseJson});
                {this.getNums()}
            })
        } 
  }

  getNums(){
    for (var i = 0; i < 15; i++) {
      var itemNum = this.state.itemNum.slice();
      itemNum[i] = (this.state.LaundryList[itemTypes[i]]);
      this.setState({ itemNum: itemNum });
        }
  }
  
  handleResetLaundryPress() {
    {this.componentWillMount()}
    fetch('https://dry-beyond-51182.herokuapp.com/api/v1/clean/' + this.state.id, {
      method: 'PUT',
      headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'token': this.state.token
      },
      body: JSON.stringify({
      })
    })

    fetch('https://dry-beyond-51182.herokuapp.com/api/v1/laundry/' + this.props.id, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'token': this.props.token
            }
          
          }).then((response) => response.json())
            .then((responseJson) => {
                this.setState({LaundryList: responseJson});
                {this.getNums()}
            })

    
  }

  resetLaundryButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={() => this.handleResetLaundryPress()}
      style={styles.resetButton}
      >
        <Text style={styles.resetButtonText}>
          RESET LAUNDRY
        </Text>

    </TouchableHighlight>
  }

  backButton() {
		return <TouchableHighlight
		underlayColor='transparent'
		onPress={() => this.handleBackPress()}
		style={styles.backButton}
		>
			<Image source={require('../../img/icon/left-arrow.png')} style = {styles.backImage} />

		</TouchableHighlight>
	}

	invisBlock() {
		return <TouchableHighlight
		style={styles.invisBlock}
		>
			<Text style={styles.updateButtonText}>
			INVIS
			</Text>

		</TouchableHighlight>
	}

  handleBackPress() {
    if(this.props.loc == 1){
      this.props.navigator.pop()
    } else {
      this.props.navigator.push({
        ident: "Recommendation",
        id: this.state.id,
        token: this.state.token
      })
    }
  }
};

var styles = StyleSheet.create({
  container: {
		height: 460
	},
	backgroundImage: {
    flex: 1,
    height: null,
    width: null,
    justifyContent: 'flex-start',
    alignItems: 'center',
		flexDirection: 'column'
		
    },
	title: {
		paddingTop: 20,
		paddingBottom: 20,
		flexDirection: 'row',
	},
	titleText: {
		flex: 1,
		fontSize: 36,
		fontWeight: '700',
		color: '#FFFFFF',
		textAlign:'center',
	},
	closetItems: {
		width: 300,
	},
  reset: {
		marginTop: 30,
		justifyContent: 'center',
		alignItems: 'center'
	},
	resetButton: {
		width: 150,
		height: 40,
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
	resetButtonText: {
    fontSize: 12,
    fontWeight: '600',
		color: '#FFFFFF'
  },
  backImage: {
		height: 30,
		width: 30,
    opacity: 0.7,
		tintColor: '#FFFFFF'
    },
	backButton: {
		width: 70,
		height: 30,
		justifyContent: 'center',
		alignItems: 'center',
		paddingLeft: 5,
		paddingTop: 15
	},
	invisBlock: {
		width: 70,
		height: 30,
		opacity: 0
	}
})

module.exports = LaundryScreen;
