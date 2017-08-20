package net.archeryc.imagecompress.ImageCompress;

/**
 * Created by yc on 2017/8/18.
 * 图片压缩方案
 */

public class ImageCompressUtils {

    public static final int MAX_SIZE = 1280;//最大宽高

    public static final int DEFAULT_OPTIONS = 90;

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
        int originWidth = size.getWidth();
        int originHeight = size.getHeight();

        int targetWidth;
        int targetHeight;


        if (originWidth > originHeight) {
            if (originWidth <= MAX_SIZE) {
                targetWidth = originWidth;
                targetHeight = originHeight;
            } else {
                float ratio = (float) originWidth / MAX_SIZE;
                targetWidth = MAX_SIZE;
                targetHeight = (int) (originHeight / ratio);
            }
        } else {
            if (originHeight <= MAX_SIZE) {
                targetWidth = originWidth;
                targetHeight = originHeight;
            } else {
                float ratio = (float) originHeight / MAX_SIZE;
                targetHeight = MAX_SIZE;
                targetWidth = (int) (originWidth / ratio);
            }
        }
        return BitmapUtils.compressImage(inputFile, outputFile, targetWidth, targetHeight, targetOptions);
    }

}
