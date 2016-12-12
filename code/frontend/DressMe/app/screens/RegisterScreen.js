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

class RegisterScreen extends Component{
    constructor(props){
        super(props);
        this.state = {text: 'Email', text1: 'Password'}
    }
    render() {
        return (<View style = {styles.container}>
            <View style={styles.titleContainer}>
                <Text> Registration </Text>
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
                  onPress={() => this.handleBackPress()}
                  style={styles.loginButton}>
                    <Text>
                      Back 
                    </Text>
                </TouchableHighlight>
                <TouchableHighlight
                  underlayColor="gray"
                  onPress={() => this.handleSubmitPress()}
                  style={styles.loginButton}>
                    <Text>
                      Submit 
                    </Text>
                </TouchableHighlight>
            </View>
        
        </View>
        );
    }

    handleSubmitPress() {
        this.props.navigator.push({
            ident: "Recommendation"
        })
    }

    handleBackPress() {
        this.props.navigator.pop()
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
        justifyContent: 'space-around',
        alignItems: 'center',
        flexDirection: 'row'
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

module.exports = RegisterScreen;