package com.abcmeasure.andyzhou.ruler;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

public class TouchPanelViewModel {
    public ObservableField<Boolean> touchMode = new ObservableField<>();
    public OnPanelTouchListener touchListener;
    public ObservableField<Integer> leftCursorMargin = new ObservableField<>();
    public ObservableField<Integer> rightCursorMargin = new ObservableField<>();
    public ObservableField<String> distanceInchText = new ObservableField<>();
    float dpi;
    int viewType;

    @BindingAdapter("dynamic_marginLeft")
    public static void setMarginLeft(View view, int leftMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.setMargins(leftMargin, layoutParams.bottomMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            view.setLayoutParams(layoutParams);
        }
    }

    public TouchPanelViewModel(float dpi) {
        this.dpi = dpi;
        touchMode.set(false);
        touchListener = new OnPanelTouchListener();
        leftCursorMargin.set(0);
        rightCursorMargin.set(0);
        viewType = Constants.INCH_VIEWTYPE;
    }

    public void calculateTouchDistance(int left, int right) {
        int distancePoint = Math.abs(left - right);
        double distanceInchFloat = (float) distancePoint / dpi;
        double distanceCmFloat = (float) distancePoint / dpi * 2.54;
        if (viewType == Constants.INCH_VIEWTYPE)
            distanceInchText.set(String.format(Locale.CANADA, "%.2f in", distanceInchFloat));
        else
            distanceInchText.set(String.format(Locale.CANADA, "%.2f cm", distanceCmFloat));
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public class OnPanelTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                //Hide cursors when finger up
                touchMode.set(false);
                return false;
            }
            int pointerCount = event.getPointerCount();
            switch (pointerCount) {
                //Maximum 2 cursors supported
                case 1:
                    touchMode.set(true);
                    leftCursorMargin.set(0);
                    rightCursorMargin.set(Math.round(event.getX()));
                    calculateTouchDistance(0, Math.round(event.getX()));
                    break;
                case 2:
                    touchMode.set(true);
                    leftCursorMargin.set(Math.round(event.getX(0)));
                    rightCursorMargin.set(Math.round(event.getX(1)));
                    calculateTouchDistance(Math.round(event.getX(0)), Math.round(event.getX(1)));
                    break;
                default:
                    leftCursorMargin.set(0);
                    rightCursorMargin.set(0);
                    touchMode.set(false);
            }
            return true;
        }
    }
}
