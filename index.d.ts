declare module 'react-native-text-to-speech-edge' {
  import React from "react"
  export const createTextToSpeechByText = (text: string, voiceName: string, key:string, region: string) : Promise<{isInit: boolean}> => void
  export const createTextToSpeechBySSML = (ssml: string, voiceName: string, key:string, region: string) : Promise<{isInit: boolean}> => void
}