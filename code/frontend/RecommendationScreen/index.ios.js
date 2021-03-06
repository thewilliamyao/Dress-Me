/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
//var formatTime = require('minutes-seconds-milliseconds');
// import LinearGradient from 'react-native-linear-gradient';
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} from 'react-native';
// var Rec = null
class RecommendationScreen extends Component {
  // constructor(props) {
  //   super(props);
  //   this.onChange = this.onChange.bind(this);
  //   this.state = {Rec : null};
  // }
  // componentDidMount() {
  //   Recommendation.listen(this.onChange);
  // }
  // componentWillUnmount() {
  //   Recommendation.unlisten(this.onChange);
  // }
  // onChange(state) {
  //   this.setState(state);
  // }
  static recommendationJson = null;
  static choiceInt = 0;

  constructor(props) {
    super(props);
    this.state = {Rec: null};
    // this.onChange = this.onChange.bind(this);
  }
  
  componentDidMount() {
    this.requestClothing(0);
  }
  // getInitialState() {
  //     This is run once when it is rendered
  //   return{
  //     Rec: null
  //   }
  // }

  render() {
    return <View style= {styles.container}>
    {/*View Below is Settings Bar*/}  
      <View style= {[styles.settingsContainer, styles.buttonWrapper, this.border('pink')]}>
        {this.settingsButton()}
      </View>
    {/*View Below is Time Bar*/}
      <View style= {[styles.timeContainer, this.border('red')]}>
        <Text>
          Hello there!!!!
        </Text>
      </View>
    {/*View Below is Dress up Guy*/}
      <View style= {[styles.displayContainer, this.border('lime')]}>
        {this.middleDisplay()}
      </View>
    {/*View Below is Dress me Button*/}
      <View style= {[styles.dressMeContainer,styles.buttonWrapper, this.border('cyan')]}>
        {this.dressMeButton()}
      </View>
    </View>
  }

  settingsButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={this.handleSettingsPress}
      style={styles.settingsButtonS}
      >
        <Text>
          Settings
        </Text>

    </TouchableHighlight>
  }

  handleSettingsPress(){
    console.log('Settings was pressed');
  }

  middleDisplay() {
    return <View style={[styles.displayContainer,this.border('lime')]}>
      <View style={[styles.midLeft, styles.arrowWrapper, this.border('orange')]}>
        {this.arrowLeftButton()}
      </View>

      <View style={[styles.midMid, this.border('orange')]}>
        {this.middleContent()}
      </View>

      <View style={[styles.midRight, styles.arrowWrapper, this.border('orange')]}>
        {this.arrowRightButton()}
      </View>
    </View>
  }

  middleContent() {
    if (!this.state.Rec) {
      return (
        <View>
          <Text>Loading...</Text>
        </View>
      )
    } else {
      return (
        <View style={styles.contentText}>
          <View>
            <Text style={styles.contentTextLeft}>Top</Text>
            <Text style={styles.contentTextLeft}>Pants</Text>
            <Text style={styles.contentTextLeft}>Footwear</Text>
            <Text style={styles.contentTextLeft}>Accessory</Text>
            <Text style={styles.contentTextLeft}>Outerwear</Text>
          </View>
          <View>
            <Text>: {this.state.Rec.top}</Text>
            <Text>: {this.state.Rec.pants}</Text>
            <Text>: {this.state.Rec.footwear}</Text>
            <Text>: {this.state.Rec.accessory}</Text>
            <Text>: {this.state.Rec.outerwear}</Text>
          </View>
        </View>
      )
    }
  }

  arrowLeftButton(){
    return <TouchableHighlight
      underlayColor="gray"
      // onPress={this.handleArrowLeft}
      onPress = {() => this.requestClothing(-1)}
      style= {[styles.triangle,styles.triangleRotLeft]}
      > 
        <Text>
          Back
        </Text>
    </TouchableHighlight>
  }

  handleArrowLeft(){
    console.log('Left Arrow was pressed');
  }

  arrowRightButton(){
    return <TouchableHighlight
      underlayColor="gray"
      // onPress={this.handleArrowRight}
      onPress = {() => this.requestClothing(1)}
      style= {[styles.triangleRotRight, styles.triangle]}
      > 
        <Text>
          Forward
        </Text>
    </TouchableHighlight>
  }

  handleArrowRight(){
    console.log('Right Arrow was pressed');
    this.requestClothing(-1);
  }

  dressMeButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={() => this.handleDressMePress()}
      style={styles.dressMeButton}
      >
        <Text>
          Dress Me!
        </Text>

    </TouchableHighlight>
  }
  
  requestClothing(option) {
    if (this.recommendationJson == null) {
      console.log('im here now');
      fetch('https://dry-beyond-51182.herokuapp.com/api/v1/recommendation/0', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
        
      }).then((response) => response.json())
        .then((responseJson) => {
          this.recommendationJson = responseJson;
          this.choiceInt = 1;
          this.setState({Rec: responseJson.FirstRecommendation});
        })
    } else {
      if ((this.choiceInt != 1 && option < 0) || (this.choiceInt != 3 && option > 0)) {
        this.choiceInt = this.choiceInt + option;
        if (this.choiceInt == 1) {
          this.setState({Rec: this.recommendationJson.FirstRecommendation});
        } else if (this.choiceInt == 2) {
          this.setState({Rec: this.recommendationJson.SecondRecommendation});
        } else {
          this.setState({Rec: this.recommendationJson.ThirdRecommendation});
        }
      }
    }
  }

  handleDressMePress() {
    console.log('Dress Me was pressed');
  }

  tempRender(){
    <View>
            <Text>
              TRYING TO RERENDER
            </Text>
    </View>

  }

  border(color){
    return {
      borderColor: color,
      borderWidth: 4
    }
  }
}

var styles = StyleSheet.create({
  container: {
    flex: 1
  },
  settingsContainer: {
    flex: 1
  },
  timeContainer: {
    flex: 2
  },
  displayContainer: {
    flex: 9,
    flexDirection: 'row'
  },
  dressMeContainer: {
    flex: 3
  },
  midLeft: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  midMid: {
    justifyContent: 'center',
    alignItems: 'center',
    flex: 3
  },
  midRight: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  dressMeButton: {
    width: 100,
    height: 50,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'red'
  },
  settingsButtonS: {
    width: 100,
    height: 25,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'red'
  },
  buttonWrapper: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center'
  },
  arrowWrapper: {
    flexDirection: 'column',
    justifyContent: 'space-around',
    alignItems: 'center'
  },
  triangle: {
    width: 0,
    height: 0,
    backgroundColor: 'transparent',
    borderStyle: 'solid',
    borderLeftWidth: 25,
    borderRightWidth: 25,
    borderBottomWidth: 50,
    borderLeftColor: 'transparent',
    borderRightColor: 'transparent',
    borderBottomColor: 'red',
    justifyContent: 'center',
    alignItems: 'center',
  },
  triangleRotRight: {
    transform: [
      {rotate: '90deg'}
    ]
  },
  triangleRotLeft: {
    transform: [
      {rotate: '-90deg'}
    ]
  },
  linearGradient: {
    flex: 1,
    paddingLeft: 15,
    paddingRight: 15,
    borderRadius: 5,
    //backgroundColor: 'cyan'
  },
  buttonText: {
    fontSize: 18,
    fontFamily: 'Gill Sans',
    textAlign: 'center',
    margin: 10,
    color: '#ffffff',
    backgroundColor: 'transparent',
  },
  contentText: {
    flexDirection: 'row',
  },
  contentTextLeft: {
     textAlign: 'right',
  }
});

AppRegistry.registerComponent('RecommendationScreen', () => RecommendationScreen);
