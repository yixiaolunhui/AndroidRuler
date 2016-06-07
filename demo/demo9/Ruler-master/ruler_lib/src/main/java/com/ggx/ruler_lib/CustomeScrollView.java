package com.ggx.ruler_lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.ggx.ruler_lib.OnScrollViewChanged;

/**
 * Created by John on 2016/3/5.
 */
public class CustomeScrollView extends ScrollView{

    private OnScrollViewChanged scrollViewChanged;

    public CustomeScrollView(Context context) {
        super(context);
    }

    public CustomeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnScrollViewChanged getScrollViewChanged() {
        return scrollViewChanged;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollViewChanged!=null){
            scrollViewChanged.onScrollChanged(l,t,oldl,oldt);
        }
    }

    public void setScrollViewChanged(OnScrollViewChanged scrollViewChanged) {
        this.scrollViewChanged = scrollViewChanged;
    }
}
