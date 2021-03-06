
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
  AlertIOS,
  LayoutAnimation
} from 'react-native';
class RecommendationScreen extends Component {

  static recommendationJson = null;
  static temp = false;
  static choiceInt = 0;

  constructor(props) {
    super(props);
    this.state = {Rec: null, 
      id: this.props.id, 
      token: this.props.token, 
      shouldClean: false, 
      recOpacity: 0, 
      top: null, 
      pants: null, 
      footwear: null, 
      accessory: null, 
      outerwear: null,
      dressMeOpacity: 1,
      ratingOpacity: 1};
  }

  componentWillUpdate() {
    LayoutAnimation.easeInEaseOut();
  }
  
  fadeInRecommendation() {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.linear);
    this.setState({recOpacity: 1})
  }

  fadeOutDressMe() {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.spring);
    this.setState({dressMeOpacity: 0})
  }

  fadeOutRating() {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.spring);
    this.setState({ratingOpacity: 0})
  }

  componentDidMount() {
    this.requestClothing(0);
  }

  componentWillMount() {
    console.log("component will mount")
    this.forceUpdate();
  }

  render() {
    return <Image source={require('../../img/background/bg-morning-tint.png')} style = {styles.backgroundImage}>
    {/*View Below is Settings Bar*/}  
      <View style= {styles.settingsContainer}>
        {this.settingsButton()}
      </View>
    {/*View Below is ratingButtons*/}
        {this.ratingButtons()}
    {/*View Below is Dress up Recommendations*/}
      <View style= {styles.displayContainer}>
        {this.middleDisplay()}
      </View>
    {/*View Below is Dress me Button*/}
      <View style= {[styles.dressMeContainer,styles.buttonWrapper]}>
        {this.dressMeButton()}
      </View>
    </Image>
  }

  settingsButton() {
    return <View style={styles.settingsContainer}>
      <View style={styles.topWrapper}>
        <Text style={styles.topBarString}></Text>
          <TouchableHighlight
          underlayColor='transparent'
          onPress={() => this.handleSettingsPress()}
          style={styles.topBarButton}>
            <View style={styles.topBarButtonView}>
              <Image source={require('../../img/icon/settings-black.png')} style = {styles.topBarButtonImage} />
            </View>
          </TouchableHighlight>
        </View>
      </View>
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
    return <View style={styles.displayContainer}>
      <View style={[styles.midLeft, styles.arrowWrapper]}>
        {this.arrowLeftButton()}
      </View>

      <View style={styles.midMid}>
        {this.middleContent()}
      </View>

      <View style={[styles.midRight, styles.arrowWrapper]}>
        {this.arrowRightButton()}
      </View>
    </View>
  }

  middleContent() {
    if (this.state.Rec == null) {
      return (
        <View style={styles.contentText}>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>Loading...</Text>
        </View>
      )
    } else {
      return (
        <View style={[styles.contentText, {opacity: this.state.recOpacity}]}>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>{this.state.top}</Text>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>{this.state.pants}</Text>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>{this.state.footwear}</Text>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>{this.state.accessory}</Text>
          <Text style={[styles.prettyRecText, {color: '#FFFFFF'}]}>{this.state.outerwear}</Text>
        </View>
      )
    }
  }

  arrowLeftButton(){
    return <TouchableHighlight
      underlayColor='transparent'
      onPress = {() => this.requestClothing(-1)}
      style={styles.navButton}>
      <Image source={require('../../img/icon/left-arrow.png')} style = {styles.navImage} />
    </TouchableHighlight>
  }

  arrowRightButton(){
    return <TouchableHighlight
      underlayColor='transparent'
      onPress = {() => this.requestClothing(1)}
      style={styles.navButton}>
      <Image source={require('../../img/icon/left-arrow.png')} style = {[styles.navImage, styles.forwardButton]} />
    </TouchableHighlight>
  }

  dressMeButton() {
    return <TouchableHighlight
      underlayColor="gray"
      onPress={() => this.handleDressMePress()}
      style={[styles.optionButton, {opacity: this.state.dressMeOpacity}]}>
        <View style={styles.optionButtonView}>
          <Text style={styles.optionButtonText}>
            Dress Me!
          </Text>
        </View>
    </TouchableHighlight>
  }

  ratingButtons() {
    return (
      <View style= {[styles.timeContainer, {opacity: this.state.ratingOpacity}]}>
        <Text style={styles.feedback}>
          -Feedback-
        </Text>
        <View style={styles.buttonWrapper}>
          <TouchableHighlight
            underlayColor='transparent'
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
            underlayColor='transparent'
            onPress={() => this.handlePerfectRating()}
            style={styles.ratingButton}>
              <View style={styles.ratingButtonView}>
                <Image source={require('../../img/icon/perfect.png')} style = {styles.ratingButtonImage} />
                <Text style = {styles.ratingButtonText}>
                  Perfect
                </Text>
              </View>
          </TouchableHighlight>
          <TouchableHighlight
            underlayColor='transparent'
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

  handlePerfectRating() {
    this.fadeOutRating();
    this.handleRatings(0);
  }

  handleRatings(rate) {
    if (this.state.ratingOpacity != 0) {
      console.log(rate)
      fetch('https://dry-beyond-51182.herokuapp.com/api/v1/feedback/' + this.state.id, {
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
        }).then((response) => {
          console.log('hey you')
          this.requestClothing(-10);
        })
    }
  }
  
  requestClothing(option) {
    console.log(this.state.id);
    if (this.recommendationJson == null || option == -10) {
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
          console.log(responseJson.FirstRecommendation);
          this.setState({Rec: responseJson.FirstRecommendation});
          this.setStrings();
          this.fadeInRecommendation();
          {this.componentWillMount()}
        })
    } else {
      if ((this.choiceInt != 1 && option < 0) || (this.choiceInt != 3 && option > 0)) {
        this.choiceInt = this.choiceInt + option;
        if (this.choiceInt == 1) {
          if (this.recommendationJson.FirstRecommendation != null){
            this.setState({Rec: this.recommendationJson.FirstRecommendation});
            this.setState({top: this.recommendationJson.FirstRecommendation.top.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({pants: this.recommendationJson.FirstRecommendation.pants.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({footwear: this.recommendationJson.FirstRecommendation.footwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({accessory: this.recommendationJson.FirstRecommendation.accessory.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({outerwear: this.recommendationJson.FirstRecommendation.outerwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.fadeInRecommendation();
          }
        } else if (this.choiceInt == 2) {
          if (this.recommendationJson.SecondRecommendation != null){
            this.setState({Rec: this.recommendationJson.SecondRecommendation});
            this.setState({top: this.recommendationJson.SecondRecommendation.top.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({pants: this.recommendationJson.SecondRecommendation.pants.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({footwear: this.recommendationJson.SecondRecommendation.footwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({accessory: this.recommendationJson.SecondRecommendation.accessory.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({outerwear: this.recommendationJson.SecondRecommendation.outerwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.fadeInRecommendation();
          }
        } else {
          if (this.recommendationJson.ThirdRecommendation != null){
            this.setState({Rec: this.recommendationJson.ThirdRecommendation});
            this.setState({top: this.recommendationJson.ThirdRecommendation.top.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({pants: this.recommendationJson.ThirdRecommendation.pants.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({footwear: this.recommendationJson.ThirdRecommendation.footwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({accessory: this.recommendationJson.ThirdRecommendation.accessory.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.setState({outerwear: this.recommendationJson.ThirdRecommendation.outerwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
              return letter.toUpperCase();})});
            this.fadeInRecommendation();
          }
        }
      }
    }
  }

  handleDressMePress() {
    if (this.dressMeOpacity != 0) {
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
          this.fadeOutDressMe();
          this.requestClothing(-10);
        }).then(() => {
          if(this.state.shouldClean) {
            {this.sendAlert()}
            this.temp = false;
          }
        })
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
     token: this.state.token,
     loc: -1
    })    
  }

  setStrings() {
    this.setState({top: this.state.Rec.top.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
      return letter.toUpperCase();})});
    this.setState({pants: this.state.Rec.pants.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
      return letter.toUpperCase();})});
    this.setState({footwear: this.state.Rec.footwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
      return letter.toUpperCase();})});
    this.setState({accessory: this.state.Rec.accessory.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
      return letter.toUpperCase();})});
    this.setState({outerwear: this.state.Rec.outerwear.replace(/_/i, ' ').replace(/\b[a-z]/g, function(letter) {
      return letter.toUpperCase();})});
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
    flex: 2,
  },
  topWrapper: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  displayContainer: {
    flex: 9,
    flexDirection: 'row',
    marginTop: 10
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
  forwardButton: {
    transform: [
      {rotate: '180deg'}
    ]
  },
  linearGradient: {
    flex: 1,
    paddingLeft: 15,
    paddingRight: 15,
    borderRadius: 5,
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
    flexDirection: 'column',
    justifyContent: 'space-between'
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
    tintColor: 'white',
  },
  ratingButtonText: {
    fontSize: 12,
    fontWeight: '600',
    textAlign: 'center',
    color: 'white',
    shadowOffset:{
      width: 1,
      height: 1,
    },
    shadowColor: 'black',
    shadowOpacity: 0.3,
  },
  optionButton: {
    width: 150,
    height: 80,
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
    borderColor: '#FFFFFF',
    marginRight: 50,
    marginLeft: 50
  },
  optionButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
    backgroundColor: '#000000',
  },
  optionButtonView: {
    alignItems: 'center',
    justifyContent: 'center'
  },
  topBarButton: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  topBarButtonImage: {
    height: 30,
    width: 30,
    tintColor: 'white',
    opacity: 0.8
  },
  topBarButtonView: {
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 10,
    marginRight: 10
  },
  feedback: {
    opacity: 0,
    paddingTop: 5,
    textAlign: 'center'
  },
  outfit: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  topBarString: {
    fontSize: 20,
    color: 'white',
    marginTop: 25
  },
  prettyRecText: {
    flex: 1,
    fontSize: 30,
    fontWeight: '500',
    fontFamily: 'Helvetica',
    justifyContent: 'center',
    alignItems: 'center',
    textAlign: 'center',
    shadowOffset:{
      width: 2,
      height: 2,
    },
    shadowColor: '#111111',
    shadowOpacity: 0.5,
  },
  navImage: {
    height: 60,
    width: 60,
    tintColor: '#FFFFFF',
    },
  navButton: {
    width: 70,
    height: 70,
    justifyContent: 'center',
    alignItems: 'center',
    opacity: 0.8,
  }
});

module.exports = RecommendationScreen;
{/*AppRegistry.registerComponent('Recommendation', () => Recommendation);*/}
