package com.lxf.ruler.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.lxf.ruler.R;
import com.lxf.ruler.utils.UiUtils;

/**
 * Created by luoxf on 2016/1/5.
 */
public class RulerView extends View{
    private final String TAG = RulerView.class.getSimpleName();
    private Context mContext;
    private Paint mLinePaint, mOuterPaint, mTextPaint, mBitmapPaint;
    private int mTotalWidth, mTotalHeight; //控件宽度，高度
    private static final int LINE_HEIGHT = 70; // 刻度尺高度
    private static final int RULER_MARGIN_LEFT_RIGHT = 10; // 距离左右间
    private static final int RULER_MARGIN_TOP_BOTTOM = 10; //距离上下间
    private static final int FIRST_LINE_MARGIN = 5;   // 第一条线距离边框距离
    private static final int DEFAULT_LINE_COUNT = 10; // 打算绘制的厘米数
    private static final int DEFAULT_LINE_MIDDLE_COUNT = 3; // 一个屏幕放3个

    private int mLineHeight; // 最高线的刻度尺高度
    private int mHalfLineHeight; //小线的刻度尺高度
    private int mMiddleLineHeight; // 中间线的刻度尺高度
    private int mRulerLeftRightMargin; //左右间隔
    private int mRulerTopBottomMargin; //上下间隔
    private int mFirstLineMargin; //第一条线间隔
    private int mLineDivider; //线与线之间的间隔
    private Rect mOutRect; // 外框区域
    private Rect mRulerSrcRect, mRulerDestRect;
    private Bitmap rulerBitmap; // 尺子标志图标
    private int mRulerWidth = 8;
    private int mRulerHeight = 8;
    private int touchSlop; //最小滑动距离
    private int lineColor;
    private Scroller mScroller;
    private int lastX;
    private int mWholeRulerWidth;

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initData();
    }

    /**
     * 初始化属性
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.RulerView);
        lineColor = a.getColor(R.styleable.RulerView_lineColor, Color.BLACK);
        mLineHeight = a.getDimensionPixelOffset(R.styleable.RulerView_lineHeight, LINE_HEIGHT);
        a.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mLinePaint = new Paint();// 初始绘制线的画笔
        mLinePaint.setAntiAlias(true);// 去除画笔锯齿
        mLinePaint.setStyle(Paint.Style.FILL);// 设置风格为实线
        mLinePaint.setColor(lineColor);// 设置画笔颜色

        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setColor(Color.BLACK);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(1);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mTextPaint.setAntiAlias(true);// 去除画笔锯齿
        mTextPaint.setStyle(Paint.Style.STROKE);// 设置风格为实线
        mTextPaint.setTextSize(10);
        mTextPaint.setColor(Color.BLACK);// 设置画笔颜色

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);
    }

    /**
     * 将dp转化为px,为了适配
     */
    private void initData() {
        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        this.mScroller = new Scroller(mContext);
        mHalfLineHeight = mLineHeight / 2;
        mMiddleLineHeight = mLineHeight /  3;
        mRulerLeftRightMargin = UiUtils.dipToPx(mContext, RULER_MARGIN_LEFT_RIGHT);
        mRulerTopBottomMargin = UiUtils.dipToPx(mContext, RULER_MARGIN_TOP_BOTTOM);
        mFirstLineMargin = UiUtils.dipToPx(mContext, FIRST_LINE_MARGIN);
        rulerBitmap = ((BitmapDrawable)mContext.getResources().getDrawable(R.mipmap.ruler)).getBitmap();
        mRulerWidth = UiUtils.dipToPx(mContext, mRulerWidth);
        mRulerHeight = UiUtils.dipToPx(mContext, mRulerHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOuter(canvas);
        drawLine(canvas);
        drawText(canvas);
        drawSymbol(canvas);
    }

    /**
     * 绘制处边框
     * @param canvas
     */
    private void drawOuter(Canvas canvas) {
        canvas.drawRect(mOutRect, mOuterPaint);
    }

    /**
     * 绘制线
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        canvas.save();
        //第一条线间隔
        canvas.translate((float) (mFirstLineMargin * 1.5 + mRulerLeftRightMargin), 0);
        int top = 0;
        for(int i = 1; i <= DEFAULT_LINE_COUNT * DEFAULT_LINE_COUNT; i++) {
            //绘制最长线
            if(i % 10 == 0 || i == 1) {
                top = mTotalHeight - mLineHeight;
            }
            //绘制中间线
            else if(i % 5 == 0) {
                top = mTotalHeight - mHalfLineHeight;
            }
            //绘制短线
            else {
                top = mTotalHeight - mMiddleLineHeight;
            }
            canvas.drawLine(0, top, 0, mTotalHeight - mRulerTopBottomMargin, mLinePaint);
            //增加相应的间隔
            canvas.translate(mLineDivider, 0);
        }
        canvas.restore();
    }

    /**
     * 绘制文本
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        canvas.save();
        //第一条线间隔
        canvas.translate((float) (mFirstLineMargin * 1.5 + mRulerLeftRightMargin) - 2, 0);
        for(int i = 1; i <= DEFAULT_LINE_COUNT * DEFAULT_LINE_COUNT; i++) {
            canvas.drawText(i + "", 0, mTotalHeight, mTextPaint);
            //增加相应的间隔
            canvas.translate(mLineDivider, 0);
        }
        canvas.restore();
    }

    /**
     * 画标志
     * @param canvas
     */
    private void drawSymbol(Canvas canvas) {
        canvas.save();
        //第一条线间隔
        canvas.translate((float) (mFirstLineMargin * 1.5 + mRulerLeftRightMargin) - 5, mRulerTopBottomMargin);
        canvas.drawBitmap(rulerBitmap, mRulerSrcRect, mRulerDestRect, mBitmapPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = w;
        mTotalHeight = h;
        mLineDivider = (int) Math.round((mTotalWidth - mRulerLeftRightMargin * 2 - mFirstLineMargin * 2) / (DEFAULT_LINE_COUNT * DEFAULT_LINE_MIDDLE_COUNT - 1.0));
        mRulerSrcRect = new Rect(0, 0, mRulerWidth, mRulerHeight);
        mRulerDestRect = new Rect(0, 0, mRulerWidth, mRulerHeight);
        mWholeRulerWidth =  (DEFAULT_LINE_COUNT * (DEFAULT_LINE_COUNT - DEFAULT_LINE_MIDDLE_COUNT)) * mLineDivider;
        mOutRect = new Rect(mRulerLeftRightMargin, mRulerTopBottomMargin, mWholeRulerWidth + mTotalWidth, mTotalHeight - mRulerTopBottomMargin);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if( (x > mRulerLeftRightMargin + mFirstLineMargin) && (x < mTotalWidth - mRulerLeftRightMargin- mFirstLineMargin )) {
                    int temp = (int) (x - (mRulerLeftRightMargin + mFirstLineMargin * 1.5)) - 5;
                    temp = temp > 0 ? temp : 0;
                    mRulerDestRect = new Rect(temp, 0, temp + mRulerWidth, mRulerHeight);
                    postInvalidate();
                    scrollerSmoothScrollBy(lastX - x, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        lastX = x;
        return true;
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
    }

    //调用此方法滚动到目标位置
    public void scrollerSmoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        scrollerSmoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void scrollerSmoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }
}
