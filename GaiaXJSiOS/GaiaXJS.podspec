Pod::Spec.new do |s|

  s.name         = "GaiaXJS"
  s.version      = "0.0.1"
  s.summary      = "GaiaXJS"
  s.description  = "GaiaXJS"
  s.platform     = :ios, "9.0"

  s.homepage     = "https://github.com/alibaba/GaiaX"

  s.author       = { "ronghui1219" => "zrhzhouronghui@qq.com" }
  s.source       = { :git => "https://github.com/alibaba/GaiaX.git", :tag => "#{s.version}" }

  s.dependency  'GaiaXSocket'

  s.source_files = 'GaiaXJS/**/*.{h,m,mm,c}'
  s.resources = 'GaiaXJS/Resources/GaiaXJS.bundle'
  s.xcconfig = { "ENABLE_BITCODE" => "NO" }
  s.requires_arc = true
  
end
