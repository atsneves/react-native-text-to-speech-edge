import { NativeModules, NativeEventEmitter } from 'react-native'

const { TextToSpeechEdge } = NativeModules;

export const { createTextToSpeechBySSML, createTextToSpeechByText, stopEdge } = TextToSpeechEdge


class TSEdge extends NativeEventEmitter {
    constructor() {
        super(TextToSpeechEdge);
        console.log('this', TextToSpeechEdge);
      }
    
      addEventListener(type, handler) {
        return this.addListener(type, handler);
      }
    
      removeEventListener(type, handler) {
        this.removeListener(type, handler);
      }
} 

export default new TSEdge();

