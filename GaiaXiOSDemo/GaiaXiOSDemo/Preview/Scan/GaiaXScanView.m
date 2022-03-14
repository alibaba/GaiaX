//
//  GaiaXScanView.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXScanView.h"
#import <AVFoundation/AVFoundation.h>

@interface GaiaXScanView ()<AVCaptureMetadataOutputObjectsDelegate, AVCaptureVideoDataOutputSampleBufferDelegate>

// 扫描识别区域范围
@property (nonatomic, assign) CGRect scanFrame;
// session
@property (nonatomic, strong) AVCaptureSession * session;


@end

@implementation GaiaXScanView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.scanFrame = frame;
        [self setupPreviewLayer];
    }
    return self;
}

- (void)setupPreviewLayer{
    AVAuthorizationStatus authorizationStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authorizationStatus == AVAuthorizationStatusDenied) {
        UILabel *descLabel  = [[UILabel alloc] initWithFrame:CGRectMake(0, 150, self.frame.size.width, 50)];
        descLabel.font = [UIFont boldSystemFontOfSize:18];
        descLabel.textAlignment = NSTextAlignmentCenter;
        descLabel.textColor = [UIColor blackColor];
        descLabel.text = NSLocalizedString(@"scan_toast", nil);
        [self addSubview:descLabel];
    } else {
        AVCaptureVideoPreviewLayer *layer = [AVCaptureVideoPreviewLayer layerWithSession:self.session];
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill;
        layer.frame = self.layer.bounds;
        [self.layer insertSublayer:layer atIndex:0];
    }
}

- (void)startScan{
    if (_session) {
        [_session startRunning];
    }
}

- (void)stopScan{
    if (_session) {
        [_session stopRunning];
    }
}


#pragma mark - AVCaptureMetadataOutputObjectsDelegate

//扫描完成回调
-(void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection{
    if (metadataObjects.count > 0){
        // 描完成
        AVMetadataMachineReadableCodeObject *metadataObject = [metadataObjects firstObject];
        if (self.delegate && [self.delegate respondsToSelector:@selector(didRecievedScanContent:)]) {
            [self.delegate didRecievedScanContent:metadataObject.stringValue];
        }
    }
}


#pragma mark - AVCaptureVideoDataOutputSampleBufferDelegate

//扫描亮度判断
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection{
    CFDictionaryRef metadataDict = CMCopyDictionaryOfAttachments(NULL, sampleBuffer, kCMAttachmentMode_ShouldPropagate);
    NSDictionary *metadata = [[NSMutableDictionary alloc] initWithDictionary:(__bridge NSDictionary *)metadataDict];
    CFRelease(metadataDict);
    
    //获取亮度
    NSDictionary *exifMetadata = [[metadata objectForKey:(NSString *)kCGImagePropertyExifDictionary] mutableCopy];
    float brightnessValue = [[exifMetadata objectForKey:(NSString *)kCGImagePropertyExifBrightnessValue] floatValue];
        
    //判断否有闪光灯
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    BOOL hasTorch = [device hasTorch];
    
    if((brightnessValue <0) && hasTorch) {
     // 打开闪光灯
//       [device lockForConfiguration:nil];
//       [device setTorchMode:AVCaptureTorchModeOn];
//       [device unlockForConfiguration];
     } else if((brightnessValue >0) && hasTorch) {
     // 关闭闪光灯
//       [device lockForConfiguration:nil];
//       [device setTorchMode:AVCaptureTorchModeOff];
//       [device unlockForConfiguration];
     }
    
}


#pragma mark - session

- (AVCaptureSession *)session{
    
    if (_session == nil){
        //获取摄像设备
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        //创建输入流
        AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:device error:nil];
        if (!input){
            return nil;
        }
        
        //二维码output
        AVCaptureMetadataOutput *output = [[AVCaptureMetadataOutput alloc] init];
        [output setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
        
        //设置采集扫描区域的比例
        CGFloat x = CGRectGetMinX(self.scanFrame) / CGRectGetWidth(self.frame);
        CGFloat y = CGRectGetMinY(self.scanFrame) / CGRectGetHeight(self.frame);
        CGFloat width = CGRectGetWidth(self.scanFrame) / CGRectGetWidth(self.frame);
        CGFloat height = CGRectGetHeight(self.scanFrame) / CGRectGetHeight(self.frame);
        output.rectOfInterest = CGRectMake(y, x, height, width);
        
        //亮度output
        AVCaptureVideoDataOutput *output2 = [[AVCaptureVideoDataOutput alloc] init];
        [output2 setSampleBufferDelegate:self queue:dispatch_get_main_queue()];
        
        //创建session，设置高质量采集率
        _session = [[AVCaptureSession alloc] init];
        [_session setSessionPreset:AVCaptureSessionPresetHigh];
        [_session addInput:input];
        [_session addOutput:output];
        [_session addOutput:output2];
        
        //设置扫码支持的编码格式，支持(条形码，二维码兼容)
        output.metadataObjectTypes = @[AVMetadataObjectTypeQRCode,
                                       AVMetadataObjectTypeEAN13Code,
                                       AVMetadataObjectTypeEAN8Code,
                                       AVMetadataObjectTypeCode128Code];
    }
    
    return _session;
}


@end
