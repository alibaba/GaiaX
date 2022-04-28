//
//  GaiaValueParser.m
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/16.
//

#import "GXValueParser.h"

@interface GXValueParser ()

@property (nonatomic, copy) NSString *key;
@property (nonatomic, assign) NSInteger index;
@property (nonatomic, strong) GXValueParser *nextParser;

@end

@implementation GXValueParser

- (instancetype)init{
    if (self = [super init]) {
        _index = -1;
    }
    return self;
}

//生成表达式
+ (GXValueParser *)parserWithExpString:(NSString *)string{
    //默认值
    GXValueParser *parser = nil;
    if (string && [string isKindOfClass:[NSString class]] && string.length > 0) {
        //去除空格
        string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        //内容部分有效
        if (string.length) {
            if (![string hasPrefix:@"["]) {
                string = [@"." stringByAppendingString:string];
            }
            parser = [GXValueParser creatParserWithString:string];
        }
    }
    return parser;
}

//生成表达式
+ (GXValueParser *)creatParserWithString:(NSString *)string
{
    static NSCharacterSet *nextCharacterSet = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        nextCharacterSet = [NSCharacterSet characterSetWithCharactersInString:@"[."];
    });
    
    //默认值
    GXValueParser *parser = nil;
    //类型判断
    if ([string hasPrefix:@"["]) {
        //数组类型
        NSRange range = [string rangeOfString:@"]"];
        if (range.location != NSNotFound) {
            //获取index
            NSString *indexString = [string substringWithRange:NSMakeRange(1, range.location - 1)];
            parser = [[GXValueParser alloc] init];
            parser.index = [indexString integerValue];
            string = [string substringFromIndex:range.location + 1];
            
        } else {
            //不符合规则，置nil
            string = nil;
        }
        
    } else if ([string hasPrefix:@"."]) {
        //字典类型
        NSRange range = [string rangeOfCharacterFromSet:nextCharacterSet options:0 range:NSMakeRange(1, string.length - 1)];
        if (range.location != NSNotFound) {
            //获取key
            NSString *key = [string substringWithRange:NSMakeRange(1, range.location - 1)];
            parser = [[GXValueParser alloc] init];
            parser.key = key;
            string = [string substringFromIndex:range.location];
            
        } else {
            //获取key
            NSString *key = [string substringFromIndex:1];
            parser = [[GXValueParser alloc] init];
            parser.key = key;
            string = nil;
        }
    }
    
    //递归遍历
    if (parser && string && string.length) {
        parser.nextParser = [self creatParserWithString:string];
    }
    
    //返回表达式
    return parser;
}

//通过解析->解析object->返回value
- (id)valueWithObject:(id)object{
    if (object) {
        //取值
        id nextObject = nil;
        if (self.key.length && [object isKindOfClass:[NSDictionary class]]) {
            //dictionary
            NSDictionary *dict =(NSDictionary *)object;
            nextObject = [dict objectForKey:self.key];
            
        } else if (self.index >= 0 && [object isKindOfClass:[NSArray class]]) {
            //array
            NSArray *array = (NSArray *)object;
            if (self.index < array.count) {
                nextObject = [array objectAtIndex:self.index];
            }
        }
        
        //获取下一级
        if (self.nextParser) {
            return [self.nextParser valueWithObject:nextObject];
        } else {
            return nextObject;
        }
    }
    
    return nil;
}

@end
