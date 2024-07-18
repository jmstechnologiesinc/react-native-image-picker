import {NativeModules} from 'react-native';

import {ImageLibraryOptions, Callback, ImagePickerResponse} from '../types';

const DEFAULT_OPTIONS: ImageLibraryOptions = {
  mediaType: 'photo',
  quality: 1,
  maxWidth: 0,
  maxHeight: 0,
  includeBase64: false,
  includeExtra: false,
  presentationStyle: 'pageSheet',
  assetRepresentationMode: 'auto',
};

// @ts-ignore We want to check whether __turboModuleProxy exists, it may not
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const nativeImagePicker = isTurboModuleEnabled
  ? require('./NativeImagePicker').default
  : NativeModules.ImagePicker;

export function imageLibrary(
  options: ImageLibraryOptions,
  callback?: Callback,
): Promise<ImagePickerResponse> {
  return new Promise((resolve) => {
    nativeImagePicker.launchImageLibrary(
      {...DEFAULT_OPTIONS, ...options},
      (result: ImagePickerResponse) => {
        if (callback) callback(result);
        resolve(result);
      },
    );
  });
}
