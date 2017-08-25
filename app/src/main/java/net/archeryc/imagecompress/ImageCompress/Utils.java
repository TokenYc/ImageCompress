package net.archeryc.imagecompress.ImageCompress;

import java.io.File;
import java.io.IOException;

/**
 * Created by yc on 2017/8/25.
 */

public class Utils {
    public static void createNoMediaFile(String folderPath) {
        File file = new File(folderPath + ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
