//
//  GXCssTest.swift
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

import XCTest
import GaiaXiOS

final class GXCssTest: XCTestCase {

    var bizeId = "Test"

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        GXRegisterCenter().registerTemplateService(withBizId: bizeId, templateBundle: "GaiaXiOSTests.bundle")
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    
    func testCompatibleEdgeInsets() throws {
        // This is an example of a performance test case.
        let item = GXTemplateItem()
        item.isLocal = true
        item.bizId = bizeId
        item.templateId = "template_css_compatible_edge_insets"
        
        let templateData = GXTemplateData()
        templateData.data = ["title": "title"]
        
        let rootView = GXTemplateEngine.sharedInstance().creatView(by: item, measure: CGSize(width: 100, height: 100))
        
        XCTAssertTrue(rootView != nil, "root view is nil")
        
        GXTemplateEngine.sharedInstance().bindData(templateData, on: rootView!)
        
        XCTAssertTrue(rootView is UICollectionView)
        
        let edgeInsets = (rootView as? UICollectionView)?.contentInset
        XCTAssertEqual(18, edgeInsets?.top)
        XCTAssertEqual(10, edgeInsets?.bottom)
        XCTAssertEqual(18, edgeInsets?.left)
        XCTAssertEqual(10, edgeInsets?.right)
    }
}
