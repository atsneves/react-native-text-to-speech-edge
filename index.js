import { NativeModules } from 'react-native'

const { TextToSpeechEdge } = NativeModules;

export const { createTextToSpeechBySSML, createTextToSpeechByText } = TextToSpeechEdge

export default TextToSpeechEdge;

