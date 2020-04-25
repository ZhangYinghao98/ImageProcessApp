package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.adjust;

import android.graphics.Bitmap;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class AdjustItem {
    private String name;
    private Bitmap image;

    public AdjustItem(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }
}
