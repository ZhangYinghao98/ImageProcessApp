package cn.edu.xidian.yhzhang2020.imageprocessapp.ui.laboratory;

import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageHelper;
import cn.edu.xidian.yhzhang2020.imageprocessapp.ImageProcessActivity;
import cn.edu.xidian.yhzhang2020.imageprocessapp.R;

/**
 * @Author yhzhang2020@stu.xidian.edu.cn
 */

public class LaboratoryFragment extends Fragment {
    private ImageProcessActivity rootActivity;
    private EditText[] editTexts = new EditText[20];
    private float[] adjustMatrix = new float[20];
    private GridLayout gridLayout;
    private int editTextWidth = 60;
    private int editTextHeight = 30;

    String TAG = "LABORATORY_FRAGMENT";

    private float[] getAdjustMatrix() {
        return adjustMatrix;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_laboratory, container, false);
        rootActivity = (ImageProcessActivity) getActivity();
        ImageButton helpButton = root.findViewById(R.id.helpButtonInLaboratory);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        " 这是安卓系统中的颜色矩阵, 矩阵的四行分别决定图像的红色、绿色、蓝色、透明度属性， "+
                        "尝试不同的值以获取属于你的独一无二的滤镜",Toast.LENGTH_LONG).show();
            }
        });
        ImageButton refreshButton = root.findViewById(R.id.refreshButtonInLaboratory);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEditTexts();
                rootActivity.setTemporaryBitmap(rootActivity.getOriginalBitmap());
                rootActivity.showImage("原图");
            }
        });
        ImageButton confirmButton = root.findViewById(R.id.checkButtonInLaboratory);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesOfEditText();
                rootActivity.setTemporaryBitmap(ImageHelper.setImageMatrix(rootActivity.getOriginalBitmap(), getAdjustMatrix()));
                rootActivity.showImage("自定义");
            }
        });
        gridLayout = root.findViewById(R.id.grid_layout);
//        editTextWidth = gridLayout.getWidth() / 4;
//        editTextHeight = gridLayout.getHeight() / 5;
        addEditTexts();
        initEditTexts();
        return root;
    }

    //动态添加EditText
    private void addEditTexts() {
        for (int i = 0; i < 20; i++) {
            EditText et = new EditText(getContext());
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editTexts[i] = et;
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            gridLayout.addView(et, params);
        }
    }

    //初始化EditText
    private void initEditTexts() {
        for (int i = 0; i < 20; i++) {
            if (i % 6 == 0) {
                editTexts[i].setText(String.valueOf(1));
            } else {
                editTexts[i].setText(String.valueOf(0));
            }
        }
    }

    //获取矩阵值
    private void getValuesOfEditText() {
        for (int i = 0; i < 20; i++) {
            String valueString = editTexts[i].getText().toString();
            boolean isNone = (null == valueString) || "".equals(valueString);
            adjustMatrix[i] = isNone ? 0.0f : Float.parseFloat(valueString);
            if (isNone) {
                editTexts[i].setText("0");
            }
        }
    }

}
