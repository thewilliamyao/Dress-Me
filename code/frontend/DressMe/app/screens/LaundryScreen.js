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

class LaundryScreen extends Component{
  constructor(props) {
      super(props)
      this.state = {
        LaundryList: null,
        itemNum: ['...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...'],
        text: '0'
      };
    }

    // componentWillMount() {
    //  this.getCloset();
    // }

  render(){
    return <ScrollView style={styles.container}>
      <View style={styles.title}>
        <Text style={styles.titleText}>
          Laundry Basket
        </Text>
      </View>
      <View style={styles.closetItems}>
        {this.closet()}
      </View>
      <View style={styles.reset}>
        {this.resetLaundryButton()}
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

  // textBoxInput(number) {
  //  var value;
  //  if (!this.state.itemNum[number]) {
  //    value = "..."
  //    return (
  //          <TextInput
  //          style={styles.amount}
  //          onChangeText={(text) => this.setState({text})};
  //          value={value}/> 
  //         );
  //  } else {
  //    value = this.state.itemNum[number] + "";
  //      return (
  //        <TextInput
  //          style={styles.amount}
  //          onChangeText={(value) => this.setState({value})}
  //          value={value}/> 
  //      );
  //  }
  //   }

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
                console.log(responseJson.boots);
                this.setState({LaundryList: responseJson});
                console.log(this.state.LaundryList.boots);
                console.log(this.state.LaundryList);
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
    for (var i = 0; i < 15; i++) {
      var itemNum = this.state.itemNum.slice();
      itemNum[i] = (this.state.LaundryList[itemTypes[i]]);
      this.setState({ itemNum: itemNum });
        }
  }
  
  handleResetLaundryPress() {
    fetch('https://dry-beyond-51182.herokuapp.com/api/v1/clean/0', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      }
    })
  }

  resetLaundryButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={() => this.handleResetLaundryPress()}
      style={styles.resetButton}
      >
        <Text>
          Reset Laundry!
        </Text>

    </TouchableHighlight>
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
  },
  reset: {
    justifyContent: 'center',
    alignItems: 'center'
  },
  resetButton: {
    width: 200,
    height: 50,
    borderWidth: 2,
    borderColor: 'black',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
  },
})

module.exports = LaundryScreen;
