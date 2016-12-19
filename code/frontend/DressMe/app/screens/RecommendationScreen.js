
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Navigator,
  Image,
  AlertIOS
} from 'react-native';
// var Rec = null
class RecommendationScreen extends Component {

  static recommendationJson = null;
  static temp = false;
  static choiceInt = 0;

  constructor(props) {
    super(props);
    this.state = {Rec: null, id: this.props.id, token: this.props.token, shouldClean: false};
  }
  
  componentDidMount() {
    this.requestClothing(0);
  }

  render() {
    return <Image source={require('../../img/background/bg-morning.jpg')} style = {styles.backgroundImage}>
    {/*View Below is Settings Bar*/}  
      <View style= {[styles.settingsContainer, styles.buttonWrapper, this.border('pink')]}>
        {this.settingsButton()}
      </View>
    {/*View Below is ratingButtons*/}
        {this.ratingButtons()}
    {/*View Below is Dress up Guy*/}
      <View style= {[styles.displayContainer, this.border('lime')]}>
        {this.middleDisplay()}
      </View>
    {/*View Below is Dress me Button*/}
      <View style= {[styles.dressMeContainer,styles.buttonWrapper, this.border('cyan')]}>
        {this.dressMeButton()}
      </View>
    </Image>
  }

  settingsButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={() => this.handleSettingsPress()}
      style={styles.settingsButtonS}
      >
        <Text>
          Settings
        </Text>

    </TouchableHighlight>
  }

  handleSettingsPress(){
    console.log("Pressed Settings")
    this.props.navigator.push({
            ident: "Recommendation",
            id: this.state.id,
            token: this.state.token
        })
    this.props.navigator.push({
            ident: "Settings",
            id: this.state.id,
            token: this.state.token
    }) 
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

  ratingButtons() {
    return (
      <View style= {[styles.timeContainer, this.border('red')]}>
        <Text style={styles.feedback}>
          -Feedback-
        </Text>
        <View style={styles.buttonWrapper}>
          <TouchableHighlight
            underlayColor="gray"
            onPress={() => this.handleRatings(-10)}
            style={styles.ratingButton}>
              <View style={styles.ratingButtonView}>
                <Image source={require('../../img/icon/too-cold.png')} style = {styles.ratingButtonImage} />
                <Text style = {styles.ratingButtonText}>
                  Too Cold
                </Text>
              </View>
          </TouchableHighlight>
          <TouchableHighlight
            underlayColor="gray"
            onPress={() => this.handleRatings(0)}
            style={styles.ratingButton}>
              <View style={styles.ratingButtonView}>
                <Image source={require('../../img/icon/perfect.png')} style = {styles.ratingButtonImage} />
                <Text style = {styles.ratingButtonText}>
                  Perfect
                </Text>
              </View>
          </TouchableHighlight>
          <TouchableHighlight
            underlayColor="gray"
            onPress={() => this.handleRatings(10)}
            style={styles.ratingButton}>
              <View style={styles.ratingButtonView}>
                <Image source={require('../../img/icon/too-hot.png')} style = {styles.ratingButtonImage} />
                <Text style = {styles.ratingButtonText}>
                  Too Hot
                </Text>
              </View>
          </TouchableHighlight>
        </View>
    </View>
    )
  }

  handleRatings(rate) {
    console.log(rate)
    fetch('https://dry-beyond-51182.herokuapp.com/api/v1/feedback' + this.state.id, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'token': this.state.token
        },
        body: JSON.stringify({
            top: this.state.Rec.top,
            pants:  this.state.Rec.pants,
            footwear:  this.state.Rec.footwear,
            accessory: this.state.Rec.accessory,
            outerwear: this.state.Rec.outerwear,
            adjustment: rate
        })
      })
  }

  handleRateMePress(){
    console.log("Pressed Rate")
    this.props.navigator.push({
            ident: "Rate"
        })
  }
  
  requestClothing(option) {
    console.log(this.state.id);
    if (this.recommendationJson == null) {
      fetch('https://dry-beyond-51182.herokuapp.com/api/v1/recommendation/' + this.state.id, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'token': this.state.token
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
      fetch('https://dry-beyond-51182.herokuapp.com/api/v1/dirty/' + this.state.id, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'token': this.state.token
        },
        body: JSON.stringify({
            top: this.state.Rec.top,
            pants:  this.state.Rec.pants,
            footwear:  this.state.Rec.footwear,
            accessory: this.state.Rec.accessory,
            outerwear: this.state.Rec.outerwear
        })
      }).then((response) => response.json())
        .then((responseJson) => {
          this.temp = responseJson
          this.setState({shouldClean: responseJson});
          console.log(responseJson);
        })
      if(this.state.shouldClean) {
        console.log("We should do laundry")
        {this.sendAlert()}
        this.temp = false;
      }
  }

  sendAlert(){
    AlertIOS.alert(
     'Update Time',
     'Please do your Laundry',
     [
       {text: 'Cancel', onPress: () => console.log('Cancel Pressed'), style: 'cancel'},
       {text: 'Do Laundry', onPress: () => {this.doLaundry()}},
     ],
    );   
  }

  doLaundry(){
    this.props.navigator.push({
     ident: "Laundry",
     id: this.state.id,
     token: this.state.token
    })    
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
  backgroundImage: {
    flex: 1,
    height: null,
    width: null,
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
  },
  ratingButton: {
    width: 60,
    height: 60,
    justifyContent: 'center',
    alignItems: 'center'
  },
  ratingButtonView: {
    justifyContent: 'center',
    alignItems: 'center'
  },
  ratingButtonImage: {
		height: 30,
		width: 30,
  },
  ratingButtonText: {
    fontSize: 12,
    fontWeight: '600',
    textAlign: 'center'
  },
  feedback: {
    paddingTop: 5,
    textAlign: 'center'
  }
});

module.exports = RecommendationScreen;
{/*AppRegistry.registerComponent('Recommendation', () => Recommendation);*/}
