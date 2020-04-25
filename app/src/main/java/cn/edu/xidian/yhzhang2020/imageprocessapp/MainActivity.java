package cn.edu.xidian.yhzhang2020.imageprocessapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @Author
 * yhzhang2020@stu.xidian.edu.cn
 *
 * */

public class MainActivity extends AppCompatActivity {

    Uri photoURI;
    private String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTransparentStatusBar();
        ImageButton buttonOfCamera = findViewById(R.id.buttonOfCamera);
        ImageButton buttonOfPicture = findViewById(R.id.buttonOfPicture);
        buttonOfCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        buttonOfPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromAlbum();
            }
        });
    }

    private void setTransparentStatusBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_PHOTO = 2;

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, Objects.requireNonNull(ex.getMessage()));
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "cn.edu.xidian.yhzhang2020.imageprocessapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
//                    Toast.makeText(this, "图片已保存至" + currentPhotoPath, Toast.LENGTH_LONG).show();
                    launchImageProcessActivityWithUri();

                }
                break;
            case REQUEST_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    launchImageProcessActivityWithPath(getImagePath(data));
                }
                break;
            default:
                break;
        }
    }

    private void launchImageProcessActivityWithUri() {
        Intent intent = new Intent(this, ImageProcessActivity.class);
        intent.putExtra("data", "URI<-->" + photoURI.toString());
        startActivity(intent);
    }

    String currentPhotoPath;

    //保存图片
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = ImageHelper.makeFileName() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        ImageHelper.addToGallery(currentPhotoPath, MainActivity.this);
        return image;
    }


    public void choosePhotoFromAlbum() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
        }
    }

    public String getImagePath(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void launchImageProcessActivityWithPath(String path) {
        Intent intent = new Intent(this, ImageProcessActivity.class);
        intent.putExtra("data", "PATH<-->" + path);
        startActivity(intent);
    }
}
