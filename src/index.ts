import {Platform} from 'react-native';

import {ImageLibraryOptions, Callback} from './types';
import {imageLibrary as nativeImageLibrary} from './platforms/native';
import {imageLibrary as webImageLibrary} from './platforms/web';

export * from './types';

export function launchImageLibrary(
  options: ImageLibraryOptions,
  callback?: Callback,
) {
  return Platform.OS === 'web'
    ? webImageLibrary(options, callback)
    : nativeImageLibrary(options, callback);
}
