package com.study.horizontalruler;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

	private OnScrollListener onScrollListener = null;
	
	public MyHorizontalScrollView(Context context) {
		this(context, null);
	}
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(onScrollListener != null){
			onScrollListener.onScrollChanged(l, t, oldl, oldt);
		}
	}
	
	protected interface OnScrollListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	protected void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
