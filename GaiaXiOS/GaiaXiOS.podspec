Pod::Spec.new do |s|

  s.name          = "GaiaXiOS"
  s.version       = "0.1.0"
  s.platform      = :ios, "9.0"
  s.license      = { :type => 'Apache License, Version 2.0' }
  s.summary       = "dynamic template engine is a lightweight cross-end solution of pure native dynamic card"

  s.homepage      = "https://github.com/alibaba/GaiaX"

  s.author        = { "jingcheng.zjc" => "jingcheng.zjc@alibaba-inc.com" }
  s.source        = { :git => "https://github.com/alibaba/GaiaX.git", :tag => "#{s.version}" }
  
  s.source_files  = 'GaiaXiOS/GaiaXiOS/**/*.{h,m,mm,c}'
  s.xcconfig      = { "ENABLE_BITCODE" => "NO" }
  s.requires_arc  = true

  s.vendored_libraries = 'GaiaXiOS/GaiaXiOS/Core/StretchKit/Libraries/libstretch.a'

end
