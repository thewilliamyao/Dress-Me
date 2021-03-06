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

class ClosetSetup extends Component{
    static closetJson = null;

    constructor(props) {
        super(props)
        this.state = {
          ClosetList: null,
          itemNum: ['...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...', '...'],
          text: '0',
          id: this.props.id,
          token: this.props.token,
          buttonWidth: 140,
          buttonHeight: 40,
          buttonMarginTop: 0,
          buttonMarginLeft: 0,
        };
    }

    render(){
        return (
        <Image source={require('../../img/background/bg-mahogany.jpg')} style = {styles.backgroundImage}>
            <View style={styles.title}>
                {this.invisBlock()}
                <Text style={styles.titleText} >
                    S e t u p
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
            closetItems.push(
                <ClosetItem style={styles.closetElement} key={i} type={itemType} amount={this.state.itemNum[i]} id={this.state.id} token={this.state.token} which={'closet/'} />
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
                    this.setState({ClosetList: responseJson});
                    {this.getNums()}
                })
        }   
    }

    getNums(){
        for (var i = 0; i < 15; i++) {
            var itemNum = this.state.itemNum.slice();
            itemNum[i] = (this.state.ClosetList[itemTypes[i]]);
            this.setState({ itemNum: itemNum });
        }
    }

    resetLaundryButton() {
        return <TouchableHighlight
        underlayColor="gray"
        onPress={() => this.handleSetupPress()}
        style={[styles.updateButton,
             {width: this.state.buttonWidth, 
                 height: this.state.buttonHeight, 
                 marginTop: this.state.buttonMarginTop, 
                 marginLeft: this.state.buttonMarginLeft}]}
        >
            <Text style={styles.updateButtonText}>
            SETUP CLOSET
            </Text>

        </TouchableHighlight>
    }

    setupButton() {
        return <TouchableHighlight
        underlayColor='transparent'
        onPress={() => this.handleSetupPress()}
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

    handleSetupPress() {
        this.props.navigator.push({
                ident: "Recommendation",
                id: this.state.id,
                token: this.state.token,
                transition: -1
          })
    }

    handleUpdatePress() {
        console.log(this.state.itemNum[0]);
        fetch('https://dry-beyond-51182.herokuapp.com/api/v1/closet/' + this.state.id, {
          method: 'PUT',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'token': this.state.token
          },
          body: JSON.stringify({
            boots: this.state.itemNum[0],
            hoodie: this.state.itemNum[1],
            long_pants: this.state.itemNum[2],
            long_sleeve: this.state.itemNum[3],
            rain_jacket: this.state.itemNum[4],
            sandals: this.state.itemNum[5],
            scarf: this.state.itemNum[6],
            shoes: this.state.itemNum[7],
            shorts: this.state.itemNum[8],
            sweater: this.state.itemNum[9],
            t_shirt: this.state.itemNum[10],
            tank_top: this.state.itemNum[11],
            umbrella: this.state.itemNum[12],
            windbreaker: this.state.itemNum[13],
            winter_coat: this.state.itemNum[14]
          })
        })
    }
};

var styles = StyleSheet.create({
    container: {
        height: 510
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
        justifyContent: 'center',
        alignItems: 'center',
        flexDirection: 'row',        
    },
    titleText: {
        flex: 1,
        fontSize: 36,
        paddingLeft: 10,
        fontWeight: '700',
        color: '#FFFFFF',
        textAlign:'center',
        justifyContent: 'space-around'
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
        color: '#FFFFFF',
        backgroundColor: '#000000',
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

module.exports = ClosetSetup;
