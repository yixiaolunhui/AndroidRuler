

package com.rubin.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.seek.ruler.R;

import java.text.DecimalFormat;
import java.util.List;

public class RulerView extends View implements GestureDetector.OnGestureListener {
    /**
     * 尺子画笔
     */
    private Paint mRulerPaint;
    /**
     * 刻度值画笔
     */
    private TextPaint mTextPaint;
    /**
     * 当前选中的真实刻度index
     */
    private int mSelectedIndex = 0;
    /**
     * 当前选中刻度颜色
     */
    private int mHighlightColor = Color.RED;
    /**
     * 数字的颜色
     */
    private int mTextColor = Color.BLACK;
    /**
     * 尺子的颜色
     */
    private int mRulerColor = Color.BLACK;

    /**
     * 尺子的高度
     */
    private int mRulerHeight;

    /**
     * 尺子的宽度
     */
    private int mRulerWidth;
    /**
     * 待展现的所有刻度值
     */
    private List<String> mTextList;

    /**
     * 能够滚动的最大距离（尺子（可见）的宽度的一半,in px）
     */
    private float mMaxOverScrollDistance;
    /**
     * 能够滚动的最大刻度数量(尺子（可见）的刻度数量的一半)
     */
    private int mViewScopeSize;

    /**
     * 内容的长度（in px）
     */
    private float mContentLength;
    /**
     * 是否快速滑动
     */
    private boolean mFling = false;

    /**
     * 尺子的最大值与最小值
     */
    private float mMaxValue, mMinValue;
    /**
     * 相邻刻度间代表的刻度值
     */
    private float mIntervalValue = 1f;
    /**
     * 相邻刻度间的距离(in px)
     */
    private float mIntervalDistance = 0f;

    /**
     * 尺子上所有的刻度值的总数（包括暂时没有显示出来的）
     */
    private int mRulerCount;
    /**
     * 刻度值的字体大小
     */
    private float mTextSize;
    /**
     * 一个正常刻度的宽度
     */
    private float mRulerLineWidth;

    /**
     * 一个正常刻度的高度
     */
    private float mRulerLineHeight;
    /**
     * 刻度值小数点后保留多少位：支持0~3位
     */
    private int mRetainLength = 0;

    /**
     * 是否单独处理整10格的特殊刻度
     */
    private boolean mIsDivideByTen = true;

    /**
     * 整10格的特殊刻度的高度
     */
    private float mDivideByTenHeight;

    /**
     * 整10格的特殊刻度的宽度
     */
    private float mDivideByTenWidth;

    /**
     * 是否单独处理整5格的特殊刻度
     */
    private boolean mIsDivideByFive = false;
    /**
     * 整5格的特殊刻度的高度
     */
    private float mDivideByFiveHeight;
    /**
     * 整5格的特殊刻度的宽度
     */
    private float mDivideByFiveWidth;

    /**
     * 刻度值与尺子最长刻度之间的距离
     */
    private float mTextBaseLineDistance;

    private int mOrientation = 0;

    private OnValueChangeListener onValueChangeListener;

    public static final int HORIZONTAL = 0;//水平方向
    public static final int VERTICAL = 1;//垂直方向
    private Scroller mScroller;
    //手势
    private GestureDetectorCompat mGestureDetectorCompat;

    public interface OnValueChangeListener {
        void onChange(RulerView view, float value);
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        DisplayMetrics dm = getResources().getDisplayMetrics();


        mRulerLineWidth = dm.density * 2;
        mDivideByFiveWidth = dm.density * 3;
        mDivideByTenWidth = dm.density * 4;

        mRulerLineHeight = dm.density * 15;
        mDivideByFiveHeight = dm.density * 20;
        mDivideByTenHeight = dm.density * 30;

        mIntervalDistance = dm.density * 8;
        mTextSize = dm.scaledDensity * 15;


        TypedArray typedArray = attrs == null ? null : getContext()
                .obtainStyledAttributes(attrs, R.styleable.customRulerView);
        if (typedArray != null) {
            mOrientation = typedArray.getInt(R.styleable.customRulerView_rulerOrientation, HORIZONTAL);
            mHighlightColor = typedArray
                    .getColor(R.styleable.customRulerView_rulerHighlightColor,
                            mHighlightColor);
            mTextColor = typedArray.getColor(
                    R.styleable.customRulerView_rulerTextColor, mTextColor);
            mRulerColor = typedArray.getColor(R.styleable.customRulerView_rulerColor,
                    mRulerColor);
            mIntervalValue = typedArray
                    .getFloat(R.styleable.customRulerView_rulerIntervalValue,
                            mIntervalValue);
            mMaxValue = typedArray
                    .getFloat(R.styleable.customRulerView_rulerMaxValue,
                            mMaxValue);
            mMinValue = typedArray
                    .getFloat(R.styleable.customRulerView_rulerMinValue, mMinValue);
            mTextSize = typedArray.getDimension(
                    R.styleable.customRulerView_rulerTextSize,
                    mTextSize);
            mRulerLineWidth = typedArray.getDimension(
                    R.styleable.customRulerView_rulerLineWidth, mRulerLineWidth);
            mIntervalDistance = typedArray.getDimension(R.styleable.customRulerView_rulerIntervalDistance, mIntervalDistance);
            mRetainLength = typedArray.getInteger(R.styleable.customRulerView_rulerRetainLength, 0);


            mRulerLineHeight = typedArray.getDimension(
                    R.styleable.customRulerView_rulerLineHeight, mRulerLineHeight);

            mIsDivideByFive = typedArray.getBoolean(R.styleable.customRulerView_rulerIsDivideByFive, mIsDivideByFive);
            mDivideByFiveHeight = typedArray.getDimension(
                    R.styleable.customRulerView_rulerDivideByFiveHeight, mDivideByFiveHeight);
            mDivideByFiveWidth = typedArray.getDimension(
                    R.styleable.customRulerView_rulerDivideByFiveWidth, mDivideByFiveWidth);

            mIsDivideByTen = typedArray.getBoolean(R.styleable.customRulerView_rulerIsDivideByTen, mIsDivideByTen);
            mDivideByTenHeight = typedArray.getDimension(
                    R.styleable.customRulerView_rulerDivideByTenHeight, mDivideByTenHeight);
            mDivideByTenWidth = typedArray.getDimension(R.styleable.customRulerView_rulerDivideByTenWidth, mDivideByTenWidth);

            mTextBaseLineDistance = typedArray.getDimension(
                    R.styleable.customRulerView_rulerTextBaseLineDistance,
                    mTextBaseLineDistance);
        }
        typedArray.recycle();
        checkRulerLineParam();
        calculateTotal();

        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
        mScroller = new Scroller(getContext());

        mRulerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRulerPaint.setStrokeWidth(mRulerLineWidth);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);

        setSelectedIndex(mRulerCount / 2);
    }

    private void checkRulerLineParam() {
        float[] heights = new float[]{mRulerLineHeight, mDivideByFiveHeight, mDivideByTenHeight};
        float[] weights = new float[]{mRulerLineWidth, mDivideByFiveWidth, mDivideByTenWidth};
        //从小到大排序
        for (int i = 0; i < heights.length; i++) {
            float heightTemp;
            float weightTemp;
            for (int j = 0; j < heights.length - i - 1; j++) {
                if (heights[j] > heights[j + 1]) {
                    heightTemp = heights[j];
                    heights[j] = heights[j + 1];
                    heights[j + 1] = heightTemp;
                }
                if (weights[j] > weights[j + 1]) {
                    weightTemp = weights[j];
                    weights[j] = weights[j + 1];
                    weights[j + 1] = weightTemp;
                }
            }
        }
        mRulerLineHeight = heights[0];
        mDivideByFiveHeight = heights[1];
        mDivideByTenHeight = heights[2];

        mRulerLineWidth = weights[0];
        mDivideByFiveWidth = weights[1];
        mDivideByTenWidth = weights[2];

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    //测量宽度：处理MeasureSpec.UNSPECIFIED的情况
    private int measureWidth(int widthMeasureSpec) {

        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = MeasureSpec.getSize(widthMeasureSpec);

        //View的最小值与背景最小值两者中的最大值（宽度）
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

    //测量高度
    private int measureHeight(int heightMeasure) {
        int measureMode = MeasureSpec.getMode(heightMeasure);
        int measureSize = MeasureSpec.getSize(heightMeasure);
        int result = 0;
        if (mOrientation == HORIZONTAL) {
            result = (int) (mTextSize) * 4;
        } else {
            result = getSuggestedMinimumHeight();
        }
        switch (measureMode) {
            //设置了确切的高度
            case MeasureSpec.EXACTLY:
                result = Math.max(result, measureSize);
                break;
            //没有设置了确切的高度
            case MeasureSpec.AT_MOST:
                result = Math.min(result, measureSize);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 当 View 的大小改变时调用
     *
     * @param w    新的 view 宽度
     * @param h    新的 view 宽度
     * @param oldw 旧的view 宽度
     * @param oldh 旧的view 高度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw || h != oldh) {

            if (mOrientation == HORIZONTAL) {
                mRulerHeight = h;
                mMaxOverScrollDistance = w / 2.f;
            } else {
                mRulerWidth = w;
                mMaxOverScrollDistance = h / 2.f;
            }

            mContentLength = ((mMaxValue - mMinValue) / mIntervalValue)
                    * mIntervalDistance;
            mViewScopeSize = (int) Math.ceil(mMaxOverScrollDistance
                    / mIntervalDistance);
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int start = mSelectedIndex - mViewScopeSize;
        int end = mSelectedIndex + mViewScopeSize;
        if (mSelectedIndex == mMaxValue) {
            end += mViewScopeSize;
        } else if (mSelectedIndex == mMinValue) {
            start -= mViewScopeSize;
        }

        // 控制下各个刻度的宽度
        if (mDivideByTenWidth >= mIntervalDistance) {
            mRulerLineWidth = mIntervalDistance / 6;
            mDivideByFiveWidth = mIntervalDistance / 3;
            mDivideByTenWidth = mIntervalDistance / 2;
        }

        //水平方向
        if (mOrientation == HORIZONTAL) {
            float x = start * mIntervalDistance;
            //刻度线的最大高度
            float markHeight = mRulerHeight - mTextSize;
            //控制下各个刻度的高度
            if (mDivideByTenHeight + mTextBaseLineDistance > markHeight) {
                mRulerLineHeight = markHeight / 2;
                mDivideByFiveHeight = markHeight * 3 / 4;
                mDivideByTenHeight = markHeight;
                mTextBaseLineDistance = 0;
            }

            //start 可能小于0
            for (int i = start; i < end; i++) {
                if (mRulerCount > 0 && i >= 0 && i < mRulerCount) {
                    // 画线
                    int remainderBy2 = i % 2;
                    int remainderBy5 = i % 5;
                    //标记选中的刻度
                    if (i == mSelectedIndex) {
                        mRulerPaint.setColor(mHighlightColor);
                    } else {
                        mRulerPaint.setColor(mRulerColor);
                    }
                    //被10整除的刻度线
                    if (mIsDivideByTen && remainderBy2 == 0 && remainderBy5 == 0) {
                        mRulerPaint.setStrokeWidth(mDivideByTenWidth);
                        canvas.drawLine(x, 0, x, mDivideByTenHeight,
                                mRulerPaint);
                    }
                    //被5整除的刻度线
                    else if (mIsDivideByFive && remainderBy2 != 0 && remainderBy5 == 0) {
                        mRulerPaint.setStrokeWidth(mDivideByFiveWidth);
                        canvas.drawLine(x, 0, x,
                                mDivideByFiveHeight, mRulerPaint);
                    }
                    //正常的刻度
                    else {
                        mRulerPaint.setStrokeWidth(mRulerLineWidth);
                        canvas.drawLine(x, 0, x, mRulerLineHeight,
                                mRulerPaint);
                    }

                    mTextPaint.setColor(mTextColor);
                    //标记选中的刻度
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
                            text = new DecimalFormat("##0").format(i * mIntervalValue + mMinValue);
                        }
                        Rect rect = new Rect();
                        mTextPaint.getTextBounds(text, 0, text.length(), rect);
                        //文本的下边缘线中心位置
                        canvas.drawText(text, 0, text.length(), x, mDivideByTenHeight + rect.height() + mTextBaseLineDistance, mTextPaint);
                    }
                }
                x += mIntervalDistance;
            }
        }
        //竖直方向
        else {
            float y = start * mIntervalDistance;
            //刻度线的最大宽度
            float markWidth = mRulerWidth - mTextSize;
            //控制下各个刻度的高度
            if (mDivideByTenHeight + mTextBaseLineDistance > markWidth) {
                mRulerLineHeight = markWidth / 2;
                mDivideByFiveHeight = markWidth * 3 / 4;
                mDivideByTenHeight = markWidth;
                mTextBaseLineDistance = 0;
            }

            //start 可能小于0
            for (int i = start; i < end; i++) {
                if (mRulerCount > 0 && i >= 0 && i < mRulerCount) {
                    // 画线
                    int remainderBy2 = i % 2;
                    int remainderBy5 = i % 5;
                    //标记选中的刻度
                    if (i == mSelectedIndex) {
                        mRulerPaint.setColor(mHighlightColor);
                    } else {
                        mRulerPaint.setColor(mRulerColor);
                    }
                    //被10整除的刻度线
                    if (mIsDivideByTen && remainderBy2 == 0 && remainderBy5 == 0) {
                        mRulerPaint.setStrokeWidth(mDivideByTenWidth);
                        canvas.drawLine(0, y, mDivideByTenHeight, y,
                                mRulerPaint);
                    }
                    //被5整除的刻度线
                    else if (mIsDivideByFive && remainderBy2 != 0 && remainderBy5 == 0) {
                        mRulerPaint.setStrokeWidth(mDivideByFiveWidth);
                        canvas.drawLine(0, y,
                                mDivideByFiveHeight, y, mRulerPaint);
                    }
                    //正常的刻度
                    else {
                        mRulerPaint.setStrokeWidth(mRulerLineWidth);
                        canvas.drawLine(0, y, mRulerLineHeight, y,
                                mRulerPaint);
                    }

                    mTextPaint.setColor(mTextColor);
                    //标记选中的刻度
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
                            //上大下小
                            text = new DecimalFormat("##0").format(mMaxValue - i * mIntervalValue);
                        }
                        Rect rect = new Rect();
                        mTextPaint.getTextBounds(text, 0, text.length(), rect);
                        //文本的下边缘线中心位置
                        canvas.drawText(text, 0, text.length(), mDivideByTenHeight + rect.width() / 2 + mTextBaseLineDistance, y + rect.height() / 2 - mDivideByTenWidth / 2, mTextPaint);
                    }
                }
                y += mIntervalDistance;
            }
        }
    }

    /**
     * 格式化刻度值
     */
    private String format(float value) {
        switch (mRetainLength) {
            case 0:
                return new DecimalFormat("##0").format(value);
            case 1:
                return new DecimalFormat("##0.0").format(value);
            case 2:
                return new DecimalFormat("##0.00").format(value);
            case 3:
                return new DecimalFormat("##0.000").format(value);
            default:
                return new DecimalFormat("##0.0").format(value);
        }
    }

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
     * 滑动时调整尺子,使其指向整格
     */
    private void adjustPosition() {
        int scroll;
        if (mOrientation == HORIZONTAL) {
            scroll = getScrollX();
        } else {
            scroll = getScrollY();
        }
        float distance = mSelectedIndex * mIntervalDistance - scroll
                - mMaxOverScrollDistance;
        if (distance == 0) {
            return;
        }
        if (mOrientation == HORIZONTAL) {
            mScroller.startScroll(scroll, 0, (int) distance, 0);
        } else {
            mScroller.startScroll(0, scroll, 0, (int) distance);
        }
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            onValueChange();
            invalidate();
        } else {
            //滑动完成后，调整下刻度位置，使其指向整格
            if (mFling) {
                mFling = false;
                adjustPosition();
            }
        }
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

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        float scroll = 0;
        float distance = 0;
        if (mOrientation == HORIZONTAL) {
            scroll = getScrollX();
            distance = distanceX;
        } else {
            scroll = getScrollY();
            distance = distanceY;
        }

        //不要越界
        if (scroll + distance <= -mMaxOverScrollDistance) {
            distance = -(int) (scroll + mMaxOverScrollDistance);
        } else if (scroll + distance >= mContentLength - mMaxOverScrollDistance) {
            distance = (int) (mContentLength - mMaxOverScrollDistance - scroll);
        }

        if (distance == 0) {
            return true;
        }
        if (mOrientation == HORIZONTAL) {
            scrollBy((int) distance, 0);

        } else {
            scrollBy(0, (int) distance);
        }
        onValueChange();
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float scroll = 0;
        float velocity = 0;
        if (mOrientation == HORIZONTAL) {
            scroll = getScrollX();
            velocity = velocityX;
        } else {
            scroll = getScrollY();
            velocity = velocityY;
        }

        if (scroll < -mMaxOverScrollDistance
                || scroll > mContentLength - mMaxOverScrollDistance) {
            return false;
        }
        mFling = true;
        fling((int) -velocity / 2);
        return true;
    }

    private void fling(int velocity) {
        if (mOrientation == HORIZONTAL) {
            mScroller.fling(getScrollX(), 0, velocity, 0, (int) -mMaxOverScrollDistance, (int) (mContentLength - mMaxOverScrollDistance), 0, 0);
        } else {
            mScroller.fling(0, getScrollY(), 0, velocity, 0, 0, (int) -mMaxOverScrollDistance, (int) (mContentLength - mMaxOverScrollDistance));

        }
        //触发computeScroll
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 调用监听接口
     */
    private void onValueChange() {
        int offset = 0;

        if (mOrientation == HORIZONTAL) {
            offset = (int) (getScrollX() + mMaxOverScrollDistance);
        } else {
            offset = (int) (getScrollY() + mMaxOverScrollDistance);
        }
        int tempIndex = Math.round(offset / mIntervalDistance);
        tempIndex = clampSelectedIndex(tempIndex);
        mSelectedIndex = tempIndex;
        if (onValueChangeListener != null) {
            String str = null;
            if (mOrientation == HORIZONTAL) {

                str = format(mSelectedIndex * mIntervalValue
                        + mMinValue);
            } else {
                str = format(mMaxValue - mSelectedIndex * mIntervalValue
                );
            }
            float mValue = Float.parseFloat(str);
            onValueChangeListener.onChange(this, mValue);
        }
    }


    /**
     * 保证selectedIndex不越界
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
     * 滑动到指定刻度
     * selectedIndex：以最小刻度计算的索引
     */
    private void setSelectedIndex(int selectedIndex) {
        this.mSelectedIndex = clampSelectedIndex(selectedIndex);
        post(new Runnable() {
            @Override
            public void run() {
                int position = (int) (mSelectedIndex * mIntervalDistance - mMaxOverScrollDistance);
                if (mOrientation == HORIZONTAL) {
                    scrollTo(position, 0);
                } else {
                    scrollTo(0, position);
                }
                onValueChange();
                invalidate();

            }
        });
    }

    /**
     * 计算刻度值的总数
     */
    private void calculateTotal() {
        mRulerCount = (int) ((mMaxValue - mMinValue) / mIntervalValue) + 1;
    }

    /**
     * 设置当前刻度值
     */
    public void setSelectedValue(float selectedValue) {
        if (selectedValue < mMinValue) {
            selectedValue = mMinValue;
        } else if (selectedValue > mMaxValue) {
            selectedValue = mMaxValue;
        }
        int index = Math.round(((selectedValue - mMinValue) / mIntervalValue));

        if (mOrientation == VERTICAL) {
            index = mRulerCount - index - 1;
        }
        setSelectedIndex(index);
    }


    public void setMinValue(float mMinValue) {
        this.mMinValue = mMinValue;
        calculateTotal();
        invalidate();
    }

    public void setIntervalValue(float mIntervalValue) {
        this.mIntervalValue = mIntervalValue;
        calculateTotal();
        invalidate();
    }

    public void setIntervalDis(float mIntervalDis) {
        this.mIntervalDistance = mIntervalDis;
    }

    public void setHighlightColor(int mHighlightColor) {
        this.mHighlightColor = mHighlightColor;
    }


    public void setMarkTextColor(int mMarkTextColor) {
        this.mTextColor = mMarkTextColor;
    }


    public void setMarkColor(int mMarkColor) {
        this.mRulerColor = mMarkColor;
    }


    public void setTextList(List<String> mTextList) {
        this.mTextList = mTextList;
    }


    public void setMaxValue(float mMaxValue) {
        this.mMaxValue = mMaxValue;
        calculateTotal();
        invalidate();
    }

    public void setRetainLength(int retainLength) {
        //只支持保留1~3位小数点
        if (retainLength < 1) {
            retainLength = 1;
        } else if (retainLength > 3) {
            retainLength = 3;
        }
        this.mRetainLength = retainLength;
        invalidate();
    }

    public void setOnValueChangeListener(
            OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

}
