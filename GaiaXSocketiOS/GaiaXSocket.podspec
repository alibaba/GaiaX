Pod::Spec.new do |s|

  s.name         = "GaiaXSocket"
  s.version      = "0.1.0"
  s.summary      = "GaiaXSocket"
  s.description  = "GaiaXSocket"


  s.homepage     = "https://github.com/alibaba/GaiaX"


  s.author       = { "jingcheng1988" => "zhang_jing_cheng@163.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/alibaba/GaiaX.git", :tag => "#{s.version}" }
  

  s.source_files = 'GaiaXSocket/**/*.{h,m}'
  s.xcconfig     = { "ENABLE_BITCODE" => "NO" }
  s.requires_arc = true

  s.dependency 'SocketRocket', '~> 0.5.1' 

  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }

end
