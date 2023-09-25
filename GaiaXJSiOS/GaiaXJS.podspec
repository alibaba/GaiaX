Pod::Spec.new do |s|

  s.name         = "GaiaXJS"
  s.version      = "0.1.0"
  s.summary      = "GaiaXJS"
  s.description  = "GaiaXJS"

  s.homepage     = "https://github.com/alibaba/GaiaX"


  s.author             = { "ronghui1219" => "zrhzhouronghui@qq.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/alibaba/GaiaX.git", :tag => "#{s.version}" }

  s.dependency  'GaiaXSocket'

  s.source_files = 'GaiaXJS/**/*.{h,m,mm,c}'
  s.resources = 'GaiaXJS/Resources/GaiaXJS.bundle'
  s.xcconfig = { "ENABLE_BITCODE" => "NO" }
  s.requires_arc = true
end
