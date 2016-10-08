package com.kongdy.hasee.noendlessruler.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 尺子,无限瀑布流
 * @author kongdy
 * @date 2016-07-12 20:13
 * @TIME 20:13
 **/

public class NoEndlessRuler1 extends FrameLayout {

    private int mHeight;
    private int mWidth;

    private ORIENTATION mOrientation;

    private boolean beginDraged = false;

    private float mLastMotionX;
    private float mLastMotionY;

    private float mTouSlop;// 最小滑动阀值
    private float mOverscrollDistance;

    private int mScrollY;
    private int mScrollX;

    private DeadRuler ruler;

    private int pointerId;

    public NoEndlessRuler1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NoEndlessRuler1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoEndlessRuler1(Context context) {
        super(context);
        init();
    }

    private void init() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouSlop = configuration.getScaledPagingTouchSlop();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();

        pointerId = 1;

        ruler = new DeadRuler(getContext());
        // 默认水平滑动
        setmOrientation(ORIENTATION.VERTICAL);

        addView(ruler, (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,50),
                ViewGroup.LayoutParams.MATCH_PARENT);

    }



    @Override
    protected void measureChildWithMargins(View child,
                                           int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child
                    .getLayoutParams();
        if (mOrientation == ORIENTATION.VERTICAL) {
            child.measure(parentWidthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    marginLayoutParams.topMargin
                            + marginLayoutParams.bottomMargin,
                    MeasureSpec.UNSPECIFIED));
        } else {
            child.measure(MeasureSpec.makeMeasureSpec(
                    marginLayoutParams.leftMargin
                            + marginLayoutParams.rightMargin,
                    MeasureSpec.UNSPECIFIED), parentHeightMeasureSpec);
        }
    }

    /**
     * <h1>
     * 滑动处理</h1> 触摸事件写的比较简陋，以后完善
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getRawX();
                mLastMotionY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 两套滑动机制
                int deltaX = 0;
                int deltaY = 0;
                int overScrollDistanceX = 0;
                int overScrollDistanceY = 0;
                int scrollRangeX = 0;
                int scrollRangeY = 0;
                if (mOrientation == ORIENTATION.HORIZONTAL) {
                    final float moveX = ev.getRawX();
                    deltaX = (int) (mLastMotionX - moveX);
                    if (!beginDraged && Math.abs(deltaX) > mTouSlop) {
                        beginDraged = true;
                    }
                    mScrollX = getScrollX() + deltaX;
                    mScrollY = getScrollY();
                    scrollRangeX = getScrollRangeX();
                    overScrollDistanceX = (int) mOverscrollDistance;
                } else {
                    final float moveY = ev.getRawY();
                    deltaY = (int) (mLastMotionY - moveY);
                    if (!beginDraged && Math.abs(deltaY) > mTouSlop) {
                        beginDraged = true;
                    }
                    mScrollX = getScrollX();
                    mScrollY = getScrollY() + deltaY;
                    scrollRangeY = getScrollRangeY();
                    overScrollDistanceY = (int) mOverscrollDistance;
                }
                if (beginDraged) {
                    overScrollBy(deltaX, deltaY, mScrollX, mScrollY, scrollRangeX,
                            scrollRangeY, overScrollDistanceX, overScrollDistanceY,
                            true);
                }
                break;
            case MotionEvent.ACTION_UP:
                beginDraged = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 滑动算法后期有待优化
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        // super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        super.scrollTo(scrollX, scrollY);
    }

    /**
     * 判断是否到最底部
     *
     * @return
     */
    private int getScrollRangeX() {
        int scrollRangX = 0;
        if (getChildCount() > 0 && mOrientation == ORIENTATION.HORIZONTAL) {
            View child = getChildAt(0);
            scrollRangX = Math.max(0, child.getWidth()
                    - (getWidth() - getPaddingLeft() - getPaddingRight()));
        }
        return scrollRangX;
    }

    /**
     * 判断是否到最右边
     *
     * @return
     */
    private int getScrollRangeY() {
        int scrollRangY = 0;
        if (getChildCount() > 0 && mOrientation == ORIENTATION.VERTICAL) {
            View child = getChildAt(0);
            scrollRangY = Math.max(0, child.getHeight()
                    - (getHeight() - getPaddingTop() - getPaddingBottom()));
        }
        return scrollRangY;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        drawPointer();
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(
                    "NoEndlessRuler can host only one direct child");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(
                    "NoEndlessRuler can host only one direct child");
        }
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(
                    "NoEndlessRuler can host only one direct child");
        }
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(
                    "NoEndlessRuler can host only one direct child");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(
                    "NoEndlessRuler can host only one direct child");
        }
        super.addView(child, params);
    }

    /**
     * 方向枚举
     *
     * @author wangk
     */
    public static enum ORIENTATION {
        VERTICAL, HORIZONTAL
    }

    public ORIENTATION getmOrientation() {
        return mOrientation;
    }

    public void setmOrientation(ORIENTATION mOrientation) {
        this.mOrientation = mOrientation;
        ruler.setmOrientation(mOrientation);
    }

    /**
     * 根据单位返回一个像素
     *
     * @param unit
     * @param value
     * @return
     */
    public float getRawSize(int unit, float value) {
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, metrics);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        View child = getChildAt(0);
        // 置中
        if (mOrientation == ORIENTATION.HORIZONTAL) {
            super.setScrollX((Math.abs(getWidth() - child.getWidth())) / 2);
        } else {
            super.setScrollY((Math.abs(getHeight() - child.getHeight()) / 2));
        }

    }

    /**
     * 绘制指针
     */
    private void drawPointer() {
        ruler = (DeadRuler) getChildAt(0);
        ViewGroup parent = ((ViewGroup)getParent());
        for (int i = 0; i < parent.getChildCount(); i++) {
            if(parent.getChildAt(i).getId() == pointerId) {
                return;
            }
        }
        View view = new View(getContext());
        view.setBackgroundColor(Color.argb(255, 250, 124, 0));
        view.setId(pointerId);
        int l = 0;
        int t = 0;
        int r = 0;
        int b = 0;
        if (mOrientation == ORIENTATION.HORIZONTAL) {
            l = (int) (getLeft() + mWidth / 2 - getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 1));
            t = getTop() + mHeight / 3;
            r = (int) (getLeft() + mWidth / 2 + getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 1));
            b = getBottom() - ruler.getBottomLabelPadding();
            parent.addView(view,new LayoutParams((int) getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 2), 2*(mHeight / 3)));
        } else {
            l = ruler.getBottomLabelPadding();
            t = (int) (getTop()+mHeight/2-getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 1));
            r = getRight()-mWidth/3;
            b = (int) (getBottom()-mHeight/2+getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 1));
            parent.addView(view,new LayoutParams(2*(mWidth/3), (int) getRawSize(
                    TypedValue.COMPLEX_UNIT_DIP, 2)));
        }

        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.leftMargin = l;
        marginLayoutParams.rightMargin = r;
        marginLayoutParams.topMargin = t;
        marginLayoutParams.bottomMargin = b;
        view.setLayoutParams(marginLayoutParams);
    }

}
