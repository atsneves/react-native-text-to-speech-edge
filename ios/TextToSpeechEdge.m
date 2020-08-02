#import "TextToSpeechEdge.h"
#import <MicrosoftCognitiveServicesSpeech/SPXSpeechApi.h>

@implementation TextToSpeechEdge



RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(sampleMethod:(NSString *)stringArgument numberParameter:(nonnull NSNumber *)numberArgument callback:(RCTResponseSenderBlock)callback)
{
    // TODO: Implement some actually useful functionality
    callback(@[[NSString stringWithFormat: @"numberArgument: %@ stringArgument: %@", numberArgument, stringArgument]]);
}


- (NSDictionary*)synthesis:(NSString*)inputText withSSML:(NSString*)ssmlString withKey:(NSString*)speechKey andServiceRegion:(NSString*)serviceRegion andVoiceName:(NSString*)voiceName{
    SPXSpeechConfiguration *speechConfig = [[SPXSpeechConfiguration alloc] initWithSubscription:speechKey region:serviceRegion];
    [speechConfig setSpeechSynthesisOutputFormat:SPXSpeechSynthesisOutputFormat_Audio16Khz128KBitRateMonoMp3];
    [speechConfig setSpeechSynthesisVoiceName:[voiceName isEqualToString:@""] ? @"en-US-AriaNeural" : voiceName];

    SPXSpeechSynthesizer *speechSynthesizer = [[SPXSpeechSynthesizer alloc] initWithSpeechConfiguration:speechConfig audioConfiguration:nil];
    
    NSLog(@"Start synthesizing...");
    
    
    
    NSString *strSSML = ssmlString;
    
    NSLog(@"STRSSM = \n %@",strSSML);
    
    
    SPXSpeechSynthesisResult *speechResult;
    
    if ([ssmlString isEqualToString:@""])
        speechResult = [speechSynthesizer speakText:inputText];
    else
        speechResult = [speechSynthesizer speakSsml:ssmlString];
    
    // Checks result.
    if (SPXResultReason_Canceled == speechResult.reason) {
        SPXSpeechSynthesisCancellationDetails *details = [[SPXSpeechSynthesisCancellationDetails alloc] initFromCanceledSynthesisResult:speechResult];
        NSLog(@"Speech synthesis was canceled: %@. Did you pass the correct key/region combination?", details.errorDetails);
        return @{@"success": @(NO), @"errorMessage": details.errorDetails};
    } else if (SPXResultReason_SynthesizingAudioCompleted == speechResult.reason) {
        NSLog(@"Speech synthesis was completed");
        // Play audio.
        NSError *error;
        _player = [[AVAudioPlayer alloc] initWithData:[speechResult audioData] error:&error];
        if (error) {
            return @{@"success": @(NO), @"errorMessage": error.description};
        }
        [_player prepareToPlay];
        [_player play];
        return @{@"success": @(YES), @"errorMessage": @""};
    } else {
        NSLog(@"There was an error.");
        return @{@"success": @(NO), @"errorMessage": @"No Synthesis"};
    }
}


RCT_EXPORT_METHOD(createTextToSpeechByText:(NSString *)text withVoiceName:(nonnull NSString *)voiceName andKey:(NSString*)key andRegion:(NSString*)region resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    // TODO: Implement some actually useful functionality
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_DEFAULT, 0), ^{
       @try {
         NSDictionary *dictSyn = [self synthesis:text withSSML:@"" withKey:key andServiceRegion:region andVoiceName:voiceName];
           
         if ([[dictSyn objectForKey:@"success"] boolValue]) {
             return resolve(@(YES));
         } else {
             NSError *error = [NSError errorWithDomain:@"world" code:400 userInfo:@{ @"errorDetail" : [dictSyn objectForKey:@"errorMessage"]}];
             
             reject(@"no_events", [dictSyn objectForKey:@"errorMessage"], error);
         }
         
       }
       @catch (NSException * e) {
         NSError *error = [NSError errorWithDomain:e.name code:0 userInfo:@{
         NSUnderlyingErrorKey: e,
         NSDebugDescriptionErrorKey: e.userInfo ?: @{ },
         NSLocalizedFailureReasonErrorKey: (e.reason ?: @"???") }];
         reject(@"no_events", @"There were no events", error);
       }
    });
}

RCT_EXPORT_METHOD(createTextToSpeechBySSML:(NSString *)ssml withVoiceName:(nonnull NSString *)voiceName andKey:(NSString*)key andRegion:(NSString*)region resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    // TODO: Implement some actually useful functionality
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_DEFAULT, 0), ^{
       @try {
         NSDictionary *dictSyn = [self synthesis:@"" withSSML:ssml withKey:key andServiceRegion:region andVoiceName:voiceName];
           
         if ([dictSyn objectForKey:@"success"]) {
             return resolve(@(YES));
         } else {
             NSError *error = [[NSError alloc] init];
             
             reject(@"no_events", [dictSyn objectForKey:@"errorMessage"], error);
         }
         
       }
       @catch (NSException * e) {
         NSError *error = [NSError errorWithDomain:e.name code:0 userInfo:@{
         NSUnderlyingErrorKey: e,
         NSDebugDescriptionErrorKey: e.userInfo ?: @{ },
         NSLocalizedFailureReasonErrorKey: (e.reason ?: @"???") }];
         reject(@"no_events", @"There were no events", error);
       }
    });
}


@end
