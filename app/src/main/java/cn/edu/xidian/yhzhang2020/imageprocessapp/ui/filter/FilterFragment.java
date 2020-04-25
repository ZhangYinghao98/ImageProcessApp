package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.filter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FilterFragment extends Fragment {
    private Bitmap originalBitmap;
    private ArrayList<FilterItem> filters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter,container,false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_filter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ImageProcessActivity rootActivity = (ImageProcessActivity)getActivity();
        this.originalBitmap = ImageHelper.centerSquareScaleBitmap(rootActivity.getOriginalBitmap(), rootActivity.filterPreviewViewSize);
        initialFilters();
        final FilterItemAdapter adapter = new FilterItemAdapter(filters, rootActivity);
        recyclerView.setAdapter(adapter);
        return root;
    }



    private void initialFilters() {
        filters = new ArrayList<>();
        filters.add(new FilterItem("原图", originalBitmap));
        filters.add(new FilterItem("黑白", ImageHelper.toGrayscale(originalBitmap)));
        filters.add(new FilterItem("怀旧",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("怀旧"))));
        filters.add(new FilterItem("噪声", ImageHelper.addNoise(originalBitmap)));
        filters.add(new FilterItem("马赛克", ImageHelper.addMosaic(originalBitmap)));
        filters.add(new FilterItem("高斯模糊",ImageHelper.gaussianBlu(originalBitmap)));
        filters.add(new FilterItem("抽象", ImageHelper.abstractColor(originalBitmap)));
        filters.add(new FilterItem("反转",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("反转"))));
        filters.add(new FilterItem("自定义1",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("自定义1"))));
        filters.add(new FilterItem("自定义2",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("自定义2"))));
        filters.add(new FilterItem("自定义3",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("自定义3"))));
        filters.add(new FilterItem("自定义4",ImageHelper.setImageMatrix(originalBitmap,ImageHelper.colorAdjustMatrix.get("自定义4"))));

    }


}
