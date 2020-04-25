package cn.edu.xidian.yhzhang2020.imageprocessapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class ImageHelper {

    public static final HashMap<String, float[]> colorAdjustMatrix = new HashMap<>();

    static {
        colorAdjustMatrix.put("反转", new float[]{-1, 0, 0, 1, 1, 0, -1, 0, 1, 1, 0, 0, -1, 1, 1, 0, 0, 0, 1, 0});
        colorAdjustMatrix.put("怀旧", new float[]{(float) 0.393, (float) 0.769000, (float) 0.189, 0, 0, (float) 0.349, (float) 0.689, (float) 0.168, 0, 0, (float) 0.272, (float) 0.534, (float) 0.131, 0, 0, 0, 0, 0, 1, 0});
        colorAdjustMatrix.put("自定义1", new float[]{0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, (float) 0.254, 0, (float) 0.12154, 0, 0, 1, 1, 1});
        colorAdjustMatrix.put("自定义2", new float[]{0, 1, 0, 1, 0, (float) 0.254, 0, 1, 0, 1, 0, 1, 0, (float) 0.738, 0, (float) 0.12154, 0, 1, 1, 1, 1});
        colorAdjustMatrix.put("自定义3", new float[]{0, 1, (float) 0.154, (float) 0.87, 0, 1, (float) 0.3458, 0, 0, 0, 0, 1, 0, (float) 0.254, 0, (float) 0.12154, 0, 0, 1, 1, 1});
        colorAdjustMatrix.put("自定义4", new float[]{1, (float) 0.5, 0, (float) 0.158, 0, 1, (float) 0.1526, 1, 0, 1, 0, 1, 0, 0, 0, (float) 0.12154, 0, 0, 1, 1, 1});
    }

    public static final String TAG = "IMAGE_HELPER";

    //将文件路径转化为Uri
    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //将图片转化为黑白
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //保存图片至特定路径
    public static void saveToPath(Bitmap bitmap, String fullPath) {
        File f = new File(fullPath);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();

        } catch (Exception e) {
            Log.e(TAG, "saveToPath: FAIL TO SAVE PICTURE" + e.getMessage());
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "saveToPath: FAIL TO CLOSE STREAM" + e.getMessage());
            }
        }
    }

    //将图片同步到系统相册
    public static void addToGallery(String currentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(currentPhotoPath));
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //获取唯一文件名
    @SuppressLint("SimpleDateFormat")
    public static String makeFileName() {
        return "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    //将图片压缩成正方形
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }
            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;
            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    //根据4*5调节矩阵对图片进行处理
    //mColorMatrix是一个表示4*5矩阵的20位float类型一维数组
    public static Bitmap setImageMatrix(Bitmap bitmap, float[] mColorMatrix) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);//原来为ARGB_868
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(mColorMatrix);//将一维数组设置到ColorMatrix

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmp;
    }

    //添加马赛克
    public static Bitmap addMosaic(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];

        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0;

        int offsetX = 0, offsetY = 0;
        int newX = 0, newY = 0;
        int size = 8;
//        int size = width > 400 ? (int) ((double) width * 0.01) : 4;
        double total = size * size;
        double sumred = 0, sumgreen = 0, sumblue = 0;
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                newY = (row / size) * size;
                newX = (col / size) * size;
                offsetX = newX + size;
                offsetY = newY + size;
                for (int subRow = newY; subRow < offsetY; subRow++) {
                    for (int subCol = newX; subCol < offsetX; subCol++) {
                        if (subRow < 0 || subRow >= height) {
                            continue;
                        }
                        if (subCol < 0 || subCol >= width) {
                            continue;
                        }
                        index = subRow * width + subCol;
                        ta = (inPixels[index] >> 24) & 0xff;
                        sumred += (inPixels[index] >> 16) & 0xff;
                        sumgreen += (inPixels[index] >> 8) & 0xff;
                        sumblue += inPixels[index] & 0xff;
                    }
                }
                index = row * width + col;
                tr = (int) (sumred / total);
                tg = (int) (sumgreen / total);
                tb = (int) (sumblue / total);
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;

                sumred = sumgreen = sumblue = 0; // reset them...
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }


    //添加随机噪声
    public static Bitmap addNoise(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = bitmap.copy(Bitmap.Config.RGB_565, true);
        int originalColor = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int gray = 0;
        Random rand = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int level = rand.nextInt(15);
                switch (level) {
                    case 0:
                        originalColor = result.getPixel(x, y);
                        r = Color.red(originalColor);
                        g = Color.green(originalColor);
                        b = Color.blue(originalColor);
                        gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        if (gray < 128) {
                            result.setPixel(x, y, Color.BLACK);
                        } else {
                            result.setPixel(x, y, Color.WHITE);
                        }
                        break;
                    case 1:
                        originalColor = result.getPixel(x, y);
                        r = Color.red(originalColor);
                        g = Color.green(originalColor);
                        b = Color.blue(originalColor);
                        gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        result.setPixel(x, y, Color.rgb(r + gray, g + gray, b + gray));
                        break;
                    case 2:
                        originalColor = result.getPixel(x, y);
                        r = Color.red(originalColor);
                        g = Color.green(originalColor);
                        b = Color.blue(originalColor);
                        gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        result.setPixel(x, y, Color.rgb(r - gray, g - gray, b - gray));
                        break;
                    case 3:
                        originalColor = result.getPixel(x, y);
                        r = Color.red(originalColor);
                        g = Color.green(originalColor);
                        b = Color.blue(originalColor);
                        gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        result.setPixel(x, y, Color.rgb(r + gray, g + gray, b));
                        break;
                    default:
                        break;
                }
            }
        }
        return result;
    }

    public static Bitmap abstractColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = bitmap.copy(Bitmap.Config.RGB_565, true);
        int originalColor = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int gray = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                originalColor = result.getPixel(x, y);
                r = Color.red(originalColor);
                g = Color.green(originalColor);
                b = Color.blue(originalColor);
                gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                result.setPixel(x, y, Color.rgb(r + gray / 2, g + gray / 3, b + gray / 4));
            }
        }
        return result;
    }

    //二值化
    public static Bitmap toBlackAndWhite(Bitmap bitmap,int value) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int threshold = value;
        Bitmap result = bitmap.copy(Bitmap.Config.RGB_565, true);
        int originalColor = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int gray = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                originalColor = result.getPixel(x, y);
                r = Color.red(originalColor);
                g = Color.green(originalColor);
                b = Color.blue(originalColor);
                gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                if (gray < threshold) {
                    result.setPixel(x, y, Color.rgb(0, 0, 0));
                } else {
                    result.setPixel(x, y, Color.rgb(255, 255, 255));
                }
            }
        }
        return result;
    }

    //高斯模糊
    public static Bitmap gaussianBlu(Bitmap src) {
        //模糊程度
        Bitmap bitmap = src.copy(src.getConfig(), true);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int radius = (width < 400) ? 4 : (int) ((double) width * 0.01);

        int[] newPixels = new int[width * height];
        bitmap.getPixels(newPixels, 0, width, 0, 0, width, height);

        int wm = width - 1;
        int hm = height - 1;
        int wh = width * height;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(width, height)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < height; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = newPixels[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < width; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = newPixels[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += width;
        }
        for (x = 0; x < width; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * width;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += width;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < height; y++) {
                newPixels[yi] = (0xff000000 & newPixels[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * width;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += width;
            }
        }

        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);

        return (bitmap);
    }

    //调整亮度
    //value范围0-256
    public static Bitmap adjustBrightness(Bitmap src, int value) {
        int brightness = value - 127;
        return setImageMatrix(src, new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0});
    }

    //调整对比度
    //value范围0-256
    public static Bitmap adjustContrast(Bitmap src, int value) {
        float contrast = (float) ((value + 128) / 256.0);
        return setImageMatrix(src, new float[]{
                contrast, 0, 0, 0, 0,
                0, contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0,
                0, 0, 0, 1, 0
        });
    }

//    public static Bitmap adjustContrast(Bitmap src, int value) {
//        int width = src.getWidth();
//        int height = src.getHeight();
//        int threshold = 127;
//        Bitmap result = src.copy(Bitmap.Config.RGB_565, true);
//        int originalColor = 0;
//        int r = 0;
//        int g = 0;
//        int b = 0;
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                originalColor = result.getPixel(x, y);
//                r = Color.red(originalColor);
//                g = Color.green(originalColor);
//                b = Color.blue(originalColor);
//                r = (int) (r + (r - threshold) * ((double) (value - 1) / (double) 255));
//                g = (int) (g + (g - threshold) * ((double) (value - 1) / (double) 255));
//                b = (int) (b + (b - threshold) * ((double) (value - 1) / (double) 255));
//                result.setPixel(x,y,Color.rgb(r,g,b));
//            }
//        }
//        return result;
//    }


    //调整饱和度
    //value范围0-256
    public static Bitmap adjustSaturation(Bitmap src, int value) {
        Bitmap result = src.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation((float) value / (float) 128);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0, 0, paint);
        return result;
    }

    //调整对比度
    //value范围0-256
    public static Bitmap adjustHue(Bitmap src, int value) {
        int degree = (int) (((double) value / (double) 256) * 360);
        Bitmap result = src.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setRotate(0, degree);
        colorMatrix.setRotate(1, degree);
        colorMatrix.setRotate(2, degree);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0, 0, paint);
        return result;
    }

    //获取图片的直方图,保存为256*100的Bitmap
    public static Bitmap getHistogram(Bitmap src) {
        final int resultWidth = 256;
        final int resultHeight = 100;
        Bitmap result = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.RGB_565);
        int width = src.getWidth();
        int height = src.getHeight();
        //统计各个灰度像素的点数
        int[] grayCounts = new int[256];
        int originalColor = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int gray = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                originalColor = src.getPixel(x, y);
                r = Color.red(originalColor);
                g = Color.green(originalColor);
                b = Color.blue(originalColor);
                gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                grayCounts[gray]++;
            }
        }
        int sum = 0;
        int max = 0;
        for (int grayCount : grayCounts) {
            max = Math.max(grayCount, max);
            sum += grayCount;
        }
        //将原始像素点数以最大像素点数进行归一化处理
        for (int i = 0; i < grayCounts.length; i++) {
            grayCounts[i] = (int) (((double) grayCounts[i] / (double) max) * (double) resultHeight);
        }
        //将像素统计信息绘制到result
        for (int i = 0; i < resultWidth; i++) {
            for (int j = resultHeight - 1; j >= resultHeight - grayCounts[i]; j--) {
                result.setPixel(i, j, Color.WHITE);
            }
        }
        return result;
    }
}
