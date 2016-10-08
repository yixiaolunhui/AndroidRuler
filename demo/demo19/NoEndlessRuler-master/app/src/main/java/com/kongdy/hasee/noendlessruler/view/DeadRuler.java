package com.kongdy.hasee.noendlessruler.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.kongdy.hasee.noendlessruler.Utils;

/**
 * 尺子，可动态绘制，需要调用增加方法
 * <h1>
 * 		基本尺，不可滑动
 * </h1>
 * @author wangk
 *
 */
public class DeadRuler extends View {

	private Paint mLabelPaint; // 尺子底部label画笔
	private Paint mRulingPaint; // 刻度画笔
	private TextPaint mUnitPaint; // 标注画笔
	
	
	private Paint defaultPaint;
	
	private NoEndlessRuler1.ORIENTATION mOrientation;
	
	private int mWidth;
	private int mHeight;
	
	private int unitSpace;
	
	private int maxValue;
	
	private int bottomLabelPadding;
	
	public DeadRuler(Context context) {
		super(context);
		init();
	}

	public DeadRuler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DeadRuler(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		setmOrientation(NoEndlessRuler1.ORIENTATION.VERTICAL);
		
		maxValue = 0;
		
		mLabelPaint = new Paint();
		mRulingPaint = new Paint();
		mUnitPaint = new TextPaint();
		defaultPaint = new Paint();

		mLabelPaint.setAntiAlias(true);
		mRulingPaint.setAntiAlias(true);
		mUnitPaint.setAntiAlias(true);

		defaultPaint.setAntiAlias(true);
		// 为了把控件独立起来，使用ps采集的rgb取色
		mLabelPaint.setColor(Color.argb(255, 158, 158, 158));
		mRulingPaint.setColor(Color.argb(255, 158, 158, 158));
		mUnitPaint.setColor(Color.argb(255, 87, 87, 87));
		
		mLabelPaint.setStrokeWidth(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 2));
		mRulingPaint.setStrokeWidth(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1));

		mUnitPaint.setTextAlign(Align.CENTER);
		
		mUnitPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_SP, 8));
		
		mLabelPaint.setStyle(Style.STROKE);
		mRulingPaint.setStyle(Style.STROKE);
		
		unitSpace = 20;
	}
	
	@Override
	public void layout(int l, int t, int r, int b) {
		if(mOrientation == NoEndlessRuler1.ORIENTATION.HORIZONTAL) {
			l = getPaddingLeft();
			r = (int) (Utils.screenWidth_*1.2)-getPaddingRight();
		} else {
			t = getPaddingTop();
			b =  (int) (Utils.screenHeight_*1.2)-getPaddingBottom();
		}
		super.layout(l, t, r, b);
	}

	public NoEndlessRuler1.ORIENTATION getmOrientation() {
		return mOrientation;
	}

	public void setmOrientation(NoEndlessRuler1.ORIENTATION mOrientation) {
		this.mOrientation = mOrientation;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);// 保留背景
		canvas.saveLayer(0, 0, mWidth, mHeight, defaultPaint, Canvas.CLIP_SAVE_FLAG);
		if(mOrientation ==	NoEndlessRuler1.ORIENTATION.HORIZONTAL ) {
			canvas.drawLine(0, mHeight, mWidth, mHeight, mLabelPaint);
			int tempWidth = mWidth;
			int count = 0;
			while(tempWidth > 0) {
				tempWidth = tempWidth-unitSpace;
				if(count % 5 != 0) {
					canvas.drawLine(mWidth-tempWidth,  (mHeight/3)*2, mWidth-tempWidth,mHeight-mLabelPaint.getStrokeWidth(), mRulingPaint);
				} else {
					canvas.drawLine(mWidth-tempWidth,  mHeight/2, mWidth-tempWidth,mHeight-mLabelPaint.getStrokeWidth(), mRulingPaint);
					canvas.drawText(count+"", mWidth-tempWidth, mHeight/5, mUnitPaint);
				}
				maxValue = count;
				count ++;
			}
		} else {
			canvas.drawLine(0, 0, 0, mHeight, mLabelPaint);
			int tempHeight = mHeight;
			int count = 0;
			while(tempHeight > 0) {
				tempHeight = tempHeight-unitSpace;
				if(count % 5 != 0) {
					canvas.drawLine(mLabelPaint.getStrokeWidth(), mHeight-tempHeight, mWidth/3, mHeight-tempHeight, mRulingPaint);
				} else {
					canvas.drawLine(mLabelPaint.getStrokeWidth(), mHeight-tempHeight, mWidth/2, mHeight-tempHeight, mRulingPaint);
					canvas.drawText(count+"", 4*(mWidth/5), mHeight-tempHeight, mUnitPaint);
				}
				maxValue = count;
				count ++;
			}
		}

		canvas.restore();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
	}
	
	/**
	 * 根据单位返回一个像素
	 * @param unit
	 * @param value
	 * @return
	 */
	public float getRawSize(int unit,float value) {
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		return TypedValue.applyDimension(unit, value, metrics);
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getBottomLabelPadding() {
		bottomLabelPadding = (int) mLabelPaint.getStrokeWidth();
		return bottomLabelPadding;
	}

	public void setBottomLabelPadding(int bottomLabelPadding) {
		this.bottomLabelPadding = bottomLabelPadding;
		mLabelPaint.setStrokeWidth(bottomLabelPadding);
	}
	
}
