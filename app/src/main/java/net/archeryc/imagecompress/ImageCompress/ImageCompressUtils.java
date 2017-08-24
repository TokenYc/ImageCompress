package net.archeryc.imagecompress.ImageCompress;

import static net.archeryc.imagecompress.ImageCompress.ImageCompressUtils.Config.DEFAULT_OPTIONS;
import static net.archeryc.imagecompress.ImageCompress.ImageCompressUtils.Config.MAX_SIZE;

/**
 * Created by yc on 2017/8/18.
 * 图片压缩方案
 */

public class ImageCompressUtils {

    public static class Config {

        public static final int MAX_SIZE = 1280;//普通图片最大宽高

        public static final int DEFAULT_OPTIONS = 90;//默认压缩质量压缩比

        public static final int LONG_IMAGE_RATIO = 3;//长图，宽图比例基准

        public static final int MAX_LONG_IMG_HEIGHT = 10000;//长图长度上限

        public static final int MAX_LONG_IMG_WIDTH = 2048;//宽图宽度上限

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

        float originWidth = (float) size.getWidth();//原始宽
        float originHeight = (float) size.getHeight();//原始高

        float targetWidth;//最终宽
        float targetHeight;//最终高

        if (originWidth > originHeight) {
            if (originWidth / originHeight > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是宽图
                if (originWidth > Config.MAX_LONG_IMG_WIDTH) {//超过最长宽限制，需要压缩宽高
                    targetWidth = Config.MAX_LONG_IMG_WIDTH;
                    float ratio = originWidth / Config.MAX_LONG_IMG_WIDTH;
                    targetHeight = (originHeight / ratio);
                } else {
                    targetWidth = originWidth;//默认原图宽高
                    targetHeight = originHeight;
                }
            } else {
                if (originWidth <= MAX_SIZE) {//不超过普通图片最长宽限制，原图大小
                    targetWidth = originWidth;
                    targetHeight = originHeight;
                } else {//超过限制，等比缩放到宽为限制值
                    float ratio = originWidth / MAX_SIZE;
                    targetWidth = MAX_SIZE;
                    targetHeight = (originHeight / ratio);
                }
            }
        } else {
            if (originHeight / originWidth > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是长图
                if (originHeight > Config.MAX_LONG_IMG_HEIGHT) {//超过最大长度限制
                    targetHeight = Config.MAX_LONG_IMG_HEIGHT;
                    float ratio = originHeight / Config.MAX_LONG_IMG_HEIGHT;
                    targetWidth = (originWidth / ratio);
                } else {
                    targetWidth = originWidth;//默认原图宽高
                    targetHeight = originHeight;
                }
            } else {
                if (originHeight <= MAX_SIZE) {//不超过普通图片最长高度限制，原图大小
                    targetWidth = originWidth;
                    targetHeight = originHeight;
                } else {
                    float ratio = originHeight / MAX_SIZE;//超过限制，等比缩放高到限制值
                    targetHeight = MAX_SIZE;
                    targetWidth = (originWidth / ratio);
                }
            }
        }
        return BitmapUtils.compressImage(inputFile, outputFile, targetWidth, targetHeight, targetOptions);
    }

}
