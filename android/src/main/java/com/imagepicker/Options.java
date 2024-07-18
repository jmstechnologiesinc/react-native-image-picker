package com.imagepicker;

import com.facebook.react.bridge.ReadableMap;

public class Options {
    int selectionLimit;
    Boolean includeBase64;
    Boolean includeExtra;
    int quality;
    int maxWidth;
    int maxHeight;
    Boolean saveToPhotos;
    String mediaType;

    Options(ReadableMap options) {
        mediaType = options.getString("mediaType");
        selectionLimit = options.getInt("selectionLimit");
        includeBase64 = options.getBoolean("includeBase64");
        includeExtra = options.getBoolean("includeExtra");
        quality = (int) (options.getDouble("quality") * 100);
        maxHeight = options.getInt("maxHeight");
        maxWidth = options.getInt("maxWidth");
        // saveToPhotos = options.getBoolean("saveToPhotos");
    }
}
