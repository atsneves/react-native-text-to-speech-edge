require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-text-to-speech-edge"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-text-to-speech-edge
                   DESC
  s.homepage     = "https://github.com/atsneves/react-native-text-to-speech-edge"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Anderson Neves" => "atsneves@gmail.com" }
  s.platforms    = { :ios => "9.3" }
  s.source       = { :git => "https://github.com/atsneves/react-native-text-to-speech-edge.git", :tag => "#{s.version}" }
  s.ios.deployment_target  = '9.3'

  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.framework = 'AVFoundation'
  s.dependency "React"
  s.dependency "MicrosoftCognitiveServicesSpeech-iOS", "~> 1.9"
end

