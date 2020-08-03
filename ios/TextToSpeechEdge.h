#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#import <AVFoundation/AVFoundation.h>

@interface TextToSpeechEdge : RCTEventEmitter <RCTBridgeModule,AVAudioPlayerDelegate>

@property (nonatomic, strong) AVAudioPlayer *player;

@end
