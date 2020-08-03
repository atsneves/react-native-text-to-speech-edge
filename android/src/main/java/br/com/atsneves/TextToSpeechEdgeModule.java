package br.com.atsneves;

import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.StreamStatus;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.util.EventHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat.Audio16Khz128KBitRateMonoMp3;




public class TextToSpeechEdgeModule extends ReactContextBaseJavaModule{

    private final ReactApplicationContext reactContext;

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;
    private Future<SpeechSynthesisResult> result;
    private SpeechSynthesisEventArgs stopArgs;
    private Object stopObject;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public TextToSpeechEdgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "TextToSpeechEdge";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    private void playWav(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("audioText", "wav", reactContext.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.d("Audio Stop", mediaPlayer.toString());
                    sendEvent(reactContext, "ttedge-finish", null );
                }
            });


        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


    public ReturnSpeak synthesis(String inputText, String ssmlText, String speechSubscriptionKey, String serviceRegion, String voiceName) {
        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        speechConfig.setSpeechSynthesisVoiceName(voiceName.isEmpty() ? "en-US-AriaNeural" : voiceName );
        assert(speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig, null);
        assert(synthesizer != null);

        synthesizer.Synthesizing.addEventListener(new EventHandler<SpeechSynthesisEventArgs>() {
            @Override
            public void onEvent(Object o, SpeechSynthesisEventArgs speechSynthesisEventArgs) {
                Log.d("PlayerTesteAnderson", speechSynthesisEventArgs.toString());
                stopObject = o;
                stopArgs = speechSynthesisEventArgs;
            }
        });

        synthesizer.SynthesisStarted.addEventListener(new EventHandler<SpeechSynthesisEventArgs>() {
            @Override
            public void onEvent(Object o, SpeechSynthesisEventArgs speechSynthesisEventArgs) {
                Log.d("PlayerStarted", speechSynthesisEventArgs.toString());

            }
        });

        synthesizer.SynthesisCompleted.addEventListener(new EventHandler<SpeechSynthesisEventArgs>() {
            @Override
            public void onEvent(Object o, SpeechSynthesisEventArgs speechSynthesisEventArgs) {
                Log.d("SynthesisCompleted", speechSynthesisEventArgs.toString());

                try {
                    AudioDataStream stream = AudioDataStream.fromResult(speechSynthesisEventArgs.getResult());

                    if (stream.getStatus() == StreamStatus.AllData) {
                        playWav(speechSynthesisEventArgs.getResult().getAudioData());

                    }
                    Log.d("resultLoading", AudioDataStream.fromResult(speechSynthesisEventArgs.getResult()).getStatus().toString());

                    Log.d("getAudioData", speechSynthesisEventArgs.getResult().getAudioData().toString());

                    Log.d("getAudioLength", String.valueOf(speechSynthesisEventArgs.getResult().getAudioLength()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event

            if (inputText.isEmpty()) {
                result = synthesizer.SpeakSsmlAsync(ssmlText);
            } else {
                result = synthesizer.SpeakTextAsync(inputText);
            }

            return new ReturnSpeak(true, "");

        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }

        return new ReturnSpeak(false, "No synthesis");
    }

    @ReactMethod
    public void createTextToSpeechByText(String text, String voiceName, String key, String region, Promise promise) {
        try
        {
            // Initialize speech synthesizer and its dependencies
            ReturnSpeak speak = synthesis(text, "",key, region, voiceName);

            if (!speak.isSuccess()) {
                promise.reject("no_events", speak.getErrorMessage());
            } else {
                promise.resolve(true);
            }
        } catch (Exception error) {
            promise.reject("no_events", error.getMessage(),error.getCause());
        }
    }
    @ReactMethod
    public void stopEdge() {
        mediaPlayer.stop();
    }

    @ReactMethod
    public void createTextToSpeechBySSML(String ssml, String voiceName, String key, String region, Promise promise) {
        try
        {
            // Initialize speech synthesizer and its dependencies
            ReturnSpeak speak = synthesis("", ssml,key, region, voiceName);

            if (!speak.isSuccess()) {
                promise.reject("no_events", speak.getErrorMessage());
            } else {
                promise.resolve(true);
            }
        } catch (Exception error) {
            promise.reject("no_events", error.getMessage(),error.getCause());
        }
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

}
