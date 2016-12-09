import React, { Component, PropTypes } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NavigatorIOS,
  TouchableHighlight,
  ScrollView,
  TextInput,
  Navigator
} from 'react-native';

export default class LoginScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'blah', text1: 'MEERP'}
    }
    render() {
        const routes = [
            {title: 'First Scene', index: 0},
            {title: 'Second Scene', index: 1},
        ];
        return (<View style = {styles.container}>
            <View style={styles.titleContainer}>
                {/*<TouchableHighlight
                  underlayColor="gray"
                  onPress={this.props.onBack}
                  style={styles.loginButton}>
                    <Text>
                      Back
                    </Text>
                </TouchableHighlight>
            */}
            </View>
            <View style={styles.loginContainer}>
                <TextInput
                    style={{height: 40, borderColor: 'gray', borderWidth: 1}}
                    onChangeText={(text) => this.setState({text})}
                    value={this.state.text}/>
                <TextInput
                    style={{height: 40, borderColor: 'gray', borderWidth: 1}}
                    onChangeText={(text1) => this.setState({text1})}
                    value={this.state.text1}/>
            </View>
            <View style={styles.buttonContainer}>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleLoginPress()}
                  style={styles.loginButton}>
                    <Text>
                      Login
                    </Text>

                </TouchableHighlight>
            </View>
        
        </View>
        );
    }

    handleLoginPress() {
        this.props.navigator.push({
            ident: "Recommendation"
        })
    }

    loginButton() {
        return <TouchableHighlight
          underlayColor="gray"
          //onPress={this.props.onForward}
          style={styles.loginButton}>
            <Text>
              Login
            </Text>

        </TouchableHighlight>
  }
}

{/*}
LoginScreen.propTypes = {
    title: PropTypes.string.isRequired,
    onForward: PropTypes.func.isRequired,
    onBack: PropTypes.func.isRequired
};
*/}

var styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'blue'
    },
    titleContainer: {
        flex: 1,
        backgroundColor: 'yellow',
        justifyContent: 'center',
        alignItems: 'center'
    },
    loginContainer: {
        flex: 1,
        backgroundColor: 'red',
        justifyContent: 'center',
        alignItems: 'center'
    },
    loginButton: {
        width: 100,
        height: 50,
        borderWidth: 2,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'red'
    },
    buttonContainer: {
        flex: 1,
        backgroundColor: 'white',
        justifyContent: 'center',
        alignItems: 'center'
    },
    buttonWrapper: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center'
    },
    textColor: {
        color: 'white'
    },
    input: {
        flex: 1,
        color: '#FFFFFF',
        height:40, 
        width: 40, 
        borderColor: 'gray', 
        color:'white', 
        borderWidth: 1,
        justifyContent: 'center',
        alignItems: 'center'
    }

})

module.exports = LoginScreen;