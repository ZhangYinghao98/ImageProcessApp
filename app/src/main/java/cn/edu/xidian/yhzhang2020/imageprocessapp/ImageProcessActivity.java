package cn.edu.xidian.yhzhang2020.imageprocessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import cn.edu.xidian.yhzhang2020.imageprocessapp.ui.adjust.AdjustFragment;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ui.filter.FilterFragment;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ui.histogram.HistogramFragment;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ui.laboratory.LaboratoryFragment;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class ImageProcessActivity extends AppCompatActivity {

    private static final String TAG = "IMAGE_PROCESS_ACTIVITY";
    //滤镜预览界面的大小，单位dp
    public static final int filterPreviewViewSize = 128;
    //保存原始文件的信息
    private Uri uri;
    //作为处理的对象
    private Bitmap originalBitmap;
    private Bitmap temporaryBitmap;
    private TextView textView;
    private Toolbar toolbar;
    FilterFragment filterFragment;
    AdjustFragment adjustFragment;
    HistogramFragment histogramFragment;
    LaboratoryFragment laboratoryFragment;
    Fragment[] fragments;
    BottomNavigationView bottomNavigationView;
    private int lastFragmentPosition = 0;

    public void setTextViewContent(String content) {
        textView.setText(content);
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public Bitmap getTemporaryBitmap() {
        return temporaryBitmap;
    }

    public void setTemporaryBitmap(Bitmap bitmap) {
        this.temporaryBitmap = bitmap;
    }

    public AdjustFragment getAdjustFragment() {
        return adjustFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        getImage(getIntent());
        initialView();
    }

    private void initialView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView = findViewById(R.id.name_current_image);
//        temporaryBitmap = ImageHelper.getHistogram(originalBitmap);
        temporaryBitmap = originalBitmap;
        showImage("原图");

        filterFragment = new FilterFragment();
        adjustFragment = new AdjustFragment();
        histogramFragment = new HistogramFragment();
        laboratoryFragment = new LaboratoryFragment();
        fragments = new Fragment[]{filterFragment,adjustFragment,histogramFragment,laboratoryFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragments[0]).show(fragments[0]).commit();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_filter: {
                    Log.d(TAG, "onNavigationItemSelected: filter"+lastFragmentPosition);
                    if (lastFragmentPosition != 0) {
                        switchFragment(lastFragmentPosition, 0);
                        lastFragmentPosition = 0;
                    }
                    break;
                }
                case R.id.navigation_adjust: {
                    Log.d(TAG, "onNavigationItemSelected: adjust"+lastFragmentPosition);
                    if (lastFragmentPosition != 1) {
                        switchFragment(lastFragmentPosition, 1);
                        lastFragmentPosition = 1;
                    }
                    break;
                }
                case R.id.navigation_histogram: {
                    if (lastFragmentPosition != 2) {
                        switchFragment(lastFragmentPosition, 2);
                        lastFragmentPosition = 2;
                    }
                    break;
                }
                case R.id.navigation_explore: {
                    Log.d(TAG, "onNavigationItemSelected: explore"+lastFragmentPosition);
                    if (lastFragmentPosition != 3) {
                        switchFragment(lastFragmentPosition, 3);
                        lastFragmentPosition = 3;
                    }
                    break;
                }
            }
            return true;
        }
    };

    private void switchFragment(int lastFragmentPosition, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragmentPosition]);//隐藏上个Fragment
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.fragments_container, fragments[index]);
        }
        transaction.show(fragments[index]).commitNow();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
            case R.id.undo:
                temporaryBitmap = originalBitmap;
                showImage("原图");
                break;
            case R.id.save:
                String imageFileName = ImageHelper.makeFileName() + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    Toast.makeText(ImageProcessActivity.this, "正在保存图片，请稍候", Toast.LENGTH_LONG).show();
                    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                    String filePath = image.getAbsolutePath();
                    ImageHelper.saveToPath(temporaryBitmap, filePath);
                    ImageHelper.addToGallery(filePath, ImageProcessActivity.this);
                    Toast.makeText(ImageProcessActivity.this, "图片已存至" + filePath, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
        return true;
    }

    public void getImage(Intent intent) {
        String[] dataString = Objects.requireNonNull(intent.getStringExtra("data")).split("<-->");
        assert dataString[0] != null;
        switch (dataString[0]) {
            case "URI":
                uri = Uri.parse(dataString[1]);
                break;
            case "PATH":
                uri = ImageHelper.getImageContentUri(this, dataString[1]);
                break;
            default:
                break;
        }
        try {
            originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "showImage: FILE NOT FOUND :" + e.getMessage());
        }
    }

    public void showImage(String nameOfImage) {
        textView.setText(nameOfImage);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(temporaryBitmap);
    }
}
