package br.com.atsneves;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import java.util.Dictionary;

import static com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat.Audio16Khz128KBitRateMonoMp3;




public class TextToSpeechEdgeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

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


    public ReturnSpeak synthesis(String inputText, String ssmlText, String speechSubscriptionKey, String serviceRegion, String voiceName) {
        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        speechConfig.setSpeechSynthesisVoiceName(voiceName.isEmpty() ? "en-US-AriaNeural" : voiceName );
        assert(speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig);
        assert(synthesizer != null);



        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result;
            if (inputText.isEmpty()) {
                result = synthesizer.SpeakSsml(ssmlText);
            } else {
                result = synthesizer.SpeakText(inputText);
            }
            assert(result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                result.close();

                return new ReturnSpeak(true, "");

            }
            else if (result.getReason() == ResultReason.Canceled) {
                String cancellationDetails =
                        SpeechSynthesisCancellationDetails.fromResult(result).toString();


                result.close();
                return new ReturnSpeak(false, cancellationDetails);
            }

            result.close();
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

}
