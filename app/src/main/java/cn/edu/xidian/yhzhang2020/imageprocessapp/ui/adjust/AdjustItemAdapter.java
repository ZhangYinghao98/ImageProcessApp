package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.adjust;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageProcessActivity;
import cn.edu.xidian.yhzhang2020.imageprocessapp.R;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class AdjustItemAdapter extends RecyclerView.Adapter<AdjustItemAdapter.ViewHolder> {
    private List<AdjustItem> adjusts;
    private ImageProcessActivity rootActivity;
    private String itemName = "原图";

    public String getItemName() {
        return itemName;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View adjustView;
        TextView adjustNameTextView;
        ImageView adjustImageView;

        ViewHolder(View view) {
            super(view);
            adjustView = view;
            adjustNameTextView = view.findViewById(R.id.name_adjust);
            adjustImageView = view.findViewById(R.id.image_adjust);
        }
    }

    public AdjustItemAdapter(List<AdjustItem> adjusts, ImageProcessActivity rootActivity) {
        this.adjusts = adjusts;
        this.rootActivity = rootActivity;
    }

    @Override
    public int getItemCount() {
        return adjusts.size();
    }

    @NonNull
    @Override
    public AdjustItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adjust_item, parent, false);
        final AdjustItemAdapter.ViewHolder holder = new AdjustItemAdapter.ViewHolder(view);

        holder.adjustView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AdjustItem adjustItem = adjusts.get(position);
                itemName = adjustItem.getName();
                switch (itemName) {
                    case "原图":
                        rootActivity.setTemporaryBitmap(rootActivity.getOriginalBitmap());
                        rootActivity.showImage(itemName);
                        break;
                    default:
                        break;
                }
                rootActivity.setTextViewContent(itemName);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdjustItemAdapter.ViewHolder holder, int position) {
        AdjustItem adjustItem = adjusts.get(position);
        holder.adjustNameTextView.setText(adjustItem.getName());
        holder.adjustImageView.setImageBitmap(adjustItem.getImage());
    }
}
