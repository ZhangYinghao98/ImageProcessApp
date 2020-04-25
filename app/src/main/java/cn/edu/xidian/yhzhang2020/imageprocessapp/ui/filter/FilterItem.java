package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.filter;

import android.graphics.Bitmap;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class FilterItem {
    private String name;
    private Bitmap image;

    public FilterItem(String name, Bitmap image) {
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
