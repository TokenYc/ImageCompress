package net.archeryc.imagecompress;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.xw.repo.BubbleSeekBar;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import net.archeryc.imagecompress.ImageCompress.BitmapUtils;
import net.archeryc.imagecompress.ImageCompress.FileSizeUtils;
import net.archeryc.imagecompress.ImageCompress.ImageCompressUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 1;


    String mSelectedPath;

    String mCompressedPath;

    TextView tvDes;

    int options=ImageCompressUtils.Config.DEFAULT_OPTIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        tvDes = (TextView) findViewById(R.id.tv_des);
        tvDes.setTextIsSelectable(true);
        findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matisse.from(MainActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(1)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });


        findViewById(R.id.btn_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mSelectedPath)) {
                    Toast.makeText(MainActivity.this, "先选图片啊喂", Toast.LENGTH_SHORT).show();
                } else {

                    long startTime = System.currentTimeMillis();
                    String outputPath = ImageCompressUtils.compressImage(mSelectedPath, mCompressedPath, options);
                    BitmapUtils.Size inputSize = BitmapUtils.getImageSize(mSelectedPath);
                    BitmapUtils.Size outputSize = BitmapUtils.getImageSize(outputPath);

                    String des = "spend time:" + (System.currentTimeMillis() - startTime) + "\n" +
                            "quality compress:" + options + "%" + "\n" +
                            "max size:" + ImageCompressUtils.Config.MAX_SIZE + "\n\n" +
                            "input path:" + mSelectedPath + "\n" +
                            "input width:" + inputSize.getWidth() + "\n" +
                            "input height:" + inputSize.getHeight() + "\n" +
                            "input size:" + FileSizeUtils.getFileOrFilesSize(mSelectedPath, FileSizeUtils.SIZETYPE_MB) + "MB" + "\n" +
                            "\n" +
                            "output path:" + outputPath + "\n" +
                            "output width:" + outputSize.getWidth() + "\n" +
                            "output height:" + outputSize.getHeight() + "\n" +
                            "output size:" + FileSizeUtils.getFileOrFilesSize(outputPath, FileSizeUtils.SIZETYPE_MB) + "MB" + "\n";
                    tvDes.setText(des);
                }
            }
        });

        findViewById(R.id.btn_view_orgin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mSelectedPath)) {
                    Toast.makeText(MainActivity.this, "先选图片啊喂", Toast.LENGTH_SHORT).show();
                } else {
                    viewPhoto(mSelectedPath);
                }
            }
        });

        findViewById(R.id.btn_view_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mCompressedPath)){
                    Toast.makeText(MainActivity.this, "先压缩啊喂", Toast.LENGTH_SHORT).show();
                }else{
                    viewPhoto(mCompressedPath);
                }
            }
        });

        BubbleSeekBar bubbleSeekBar = (BubbleSeekBar) findViewById(R.id.seekbar);
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                options=progress;
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    private String createDir() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "aImageTest" + File.separator);
        if (file.exists()) {
            return file.getPath();
        } else {
            file.mkdir();
            return file.getPath();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = Matisse.obtainResult(data).get(0);
            mSelectedPath = getRealFilePath(this, uri);
            File file = new File(mSelectedPath);
            mCompressedPath = createDir() + File.separator + file.getName();
            tvDes.setText("input path:" + mSelectedPath);
        }
    }

    private String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void viewPhoto(String path){
        Intent intent = new Intent(Intent.ACTION_VIEW);
//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
        intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        startActivity(intent);
    }
}
