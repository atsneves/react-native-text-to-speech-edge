# react-native-text-to-speech-edge

react-native-text-to-speech-edge component for React Native (iOS and Android)

react-native-text-to-speech-edge is a mobile library for Android and iOS to convert Text in Speech using [Speech Service Azure](https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/index-text-to-speech)!




## Installing (React Native >= 0.60.0)
`$ npm install react-native-text-to-speech-edge --save`

## For iOS Configuration
Go to your ios folder and run:
```
pod install
```

**_ IMPORTANT _**



The Project Running in iOS 9.3 or higher

## For Android Configuration
Open the AndroidManifest.xml and Verify your Permission
```
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```

Open the android/build.gradle and add repository of microsoft
```
allprojects {
    repositories {
        mavenLocal()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url("$rootDir/../node_modules/react-native/android")
        }
        maven {
            // Android JSC is installed from npm
            url("$rootDir/../node_modules/jsc-android/dist")
        }
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
        mavenCentral()
        maven {
            url 'https://csspeechstorage.blob.core.windows.net/maven/'
        }
    }
}
```

And change your min-sdk-version to 19
```
    ext {
        buildToolsVersion = "28.0.3"
        minSdkVersion = 19
        compileSdkVersion = 28
        targetSdkVersion = 28
    }
```

**_ IMPORTANT _**



The Project Android Needs Min Android 4.4 KitKat (API 19)


## Usage

To transform basic Text To Speech.
```javascript
import { createTextToSpeechByText } from 'react-native-text-to-speech-edge';


createTextToSpeechByText(
      'Your Text For Speech in Here',
      'pt-BR-FranciscaNeural', //Your Neural Language [https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support#neural-voices]
      'Your Key',
      'Your Region',
    )
      .then((success) => {
        // Your Success Boolean Return
      })
      .catch((error) => {
        // Your Error String Message
      });
  };
```

To transform Text To Speech using [SSML](https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/speech-synthesis-markup?tabs=csharp).
```javascript
import { createTextToSpeechByText } from 'react-native-text-to-speech-edge';


createTextToSpeechBySSML(
      'Your SSML String in Here',
      'pt-BR-FranciscaNeural', //Your Neural Language (https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support#neural-voices)
      'Your Key',
      'Your Region',
    )
      .then((success) => {
        // Your Success Boolean Return
      })
      .catch((error) => {
        // Your Error String Message
      });
  };
```

To STOP audio text
```javascript
import { stopEdge } from 'react-native-text-to-speech-edge';


stopEdge()
```

To listen on Audio Finish event
```javascript
import TTSEdge from 'react-native-text-to-speech-edge';


TTSEdge.addEventListener('ttedge-finish', (event) =>
  console.log('ttedge-finish', event) // Your Listener To Finish Audio
);
```
