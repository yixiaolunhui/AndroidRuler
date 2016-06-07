package com.ggx.ruler_lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.ggx.ruler_lib.OnScrollViewChanged;

/**
 * Created by John on 2016/3/5.
 */
public class CustomeHorizontalScrollView extends HorizontalScrollView {

    private OnScrollViewChanged scrollViewChanged;

    public OnScrollViewChanged getScrollViewChanged() {
        return scrollViewChanged;
    }

    public void setScrollViewChanged(OnScrollViewChanged scrollViewChanged) {
        this.scrollViewChanged = scrollViewChanged;
    }

    public CustomeHorizontalScrollView(Context context) {
        super(context);
    }

    public CustomeHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomeHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollViewChanged!=null){
            scrollViewChanged.onScrollChanged(l,t,oldl,oldt);
        }
    }
}
