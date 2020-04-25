package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.adjust;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageHelper;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageProcessActivity;
import cn.edu.xidian.yhzhang2020.imageprocessapp.R;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class AdjustFragment extends Fragment {
    private Bitmap originalBitmap;
    private Bitmap histogramBitmap;
    private ImageView imageViewOfHistogram;
    private ArrayList<AdjustItem> adjusts;
    private AdjustItemAdapter adapter;
    private FrameLayout containerFrameLayout;
    String TAG = "ADJUST_FRAGMENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_adjust, container, false);
        containerFrameLayout = root.findViewById(R.id.containerInAdjustFragment);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_adjust);
        imageViewOfHistogram = root.findViewById(R.id.image_histogram);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        final ImageProcessActivity rootActivity = (ImageProcessActivity) getActivity();
//        root.post(new Runnable() {
//            @Override
//            public void run() {
//                histogramBitmap = ImageHelper.getHistogram(rootActivity.getOriginalBitmap());
//                Log.d(TAG, "run: finished");
//            }
//        });
        this.originalBitmap = ImageHelper.centerSquareScaleBitmap(rootActivity.getOriginalBitmap(), rootActivity.filterPreviewViewSize);
        initialAdjusts();
//        loadSeekBar();
        SeekBar seekBar = root.findViewById(R.id.seekBar);
        adapter = new AdjustItemAdapter(adjusts, rootActivity);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 当拖动条的滑块位置发生改变时触发该方法
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                String item = adapter.getItemName();
                switch (item) {
                    case "亮度":
                        rootActivity.setTemporaryBitmap(ImageHelper.adjustBrightness(rootActivity.getOriginalBitmap(), progress));
                        break;
                    case "对比度":
                        rootActivity.setTemporaryBitmap(ImageHelper.adjustContrast(rootActivity.getOriginalBitmap(), progress));
                        break;
                    case "饱和度":
                        rootActivity.setTemporaryBitmap(ImageHelper.adjustSaturation(rootActivity.getOriginalBitmap(), progress));
                        break;
                    case "色调":
                        rootActivity.setTemporaryBitmap(ImageHelper.adjustHue(rootActivity.getOriginalBitmap(), progress));
                        break;
                    case "白板":
                        rootActivity.setTemporaryBitmap(ImageHelper.toBlackAndWhite(rootActivity.getOriginalBitmap(), progress));
                        break;
                    default:
                        break;
                }
                rootActivity.showImage(item);

            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        recyclerView.setAdapter(adapter);
        return root;
    }

    private void initialAdjusts() {
        adjusts = new ArrayList<>();
        adjusts.add(new AdjustItem("原图", originalBitmap));
        adjusts.add(new AdjustItem("亮度", ImageHelper.adjustBrightness(originalBitmap, 196)));
        adjusts.add(new AdjustItem("对比度", ImageHelper.adjustContrast(originalBitmap,196)));
        adjusts.add(new AdjustItem("饱和度", ImageHelper.adjustSaturation(originalBitmap,196)));
        adjusts.add(new AdjustItem("色调", ImageHelper.adjustHue(originalBitmap,230)));
        adjusts.add(new AdjustItem("白板", ImageHelper.toBlackAndWhite(originalBitmap,127)));

    }
}
