Pod::Spec.new do |s|

  s.name          = "GaiaXiOS"
  s.version       = "0.1.0"
  s.platform      = :ios, "9.0"
  s.summary       = "dynamic template engine is a lightweight cross-end solution of pure native dynamic card"

  s.homepage      = "https://github.com/alibaba/GaiaX"

  s.author        = { "jingcheng.zjc" => "jingcheng.zjc@alibaba-inc.com" }
  s.source        = { :git => "https://github.com/alibaba/GaiaX.git", :tag => "#{s.version}" }
  
  s.source_files  = 'GaiaXiOS/GaiaXiOS/**/*.{h,m,mm,c}'
  s.xcconfig      = { "ENABLE_BITCODE" => "NO" }
  s.requires_arc  = true

  s.framework     = 'XCTest'

  s.vendored_libraries = 'GaiaXiOS/GaiaXiOS/Core/StretchKit/Libraries/libstretch.a'

  s.dependency 'YYText'

  s.dependency 'SDWebImage'

  s.dependency 'GaiaMotionCurve'


  s.pod_target_xcconfig = { 'VALID_ARCHS' => 'arm64 armv7 x86_64' }

  s.license      = { :type => 'Apache License, Version 2.0', :text => <<-LICENSE
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    LICENSE
  }

end
