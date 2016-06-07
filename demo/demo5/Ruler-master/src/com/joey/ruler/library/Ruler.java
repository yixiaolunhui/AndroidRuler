package com.joey.ruler.library;

import com.joey.ruler.R;
import com.joey.ruler.library.RulerScrollView.ScrollType;
import com.joey.ruler.library.RulerScrollView.ScrollViewListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Define a ruler, it can be scrolled horizontal,and marks the current label
 * 
 * @author Joey <lma_ma@163.com>
 * 
 */
public class Ruler extends FrameLayout {

	/**
	 * 标记，采用红线标示，标记当前刻度
	 */
	private ImageView mark;

	private Bitmap markBgBmp;
	/**
	 * 绘制的几个刻度，有三种刻度，最大刻度，中等刻度，和最小刻度
	 */
	private Drawable minDrawable;
	private Drawable maxDrawable;
	private Drawable midDrawable;

	private float bmpMaxHeight = 20.0f;

	private float maxTextSize = 10.0f;
	private float resultTextSize = 15.0f;

	/**
	 * unit size of the ruler
	 */
	private float minUnitSize = 20.0f;
	/**
	 * 最大单位的个数
	 */
	private int maxUnitCount = 24;
	/**
	 * 最大单位包含的每个单位数
	 */
	private int perUnitCount = 10;

	private int unitColor;
	private int markColor;
	/**
	 * 每个最小单位的大小
	 */
	private float perMinUnit;
	private int perMaxUnit = 1;
	/**
	 * Padding on the left,
	 */
	private float unitPadding = 10.0f;
	/**
	 * 这个刻度的起始位置部分偏移，这部分偏移，可以使scrollView滑动到刻度尺的最前面或者是最后面
	 */
	private int padding = 0;
	/**
	 * 刻度的宽度
	 */
	private final int UNIT_ITEM_WIDTH = 2;
	/**
	 * 刻度容器
	 */
	private LinearLayout unitContainer;
	/**
	 * 单位文字容器
	 */
	private LinearLayout textContainer;
	private RelativeLayout rulerContainer;
	/**
	 * 显示结果的容器
	 */
	private LinearLayout resultContainer;
	/**
	 * 左边的一个修饰文字
	 */
	private TextView resultTagView;
	/**
	 * 显示结果显示
	 */
	private TextView resultView;
	/**
	 * 整个刻度尺
	 */
	private LinearLayout rootContainer;
	/**
	 * 横向滑动的scrollerView
	 */
	private RulerScrollView scrollerView;

	private RulerHandler rulerHandler;

	private int mode;
	/**
	 * 标记刻度尺的类型，一种是一般的刻度尺， 另一种为时间刻度尺
	 */
	public final static int MODE_RULER = 0;
	public final static int MODE_TIMELINE = 1;

	private int unitVisible;
	/**
	 * 标记3中刻度图标的可见性
	 */
	public final static int MID_VISIBLE = 0x4;
	public final static int MIN_VISIBLE = 0x2;
	public final static int MAX_VISIBLE = 0x1;

	public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ruler,
				defStyleAttr, 0);
		minUnitSize = a.getDimension(R.styleable.Ruler_min_unit_size, 20.0f);
		maxUnitCount = a.getInteger(R.styleable.Ruler_max_unit_count, 24);
		perUnitCount = a.getInteger(R.styleable.Ruler_per_unit_count, 10);
		perMinUnit = a.getFloat(R.styleable.Ruler_min_unit, 1.0f);
		bmpMaxHeight = a.getDimension(R.styleable.Ruler_unit_bmp_height, 60.0f);
		mode = a.getInt(R.styleable.Ruler_ruler_mode, MODE_TIMELINE);
		unitPadding = minUnitSize / 2;
		unitVisible = a.getInt(R.styleable.Ruler_unit_visible, MID_VISIBLE
				| MIN_VISIBLE | MAX_VISIBLE);
		unitColor = a.getColor(R.styleable.Ruler_unit_color, Color.BLACK);
		markColor = a.getColor(R.styleable.Ruler_mark_color, Color.RED);
		a.recycle();

		init();
	}

	public Ruler(Context context, AttributeSet attrs) {
		super(context, attrs, R.attr.ruler_style);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ruler,
				R.attr.ruler_style, 0);
		minUnitSize = a.getDimension(R.styleable.Ruler_min_unit_size, 20.0f);
		maxUnitCount = a.getInteger(R.styleable.Ruler_max_unit_count, 24);
		perUnitCount = a.getInteger(R.styleable.Ruler_per_unit_count, 10);
	    perMinUnit = a.getFloat(R.styleable.Ruler_min_unit, 1.0f);
		bmpMaxHeight = a.getDimension(R.styleable.Ruler_unit_bmp_height, 60.0f);
		mode = a.getInt(R.styleable.Ruler_ruler_mode, MODE_TIMELINE);
		unitPadding = minUnitSize / 2;
		unitVisible = a.getInt(R.styleable.Ruler_unit_visible, MID_VISIBLE
				| MIN_VISIBLE | MAX_VISIBLE);
		unitColor = a.getColor(R.styleable.Ruler_unit_color, Color.BLACK);
		markColor = a.getColor(R.styleable.Ruler_mark_color, Color.RED);
		a.recycle();
		Log.i("Ruler",
				String.format(
						"minUnitSize %02f,maxUnitCount %d,perUnitCount %d,bmpMaxHeight %02f,mode %d",
						minUnitSize, maxUnitCount, perUnitCount, bmpMaxHeight,
						mode));
		init();
	}

	public Ruler(Context context) {
		super(context, null);
		init();

	}

	private void init() {
		Log.i("Ruler", "ruler init");
		switch(mode){
		    case MODE_RULER:
	              perMaxUnit =(int) (perUnitCount* perMinUnit);
		        break;
		    case MODE_TIMELINE:
		        perMaxUnit = 1;
                perMinUnit = 1;
		        break;
		}
		initDrawable();
		initParentContainer();
		initUnit();
		scrollerView.setOnScrollStateChangedListener(scrollListener);
		postDelayed(measurePaddingRunnable, 100);
	}

	/**
	 * 初始化刻度尺的第一级容器
	 */
	private void initParentContainer() {
		scrollerView = new RulerScrollView(getContext());
		scrollerView.setVerticalScrollBarEnabled(false);
		scrollerView.setHorizontalScrollBarEnabled(false);
		FrameLayout.LayoutParams scrollerParams = new FrameLayout.LayoutParams(
				-1, -2);
		scrollerParams.gravity = Gravity.CENTER_VERTICAL;
		scrollerView.setLayoutParams(scrollerParams);
		addView(scrollerView);

		rootContainer = new LinearLayout(getContext());
		rootContainer.setLayoutParams(new HorizontalScrollView.LayoutParams(-1,
				-2));
		scrollerView.addView(rootContainer);

		rulerContainer = new RelativeLayout(getContext());
		rulerContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		rootContainer.addView(rulerContainer);

		// 初始化显示刻度文字
		RelativeLayout.LayoutParams paramsTop = new RelativeLayout.LayoutParams(
				-1, -2);
		paramsTop.topMargin = dp2px((int) (bmpMaxHeight / 2 + resultTextSize));
		textContainer = new LinearLayout(getContext());
		textContainer.setLayoutParams(paramsTop);
		textContainer.setOrientation(LinearLayout.HORIZONTAL);
		textContainer.setId(R.id.unit_container_id);
		rulerContainer.addView(textContainer);
		// 初始化刻尺图标容器
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				-1, -2);
		params2.addRule(RelativeLayout.BELOW, R.id.unit_container_id);
		unitContainer = new LinearLayout(getContext());
		unitContainer.setLayoutParams(params2);
		unitContainer.setOrientation(LinearLayout.HORIZONTAL);
		unitContainer.setPadding(dp2px((int) unitPadding), 0,
				dp2px((int) unitPadding), 0);
		rulerContainer.addView(unitContainer);

		FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(-1,
				paramsTop.topMargin);
		resultContainer = new LinearLayout(getContext());
		resultContainer.setLayoutParams(params3);
		params3.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		addView(resultContainer);

		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				-2, -1);
		textParams.gravity = Gravity.CENTER;
		textParams.weight = 1;
		resultTagView = new TextView(getContext());
		resultTagView.setLayoutParams(textParams);
		resultContainer.addView(resultTagView);
		resultTagView.setTextSize(resultTextSize);
		resultTagView.setPadding(dp2px((int) resultTextSize), 0,
				dp2px((int) resultTextSize), 0);

		textParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		resultView = new TextView(getContext());
		resultView.setLayoutParams(textParams);
		resultView.setTextSize(resultTextSize);
		resultView.setPadding(dp2px((int) resultTextSize), 0,
				dp2px((int) resultTextSize), 0);

		resultContainer.addView(resultView);

		mark = new ImageView(getContext());
		FrameLayout.LayoutParams paramsMark = new FrameLayout.LayoutParams(-2,
				-1);
		paramsMark.gravity = Gravity.CENTER;
		mark.setLayoutParams(paramsMark);
		mark.setImageBitmap(markBgBmp);
		addView(mark);
	}
	/**
	 *  目的是让刻尺滑动到最前段，或者最后部分
	 */
	private Runnable measurePaddingRunnable = new Runnable() {

		@Override
		public void run() {
			if (padding == 0) {
//				这部分padding计算的总规则是，宽度的一半减去unitContainer左边的padding，再减去unitContainer第一个刻度的一半宽度。
				padding = getWidth()
						/ 2
						- dp2px((int) unitPadding) - unitContainer.getChildAt(0).getWidth()/2;
				rootContainer.setPadding(padding, 0, padding, 0);
				return;
			}
		}
	};

	public int getMinUnitSize()
	{
		return dp2px((int)minUnitSize);
	}
	public int getPerUnitCount()
	{
		return perUnitCount;
	}
	public int getMaxUnitCount()
	{
		return maxUnitCount;
	}
	/**
	 * 初始化刻度与刻度标记部分
	 */
	private void initUnit() {

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				dp2px((int) minUnitSize), -2);
		for (int i = 0; i < maxUnitCount; i++) {
			for (int j = 0; j < perUnitCount; j++) {
				TextView minUnitView = new TextView(getContext());
				minUnitView.setLayoutParams(params);
				minUnitView.setTextSize(.1f);
				minUnitView.setGravity(Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL);
				if (j == 0) {
					minUnitView.setCompoundDrawables(null, null, null,
							maxDrawable);
				} else if (j == perUnitCount / 2) {
					if ((unitVisible & (byte) MID_VISIBLE) == MID_VISIBLE)
						minUnitView.setCompoundDrawables(null, null, null,
								midDrawable);
				} else {
					if ((unitVisible & (byte) MIN_VISIBLE) == MIN_VISIBLE)
						minUnitView.setCompoundDrawables(null, null, null,
								minDrawable);
				}
				minUnitView.setText("");
				unitContainer.addView(minUnitView);
			}
		}
		TextView maxUnitView = new TextView(getContext());
		maxUnitView.setTextSize(.1f);
		maxUnitView.setLayoutParams(params);
		maxUnitView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		maxUnitView.setCompoundDrawables(null, null, null, maxDrawable);
		unitContainer.addView(maxUnitView);

		LinearLayout.LayoutParams maxParams = new LinearLayout.LayoutParams(
				dp2px((int) minUnitSize) * perUnitCount / 2, -2);
		for (int i = 0; i < maxUnitCount * 2; i++) {
			TextView textUnitView = new TextView(getContext());
			textUnitView.setTextSize(maxTextSize);
			textUnitView.setLayoutParams(maxParams);
			textUnitView.setGravity(Gravity.BOTTOM | Gravity.LEFT);

			if (i % 2 == 0) {
				textUnitView.setText(String.format("%d  ", perMaxUnit*i / 2));
			}

			textContainer.addView(textUnitView);
		}
	}

	/**
	 * 初始化单位的背景图
	 */
	private void initDrawable() {
		int maxHeight = dp2px((int) bmpMaxHeight);
		int midHeight = maxHeight * 3 / 4;
		int minHeight = maxHeight * 2 / 3;
		Bitmap bmp1 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH), maxHeight,
				Config.ARGB_8888);
		Bitmap bmp2 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH), maxHeight,
				Config.ARGB_8888);
		Bitmap bmp3 = Bitmap.createBitmap(dp2px(UNIT_ITEM_WIDTH), maxHeight,
				Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setColor(unitColor);
		paint.setStrokeWidth(10);
		paint.setStyle(Paint.Style.STROKE);

		Canvas canvas1 = new Canvas(bmp1);
		canvas1.drawLine(0, 0, 0, maxHeight, paint);

		Canvas canvas2 = new Canvas(bmp2);
		canvas2.drawLine(0, maxHeight - midHeight, 0, maxHeight, paint);

		Canvas canvas3 = new Canvas(bmp3);
		paint.setAlpha(80);
		canvas3.drawLine(0, maxHeight - minHeight, 0, maxHeight, paint);

		minDrawable = new BitmapDrawable(bmp3);
		minDrawable.setBounds(0, 0, minDrawable.getMinimumWidth(),
				minDrawable.getMinimumHeight());
		maxDrawable = new BitmapDrawable(bmp1);
		maxDrawable.setBounds(0, 0, maxDrawable.getMinimumWidth(),
				maxDrawable.getMinimumHeight());
		midDrawable = new BitmapDrawable(bmp2);
		midDrawable.setBounds(0, 0, midDrawable.getMinimumWidth(),
				midDrawable.getMinimumHeight());
		markBgBmp = Bitmap.createBitmap(2 * dp2px(UNIT_ITEM_WIDTH), maxHeight
				* 2 + dp2px((int) maxTextSize), Config.ARGB_8888);
		Canvas canvas4 = new Canvas(markBgBmp);
		paint.setColor(markColor);
		canvas4.drawLine(0, 0, 0, markBgBmp.getHeight(), paint);

	}

	/**
	 * 设置刻尺的标签，比如品牌什么的
	 * 
	 * @param textTag
	 */
	public void setRulerTag(String textTag) {
		if (textTag == null)
			return;
		resultTagView.setText(textTag);
	}

	public void setRulerHandler(RulerHandler rulerHandler) {
		this.rulerHandler = rulerHandler;
	}

	/**
	 * time format is HH:MM 跳转到时间刻度尺的部分，只有在时间轴模式下条件下才能使用
	 * 
	 * @param formatTime
	 */
	public void scrollToTime(String formatTime) {
		Log.i(getClass().getName(), "formatTime = " + formatTime);
		if (mode == MODE_RULER)
			return;
		if (formatTime == null || formatTime.isEmpty())
		{
		    if(rulerHandler != null){
                rulerHandler.error(new RulerError(RulerError.ERROR_AUTHOR));
            }
		    return;
		}
		String value[] = formatTime.split(":");
		if (value.length < 2)
		{
		    if(rulerHandler != null){
                rulerHandler.error(new RulerError(RulerError.ERROR_AUTHOR));
            }
		    return;
		}
		int minVal = 0;
		Log.i(getClass().getName(), "minVal = " + minVal);
		try{
		    int hour = Integer.parseInt(value[0]) % 24;
	        int minute = Integer.parseInt(value[1]) % 60;
	        Log.i(getClass().getName(), "hour is " + hour + ",minute is " + minute);
	        float val = hour * 10 + (float) minute / 6;
	        Log.i(getClass().getName(), "val = " + val);
	        if(hour<0||minute<0||val<0){
	            if(rulerHandler != null){
	                rulerHandler.error(new RulerError(RulerError.ERROR_NEGATIVE));
	            }
	            return;
	        }
	        if (val < minVal) {
	            scrollerView.smoothScrollTo(0, 0);
	            return;
	        }
	        scrollerView.smoothScrollTo(
	                (int) ((val - minVal) * dp2px((int) minUnitSize)), 0);
	        resultView.setText(formatTime);
		}
		catch(Exception e){
		    if(rulerHandler != null){
                rulerHandler.error(new RulerError(RulerError.ERROR_AUTHOR));
            }
            return;
		}
		
	}

	/**
	 * 跳转到刻度尺的某个位置
	 * 
	 * @param max
	 *            最大刻度
	 * @param min
	 *            最小刻度
	 * @param val
	 *            最小刻度的浮点部分
	 */
	private void scrollTo(int max, int min, float val) {
		if(min>perUnitCount)
		{
		    if(rulerHandler != null){
		        rulerHandler.error(new RulerError(RulerError.ERROR_OVER));
		    }
		    return;
		}
		int minVal = 0;
		Log.i(getClass().getName(), "minVal = " + minVal);
		if( max > maxUnitCount)
		{
		    if(rulerHandler != null){
                rulerHandler.error(new RulerError(RulerError.ERROR_OVER));
            }
		    return ;
		}
		int total = max * 10 + min;
		if (total < minVal) {
			scrollerView.smoothScrollTo(0, 0);
			return;
		}
		scrollerView.smoothScrollTo(
				(int) ((total - minVal + val) * dp2px((int) minUnitSize)), 0);
		showResult(max, min, minVal);
	}

	public void scrollTo(String msg){
	    if(msg == null){
	        return;
	    }
	    try{
	        Double value = Double.parseDouble(msg);
	        if(value < 0){
	            if(rulerHandler != null){
	                rulerHandler.error(new RulerError(RulerError.ERROR_NEGATIVE));
	            }
	            return;
	        }
	        value /= perMaxUnit ;
	        Log.i("MainActivity scrollTo","value= "+value);  

	        int max = value.intValue();
	        int min =(int)( (value.doubleValue() - max) * 10);
	        float val = (float)(value.doubleValue() - max - min/10.0f)*10;
	        Log.i("MainActivity","max = "+max+",min = "+min+" val = "+val);  
	        scrollTo(max,min,val);
	    }
	    catch(Exception e){
	        if(rulerHandler != null){
                rulerHandler.error(new RulerError(RulerError.ERROR_AUTHOR));
            }
	    }
	  
	}
	
	ScrollViewListener scrollListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ScrollType scrollType) {

			switch (scrollType) {
			case IDLE:
			case TOUCH_SCROLL:
			case FLING:
				
				int newScrollX = scrollerView.getScrollX();
				int bigUnitSize = (dp2px((int) minUnitSize) * perUnitCount);
				int smallUnitSize = dp2px((int) minUnitSize);
				int max = newScrollX / bigUnitSize;
				int min = newScrollX / smallUnitSize % perUnitCount;
				float val = (float) (newScrollX - (max * bigUnitSize) - (min * smallUnitSize))
						/ (float) smallUnitSize;

				Log.i(getClass().getName(), "max = " + max + ",min = " + min
						+ ",val = " + val);
				Log.i(getClass().getName(), "unitvisible " + unitVisible);
				Log.i(getClass().getName(), "midvisible "
						+ (unitVisible & (byte) MID_VISIBLE));
				Log.i(getClass().getName(), "minvisible "
						+ (unitVisible & (byte) MIN_VISIBLE));

				showResult(max, min, val);

				break;
			}
		}

	};

	private void showResult(int max, int min, float val) {
		if(max == maxUnitCount)
		{
			min = 0;
			val = 0f;
		}
		String text = "";
		if (mode == MODE_TIMELINE) {
			int hour = max;
			int minute = (int) ((min + val) * 60 / perUnitCount);
			text = String.format("%02d:%02d", hour, minute);
		}
		if (mode == MODE_RULER) {
			text = String.format("%.2f",
					((float) max + ((float) min + val) / 10)*perMaxUnit);
		}
		resultView.setText(text);
		if (rulerHandler != null) {
			rulerHandler.markText(text);
		}
	}

	public int dp2px(int dp) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public int px2dp(int px) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}
