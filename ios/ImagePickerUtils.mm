#import "ImagePickerUtils.h"
#import <CoreServices/CoreServices.h>
#import <PhotosUI/PhotosUI.h>

@implementation ImagePickerUtils

+ (void) setupPickerFromOptions:(UIImagePickerController *)picker options:(NSDictionary *)options target:(RNImagePickerTarget)target
{
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;

    if ([options[@"mediaType"] isEqualToString:@"photo"]) {
        picker.mediaTypes = @[(NSString *)kUTTypeImage];
    }

    picker.modalPresentationStyle = [RCTConvert UIModalPresentationStyle:options[@"presentationStyle"]];
}

+ (PHPickerConfiguration *)makeConfigurationFromOptions:(NSDictionary *)options target:(RNImagePickerTarget)target API_AVAILABLE(ios(14))
{
#if __has_include(<PhotosUI/PHPicker.h>)
    PHPickerConfiguration *configuration;
    
    if(options[@"includeExtra"]) {
        PHPhotoLibrary *photoLibrary = [PHPhotoLibrary sharedPhotoLibrary];
        configuration = [[PHPickerConfiguration alloc] initWithPhotoLibrary:photoLibrary];
    } else {
        configuration = [[PHPickerConfiguration alloc] init];
    }

    configuration.preferredAssetRepresentationMode = PHPickerConfigurationAssetRepresentationModeAutomatic;
    configuration.selectionLimit = [options[@"selectionLimit"] integerValue];

    if ([options[@"mediaType"] isEqualToString:@"photo"]) {
        configuration.filter = [PHPickerFilter imagesFilter];
    }
    return configuration;
#else
    return nil;
#endif
}

+ (BOOL) isSimulator
{
    #if TARGET_OS_SIMULATOR
        return YES;
    #endif
    return NO;
}

+ (NSString*) getFileType:(NSData *)imageData
{
    const uint8_t firstByteJpg = 0xFF;
    const uint8_t firstBytePng = 0x89;
    const uint8_t firstByteGif = 0x47;
    
    uint8_t firstByte;
    [imageData getBytes:&firstByte length:1];
    switch (firstByte) {
      case firstByteJpg:
        return @"jpg";
      case firstBytePng:
        return @"png";
      case firstByteGif:
        return @"gif";
      default:
        return @"jpg";
    }
}

+ (UIImage*)resizeImage:(UIImage*)image maxWidth:(float)maxWidth maxHeight:(float)maxHeight
{
    if ((maxWidth == 0) || (maxHeight == 0)) {
        return image;
    }
    
    if (image.size.width <= maxWidth && image.size.height <= maxHeight) {
        return image;
    }

    CGSize newSize = CGSizeMake(image.size.width, image.size.height);
    if (maxWidth < newSize.width) {
        newSize = CGSizeMake(maxWidth, (maxWidth / newSize.width) * newSize.height);
    }
    if (maxHeight < newSize.height) {
        newSize = CGSizeMake((maxHeight / newSize.height) * newSize.width, maxHeight);
    }
    
    newSize.width = (int)newSize.width;
    newSize.height = (int)newSize.height;

    UIGraphicsBeginImageContext(newSize);
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    if (newImage == nil) {
        NSLog(@"could not scale image");
    }
    UIGraphicsEndImageContext();

    return newImage;
}

+ (PHAsset *)fetchPHAssetOnIOS13:(NSDictionary<NSString *,id> *)info
{
    NSURL *referenceURL = [info objectForKey:UIImagePickerControllerReferenceURL];

    if(!referenceURL) {
      return nil;
    }

    // We fetch the asset like this to support iOS 10 and lower
    // see: https://stackoverflow.com/a/52529904/4177049
    PHFetchResult* fetchResult = [PHAsset fetchAssetsWithALAssetURLs:@[referenceURL] options:nil];
    return fetchResult.firstObject;
}

@end
