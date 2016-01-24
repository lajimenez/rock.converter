package org.rockjs.utils;

import java.io.File;

public class FileUtils {
    
    public static final String getFileName(String path) {
        String fileName = path;
        fileName = fileName.replace("\\", File.separator);
        fileName = fileName.replace("/", File.separator);
        File textureFile = new File(fileName);
        return textureFile.getName();
    }
}
