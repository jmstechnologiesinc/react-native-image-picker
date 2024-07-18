export type Callback = (response: ImagePickerResponse) => any;

export interface OptionsCommon {
  mediaType: 'photo';
  maxWidth?: number;
  maxHeight?: number;
  quality?: PhotoQuality;
  includeBase64?: boolean;
  includeExtra?: boolean;
  presentationStyle?:
    | 'currentContext'
    | 'fullScreen'
    | 'pageSheet'
    | 'formSheet'
    | 'popover'
    | 'overFullScreen'
    | 'overCurrentContext';
  assetRepresentationMode?: 'auto' | 'current' | 'compatible';
}

export interface ImageLibraryOptions extends OptionsCommon {
  selectionLimit?: number;
}

export interface Asset {
  base64?: string;
  uri?: string;
  width?: number;
  height?: number;
  originalPath?: string;
  fileSize?: number;
  type?: string;
  fileName?: string;
}

export interface ImagePickerResponse {
  didCancel?: boolean;
  errorCode?: ErrorCode;
  errorMessage?: string;
  assets?: Asset[];
}

export type PhotoQuality =
  | 0
  | 0.1
  | 0.2
  | 0.3
  | 0.4
  | 0.5
  | 0.6
  | 0.7
  | 0.8
  | 0.9
  | 1;

export type MediaType = 'photo' | 'video' | 'mixed';
export type ErrorCode = 'permission' | 'others';
