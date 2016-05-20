package com.abcmeasure.andyzhou.ruler;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.ViewGroup;

public class RulerGridViewModel {
    public String rulerNumber;
    public float dotsPerUnit;

    public RulerGridViewModel(String rulerNumber, float dpi, int viewType) {
        this.rulerNumber = rulerNumber;
        if (viewType == Constants.INCH_VIEWTYPE)
            this.dotsPerUnit = dpi;
        else
            //convert dpi to dpcm
            this.dotsPerUnit = dpi / 2.54f;
    }

    @BindingAdapter("dynamic_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }
}
