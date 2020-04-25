package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageHelper;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageProcessActivity;
import cn.edu.xidian.yhzhang2020.imageprocessapp.R;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class FilterItemAdapter extends RecyclerView.Adapter<FilterItemAdapter.ViewHolder> {

    private List<FilterItem> filters;
    private ImageProcessActivity rootActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View filterView;
        TextView filterNameTextView;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            filterView = view;
            filterNameTextView = view.findViewById(R.id.name_filter);
            imageView = view.findViewById(R.id.image_filter);
        }
    }

    public FilterItemAdapter(List<FilterItem> filters, ImageProcessActivity rootActivity) {
        this.filters = filters;
        this.rootActivity = rootActivity;
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                FilterItem filterItem = filters.get(position);
                String name = filterItem.getName();
                switch (name) {
                    case "原图":
                        rootActivity.setTemporaryBitmap(rootActivity.getOriginalBitmap());
                        break;
                    case "黑白":
                        rootActivity.setTemporaryBitmap(ImageHelper.toGrayscale(rootActivity.getOriginalBitmap()));
                        break;
                    case "怀旧":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("怀旧")));
                        break;
                    case "噪声":
                        rootActivity.setTemporaryBitmap(ImageHelper.addNoise(rootActivity.getOriginalBitmap()));
                        break;
                    case "马赛克":
                        rootActivity.setTemporaryBitmap(ImageHelper.addMosaic(rootActivity.getOriginalBitmap()));
                        break;
                    case "高斯模糊":
                        rootActivity.setTemporaryBitmap(ImageHelper.gaussianBlu(rootActivity.getOriginalBitmap()));
                        break;
                    case "抽象":
                        rootActivity.setTemporaryBitmap(ImageHelper.abstractColor(rootActivity.getOriginalBitmap()));
                        break;
                    case "反转":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("反转")));
                        break;
                    case "自定义1":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("自定义1")));
                        break;
                    case "自定义2":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("自定义2")));
                        break;
                    case "自定义3":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("自定义3")));
                        break;
                    case "自定义4":
                        rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), ImageHelper.colorAdjustMatrix.get("自定义4")));
                        break;
                    default:
                        break;
                }
                rootActivity.showImage(name);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterItem filterItem = filters.get(position);
        holder.imageView.setImageBitmap(filterItem.getImage());
        holder.filterNameTextView.setText(filterItem.getName());
    }
}
