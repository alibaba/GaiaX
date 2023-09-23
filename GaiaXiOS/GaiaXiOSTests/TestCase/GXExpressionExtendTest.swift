//
//  GXExpressionExtendTest.swift
//  GaiaXiOSTests
//
//  Created by biezhihua on 2023/4/18.
//

import XCTest
import GaiaXiOS

class FunctionExtend: NSObject,GXFunctionExpressionProtocol {
    func execute(_ funName: String, params funParams: [Any]) -> Any? {
        if (funName == "gaiax" && funParams.count==1) {
            let params1 = String(describing:  funParams[0]);
            if (params1 == "screenWidth") {
                return 100.0;
            }else if (params1 == "screenHeight") {
                return 200.0;
            }
        }
        return nil;
    }
    
}

final class GXExpressionExtendTest: XCTestCase  {
    
    var bizeId = "Test"
    
    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        GXRegisterCenter.default().registerTemplateService(withBizId: bizeId, templateBundle: "GaiaXiOSTests.bundle")
    }
    
    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    
    func testEnvExt() throws {
        
        let extend=FunctionExtend();
        GXRegisterCenter.default().registerFunctionExpression(extend);
        
        let item = GXTemplateItem()
        item.isLocal = true
        item.bizId = bizeId
        item.templateId = "expression/template_env_ext"
        
        let templateData = GXTemplateData()
        templateData.data = ["test":"test"];
        
        let rootView = GXTemplateEngine.sharedInstance().creatView(by: item, measure: CGSize(width: 100, height: 100))
        
        GXTemplateEngine.sharedInstance().bindData(templateData, on: rootView!)
        
        let targetView = GXTemplateEngine.sharedInstance().queryView(byNodeId: "text", rootView: rootView!)
        
        XCTAssertEqual((targetView as? UILabel)?.text, String(describing: 100))
        
    }
    
    
}
