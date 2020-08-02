#import <React/RCTBridgeModule.h>
#import <AVFoundation/AVFoundation.h>

@interface TextToSpeechEdge : NSObject <RCTBridgeModule>

@property (nonatomic, strong) AVAudioPlayer *player;

@end
