package net.archeryc.imagecompress.ImageCompress;

import static net.archeryc.imagecompress.ImageCompress.ImageCompressUtils.Config.DEFAULT_OPTIONS;
import static net.archeryc.imagecompress.ImageCompress.ImageCompressUtils.Config.MAX_LONG_IMG_SIZE;
import static net.archeryc.imagecompress.ImageCompress.ImageCompressUtils.Config.MAX_SIZE;

/**
 * Created by yc on 2017/8/18.
 * 图片压缩方案
 */

public class ImageCompressUtils {

    public static class Config {
        public static final int MAX_SIZE = 1280;//最大宽高

        public static final int DEFAULT_OPTIONS = 90;

        public static final int LONG_IMAGE_RATIO = 3;

        public static final int MAX_LONG_IMG_LENGTH = 30000;

        public static final int MAX_LONG_IMG_SIZE = 200000000;
    }

    public static String compressImage(String inputFile, String outputFile, int options) {

        //如果是Gif不进行压缩
        if (BitmapUtils.getImageMineType(inputFile) == BitmapUtils.ImageType.TYPE_GIF) {
            return inputFile;
        }

        int targetOptions;
        if (options <= 0 || options > 100) {
            targetOptions = DEFAULT_OPTIONS;
        } else {
            targetOptions = options;
        }


        BitmapUtils.Size size = BitmapUtils.getImageSize(inputFile);
        float originWidth = (float) size.getWidth();
        float originHeight = (float) size.getHeight();

        int targetWidth;
        int targetHeight;

        if (originWidth > originHeight) {
            if (originWidth / originHeight > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是长图
                targetWidth= (int) originWidth;
                targetHeight= (int) originHeight;
                if (originWidth > Config.MAX_LONG_IMG_LENGTH) {//超过长宽限制
                    targetWidth=Config.MAX_LONG_IMG_LENGTH;
                    float ratio = originWidth / Config.MAX_LONG_IMG_LENGTH;
                    targetHeight=(int) (originHeight / ratio);
                }
                if (targetWidth*targetHeight>Config.MAX_LONG_IMG_SIZE){//超过大小限制
                    float ratio = targetWidth / targetHeight;
                    float every=Config.MAX_LONG_IMG_SIZE/(ratio+1);
                    targetWidth= (int) (every*ratio);
                    targetHeight= (int) every;
                }
            }else {
                if (originWidth <= MAX_SIZE) {
                    targetWidth = (int) originWidth;
                    targetHeight = (int) originHeight;
                } else {
                    float ratio = originWidth / MAX_SIZE;
                    targetWidth = MAX_SIZE;
                    targetHeight = (int) (originHeight / ratio);
                }
            }
        } else {
            if (originHeight / originWidth > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是长图
                targetWidth = (int) originWidth;
                targetHeight = (int) originHeight;
                if (originHeight > Config.MAX_LONG_IMG_LENGTH) {//超过长宽限制
                    targetHeight = Config.MAX_LONG_IMG_LENGTH;
                    float ratio = originHeight / Config.MAX_LONG_IMG_LENGTH;
                    targetWidth = (int) (originWidth / ratio);
                }
                if (targetWidth * targetHeight > Config.MAX_LONG_IMG_SIZE) {//超过大小限制
                    float ratio = targetHeight / targetWidth;
                    float every = Config.MAX_LONG_IMG_SIZE / (ratio + 1);
                    targetHeight = (int) (every * ratio);
                    targetWidth = (int) every;
                }
            }else {
                if (originHeight <= MAX_SIZE) {
                    targetWidth = (int) originWidth;
                    targetHeight = (int) originHeight;
                } else {
                    float ratio = originHeight / MAX_SIZE;
                    targetHeight = MAX_SIZE;
                    targetWidth = (int) (originWidth / ratio);
                }
            }
        }
        return BitmapUtils.compressImage(inputFile, outputFile, targetWidth, targetHeight, targetOptions);
    }

}
