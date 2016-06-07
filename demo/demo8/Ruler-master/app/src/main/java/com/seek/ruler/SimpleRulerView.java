/*
 * Copyright (C) 2016 (@seek 951882080@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seek.ruler;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

public class SimpleRulerView extends View implements
		GestureDetector.OnGestureListener {
	/**
	 * the ruler line paint
	 */
	private Paint mRulerPaint;
	/**
	 * the buttom text paint
	 */
	private TextPaint mTextPaint;
	/**
	 * current selected index
	 */
	private int mSelectedIndex = -1;
	/**
	 * the selected color
	 */
	private int mHighlightColor = Color.RED;
	/**
	 * the text color
	 */
	private int mTextColor = Color.BLACK;
	/**
	 * the ruler line color
	 */
	private int mRulerColor = Color.BLACK;

	/**
	 * view height
	 */
	private int mHeight;
	/**
	 * texts for show
	 */
	private List<String> mTextList;

	/**
	 * scroll helper
	 */
	private OverScroller mScroller;
	/**
	 * the max distance be allowed to scroll
	 */
	private float mMaxOverScrollDistance;
	/**
	 * just for help to calculate
	 */
	private float mContentWidth;
	/**
	 * fling or not
	 */
	private boolean mFling = false;
	/**
	 * the gesture detector help us to handle
	 */
	private GestureDetectorCompat mGestureDetectorCompat;

	/**
	 * the max and min value this view could draw
	 */
	private float mMaxValue, mMinValue;
	/**
	 * the difference value between two adjacent value
	 */
	private float mIntervalValue = 1f;
	/**
	 * the difference distance of two adjacent value
	 */
	private float mIntervalDis = 0f;

	/**
	 * the total of ruler line
	 */
	private int mRulerCount;
	/**
	 * text size
	 */
	private float mTextSize;
	/**
	 * ruler line width
	 */
	private float mRulerLineWidth;

	/**
	 * half width
	 */
	private int mViewScopeSize;

	private OnValueChangeListener onValueChangeListener;

	public interface OnValueChangeListener {
		/**
		 * when the current selected index changed will call this method
		 * 
		 * @param view
		 *            the SimplerulerView
		 * @param position
		 *            the selected index
		 * @param value
		 *            represent the selected value
		 */
		void onChange(SimpleRulerView view, int position, float value);
	}

	/**
	 * Constructor that is called when inflating SimpleRulerView from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public SimpleRulerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	/**
	 * simple constructor to use when creating a SimpleRulerView from code
	 * 
	 * @param context
	 */
	public SimpleRulerView(Context context) {
		this(context, null);
	}

	/**
	 * set default
	 * 
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mIntervalDis = dm.density * 5;

		mRulerLineWidth = dm.density * 2;
		mTextSize = dm.scaledDensity * 14;


		TypedArray typedArray = attrs == null ? null : getContext()
				.obtainStyledAttributes(attrs, R.styleable.simpleRulerView);
		if (typedArray != null) {
			mHighlightColor = typedArray
					.getColor(R.styleable.simpleRulerView_highlightColor,
							mHighlightColor);
			mTextColor = typedArray.getColor(
					R.styleable.simpleRulerView_textColor, mTextColor);
			mRulerColor = typedArray.getColor(R.styleable.simpleRulerView_rulerColor,
					mRulerColor);
			mIntervalValue = typedArray
					.getFloat(R.styleable.simpleRulerView_intervalValue,
							mIntervalValue);
			mMaxValue = typedArray
					.getFloat(R.styleable.simpleRulerView_maxValue,
							mMaxValue);
			mMinValue = typedArray
					.getFloat(R.styleable.simpleRulerView_minValue,
							mMinValue);
			mTextSize = typedArray.getDimension(
					R.styleable.simpleRulerView_textSize,
					mTextSize);
			mRulerLineWidth = typedArray.getDimension(
					R.styleable.simpleRulerView_rulerLineWidth, mRulerLineWidth);
			mIntervalDis = typedArray.getDimension(R.styleable.simpleRulerView_intervalDistance,mIntervalDis);
			retainLength = typedArray.getInteger(R.styleable.simpleRulerView_retainLength,0);
		}

		calculateTotal();

		mRulerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRulerPaint.setStrokeWidth(mRulerLineWidth);

		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(mTextSize);

		mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
		mScroller = new OverScroller(getContext(), new DecelerateInterpolator());

		setSelectedIndex(0);
	}

	/**
	 * we mesure by ourselves
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int widthMeasureSpec) {
		int measureMode = MeasureSpec.getMode(widthMeasureSpec);
		int measureSize = MeasureSpec.getSize(widthMeasureSpec);
		int result = getSuggestedMinimumWidth();
		switch (measureMode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = measureSize;
			break;
		default:
			break;
		}
		return result;
	}

	private int measureHeight(int heightMeasure) {
		int measureMode = MeasureSpec.getMode(heightMeasure);
		int measureSize = MeasureSpec.getSize(heightMeasure);
		int result = (int) (mTextSize)*5;
		switch (measureMode) {
		case MeasureSpec.EXACTLY:
			result = Math.max(result, measureSize);
			break;
		case MeasureSpec.AT_MOST:
			result = Math.min(result, measureSize);
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			mHeight = h;
			mMaxOverScrollDistance = w / 2.f;
			mContentWidth = ((mMaxValue - mMinValue) / mIntervalValue)
					* mIntervalDis;
			mViewScopeSize = (int) Math.ceil(mMaxOverScrollDistance
					/ mIntervalDis);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/**
		 *  here is the start         the selected            the end 
		 *  /                        /                       /
		 * ||||||||||||||||||||||||||||||||||||||||||||||||||
		 * |    |    |    |    |    |    |    |    |   |    |
		 *                          0         1        2 
		 * \____mViewScopeSize_____/
		 * 
		 */
		int start = mSelectedIndex - mViewScopeSize;
		int end = mSelectedIndex + mViewScopeSize;

		start = Math.max(start, -mViewScopeSize * 2);
		end = Math.min(end, mRulerCount + mViewScopeSize * 2);

		if (mSelectedIndex == mMaxValue) {
			end += mViewScopeSize;
		} else if (mSelectedIndex == mMinValue) {
			start -= mViewScopeSize;
		}

		float x = start * mIntervalDis;
		float markHeight = mHeight - mTextSize;

		for (int i = start; i < end; i++) {

			// draw line
			int remainderBy2 = i % 2;
			int remainderBy5 = i % 5;
			if (i == mSelectedIndex) {
				mRulerPaint.setColor(mHighlightColor);
			} else {
				mRulerPaint.setColor(mRulerColor);
			}
			if (remainderBy2 == 0 && remainderBy5 == 0) {
				canvas.drawLine(x, 0, x, 0 + markHeight,
						mRulerPaint);
			} else if (remainderBy2 != 0 && remainderBy5 == 0) {
				canvas.drawLine(x, 0, x,
						markHeight * 3 / 4, mRulerPaint);
			} else {
				canvas.drawLine(x, 0, x, markHeight / 2,
						mRulerPaint);
			}

			// draw text
			if (mRulerCount > 0 && i >= 0 && i < mRulerCount) {
				mTextPaint.setColor(mTextColor);
				if (mSelectedIndex == i) {
					mTextPaint.setColor(mHighlightColor);
				}
				if (i % 10 == 0) {
					String text = null;
					if (mTextList != null && mTextList.size() > 0) {
						int index = i / 10;
						if (index < mTextList.size()) {
							text = mTextList.get(index);
						} else {
							text = "";
						}

					} else {
						text = format(i * mIntervalValue + mMinValue);
					}
					canvas.drawText(text, 0, text.length(), x, mHeight, mTextPaint);
				}
			}
			x += mIntervalDis;
		}
	}

	/**
	 * remain the text length
	 */
	private int retainLength = 0;

	public int getRetainLength() {
		return retainLength;
	}

	/**
	 * set the remain length that can be good look
	 * 
	 * @param retainLength
	 */
	public void setRetainLength(int retainLength) {
		if (retainLength < 1 || retainLength > 3) {
			throw new IllegalArgumentException(
					"retainLength beyond expected,only support in [0,3],but now you set "
							+ retainLength);
		}
		this.retainLength = retainLength;
		invalidate();
	}

	/**
	 * format the text
	 * 
	 * @param fvalue
	 * @return
	 */
	private String format(float fvalue) {
		switch (retainLength) {
		case 0:
			return new DecimalFormat("##0").format(fvalue);
		case 1:
			return new DecimalFormat("##0.0").format(fvalue);
		case 2:
			return new DecimalFormat("##0.00").format(fvalue);
		case 3:
			return new DecimalFormat("##0.000").format(fvalue);
		default:
			return new DecimalFormat("##0.0").format(fvalue);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean resolve = mGestureDetectorCompat.onTouchEvent(event);
		if (!mFling && MotionEvent.ACTION_UP == event.getAction()) {
			adjustPosition();
			resolve = true;
		}
		return resolve || super.onTouchEvent(event);
	}

	/**
	 * we hope that after every scroll the selected should be legal
	 */
	private void adjustPosition() {
		int scrollX = getScrollX();
		float dx = mSelectedIndex * mIntervalDis - scrollX
				- mMaxOverScrollDistance;
		mScroller.startScroll(scrollX, 0, (int) dx, 0);
		postInvalidate();
	}

	/**
	 * clamp selected index in bounds.
	 * 
	 * @param selectedIndex
	 * @return
	 */
	private int clampSelectedIndex(int selectedIndex) {
		if (selectedIndex < 0) {
			selectedIndex = 0;
		} else if (selectedIndex > mRulerCount) {
			selectedIndex = mRulerCount - 1;
		}
		return selectedIndex;
	}

	/**
	 * refresh current selected index
	 * 
	 * @param offsetX
	 */
	private void refreshSelected(int offsetX) {
		int offset = (int) (offsetX + mMaxOverScrollDistance);
		int tempIndex = Math.round(offset / mIntervalDis);
		tempIndex = clampSelectedIndex(tempIndex);
		if (mSelectedIndex == tempIndex) {
			return;
		}
		mSelectedIndex = tempIndex;
		// dispatch the selected index
		if (null != onValueChangeListener) {
			onValueChangeListener.onChange(
					this,
					mSelectedIndex,
					Float.parseFloat(format(mSelectedIndex * mIntervalValue
							+ mMinValue)));
		}
	}

	private void refreshSelected() {
		refreshSelected(getScrollX());
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(false);
		}
		mFling = false;
		if (null != getParent()) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	/**
	 * allowed to tab up to select
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		playSoundEffect(SoundEffectConstants.CLICK);
		refreshSelected((int) (getScrollX() + e.getX() - mMaxOverScrollDistance));
		adjustPosition();
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			// if the content disappear from sight ,we should be interrupt
			 float scrollX = getScrollX();
			 if (scrollX < -mMaxOverScrollDistance
			 || scrollX > mContentWidth-mMaxOverScrollDistance) {
			 mScroller.abortAnimation();
			 }
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			refreshSelected();
			invalidate();
		} else {
			if (mFling) {
				mFling = false;
				adjustPosition();
			}
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float mOffsetX = distanceX;
		float scrollX = getScrollX();
		if (scrollX < -mMaxOverScrollDistance) {
			mOffsetX = distanceX / 4.f;
		} else if (scrollX > mContentWidth - mMaxOverScrollDistance) {
			mOffsetX = distanceX / 4.f;
		}
		scrollBy((int) mOffsetX, 0);
		refreshSelected();
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float scrollX = getScrollX();
		// if current position is the boundary, we do not fling
		if (scrollX < -mMaxOverScrollDistance
				|| scrollX > mContentWidth - mMaxOverScrollDistance)
			return false;
		
		mFling = true;
		fling((int) -velocityX);
		return true;
	}

	private void fling(int velocityX) {
		mScroller.fling(getScrollX(), 0, velocityX / 3, 0,
				(int) -mMaxOverScrollDistance,
				(int) (mContentWidth - mMaxOverScrollDistance), 0, 0,
				(int) (mMaxOverScrollDistance / 4), 0);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getmSelectedIndex() {
		return mSelectedIndex;
	}

	/**
	 * set the the selectedIndex be the current selected
	 * 
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		this.mSelectedIndex = selectedIndex;
		post(new Runnable() {
			@Override
			public void run() {
				scrollTo(
						(int) (mSelectedIndex * mIntervalDis - mMaxOverScrollDistance),
						0);
				invalidate();
				refreshSelected();
			}
		});
	}

	/**
	 * see {@link #setSelectedIndex(int)}
	 * 
	 * @param selectedValue
	 */
	public void setSelectedValue(float selectedValue) {
		if (selectedValue < mMinValue || selectedValue > mMaxValue) {
			throw new IllegalArgumentException("expected selectedValue in ["
					+ mMinValue + "," + mMaxValue
					+ "],but the selectedValue is " + selectedValue);
		}
		setSelectedIndex((int) ((selectedValue - mMinValue) / mIntervalValue));
	}

	public int getHighlightColor() {
		return mHighlightColor;
	}

	public void setHighlightColor(int mHighlightColor) {
		this.mHighlightColor = mHighlightColor;
	}

	public int getMarkTextColor() {
		return mTextColor;
	}

	public void setMarkTextColor(int mMarkTextColor) {
		this.mTextColor = mMarkTextColor;
	}

	public int getMarkColor() {
		return mRulerColor;
	}

	public void setMarkColor(int mMarkColor) {
		this.mRulerColor = mMarkColor;
	}

	public List<String> getTextList() {
		return mTextList;
	}

	/**
	 * if you don't want the default text, you can custom them
	 * 
	 * @param mTextList
	 */
	public void setTextList(List<String> mTextList) {
		this.mTextList = mTextList;
	}

	public float getMaxValue() {
		return mMaxValue;
	}

	/**
	 * set the max value to the ruler
	 * 
	 * @param mMaxValue
	 */
	public void setMaxValue(float mMaxValue) {
		this.mMaxValue = mMaxValue;
		calculateTotal();
		invalidate();
	}

	/**
	 * calculate the ruler-line's amount by the maximum and the minimum value
	 */
	private void calculateTotal() {
		mRulerCount = (int) ((mMaxValue - mMinValue) / mIntervalValue) + 1;
	}

	public float getMinValue() {
		return mMinValue;
	}

	/**
	 * set the min value to the ruler
	 * 
	 * @param mMinValue
	 */
	public void setMinValue(float mMinValue) {
		this.mMinValue = mMinValue;
		calculateTotal();
		invalidate();
	}

	public float getIntervalValue() {
		return mIntervalValue;
	}

	public void setIntervalValue(float mIntervalValue) {
		this.mIntervalValue = mIntervalValue;
		calculateTotal();
		invalidate();
	}

	public float getIntervalDis() {
		return mIntervalDis;
	}

	public void setIntervalDis(float mIntervalDis) {
		this.mIntervalDis = mIntervalDis;
	}

	public OnValueChangeListener getOnValueChangeListener() {
		return onValueChangeListener;
	}

	public void setOnValueChangeListener(
			OnValueChangeListener onValueChangeListener) {
		this.onValueChangeListener = onValueChangeListener;
	}

}
