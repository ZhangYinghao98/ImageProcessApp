package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.histogram;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageHelper;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageProcessActivity;
import cn.edu.xidian.yhzhang2020.imageprocessapp.R;

public class HistogramFragment extends Fragment {

    private ImageProcessActivity imageProcessActivity;
    private Bitmap histogram;
    private ImageView histogramImageView;
    private RangeSeekBar rangeSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_histogram, container, false);
        imageProcessActivity = (ImageProcessActivity)getActivity();
        ImageButton refreshHistogramButton = root.findViewById(R.id.refreshButtonInHistogram);
        histogram = ImageHelper.getHistogram(imageProcessActivity.getTemporaryBitmap());
        histogramImageView = root.findViewById(R.id.image_histogram);
        histogramImageView.setImageBitmap(histogram);
        refreshHistogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                histogram = ImageHelper.getHistogram(imageProcessActivity.getTemporaryBitmap());
                histogramImageView.setImageBitmap(histogram);
            }
        });
        rangeSeekBar = root.findViewById(R.id.rangeSeekBar);
        ImageButton checkButton = root.findViewById(R.id.checkButtonInHistogram);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = (int)rangeSeekBar.getSelectedMaxValue();
                int min = (int)rangeSeekBar.getSelectedMinValue();
                imageProcessActivity.setTemporaryBitmap(ImageHelper.adjustLightnessByTwoValue(imageProcessActivity.getTemporaryBitmap(),min,max));
                imageProcessActivity.showImage("当前");
                histogram = ImageHelper.getHistogram(imageProcessActivity.getTemporaryBitmap());
                histogramImageView.setImageBitmap(histogram);
            }
        });
        return root;
    }
}
