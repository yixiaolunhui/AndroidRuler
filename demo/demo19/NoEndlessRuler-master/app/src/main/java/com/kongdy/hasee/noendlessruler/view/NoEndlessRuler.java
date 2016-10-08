package com.kongdy.hasee.noendlessruler.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.kongdy.hasee.noendlessruler.R;


/**
 * @author kongdy
 *         on 2016/8/10
 *         尺子控件
 *         <h1>
 *         无限尺寸的尺子，后期开发
 *         </h1>
 */
public class NoEndlessRuler extends View {

    private final static String TAG = "NoEndlessRuler";

    // 滑动计算
    private Scroller scroller;

    private int mHeight;
    private int mWidth;
    private int pointerWidth;
    private int pointerHeight;
    private int rulerDistance;
    private int rulerSpace;

    private float rulerTextSize;

    private int maxValue = -1;
    private int minValue;
    private int currentValue;
    private int scrollOffset; // 滑动偏移量

    private ORIENTATION mOrientation;

    /**
     * 刻度画笔
     **/
    private Paint rulerPaint;
    /**
     * 刻度数字
     **/
    private TextPaint rulerTextPaint;
    /**
     * 底部line
     **/
    private Paint labelPaint;
    /**
     * 指针
     **/
    private Bitmap pointer;

    private float lastMotionX;
    private float lastMotionY;

    private int scrollX;
    private int scrollY;

    /**
     * 滑动惯性持续时间
     */
    private static final int SCROLL_DURATION = 300;

    private GestureDetector gestureDetector;

    private final static int MESSAGE_SCROLL = 1;
    private final static int MESSAGE_JUSTIFY = 2;
    /**
     * 最小滑动阀值
     **/
    private final static int MIN_SCROLL_VALUE = 1;

    /**
     * 指针资源id
     **/
    private int pointId;

    private OnRulerListener onRulerListener;

    public NoEndlessRuler(Context context) {
        super(context);
        init(null);
    }

    public NoEndlessRuler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NoEndlessRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoEndlessRuler(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NoEndlessRuler);
        int orientation = a.getInteger(R.styleable.NoEndlessRuler_ner_orientation, 2);
        pointId = a.getResourceId(R.styleable.NoEndlessRuler_ner_point_style, -1);
        a.recycle();

        if (orientation == 1) {
            mOrientation = ORIENTATION.VERTICAL;
        } else {
            mOrientation = ORIENTATION.HORIZONTAL;
        }

        rulerPaint = new Paint();
        labelPaint = new Paint();
        rulerTextPaint = new TextPaint();

        rulerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        rulerTextPaint.setTextAlign(Paint.Align.CENTER);

        rulerPaint.setStrokeWidth(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1));
        labelPaint.setStrokeWidth(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 2));
        rulerTextPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_SP, 8));

        rulerPaint.setColor(Color.BLACK);
        labelPaint.setColor(Color.BLACK);
        rulerTextPaint.setColor(Color.BLACK);

        paintInit(rulerPaint);
        paintInit(labelPaint);
        paintInit(rulerTextPaint);

        scroller = new Scroller(getContext());
        scroller.setFriction(0.05f); // 摩擦力

        gestureDetector = new GestureDetector(getContext(), onGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
    }

    private void paintInit(Paint paint) {
        paint.setAntiAlias(true); // 锯齿
        paint.setFilterBitmap(true); // 滤波
        paint.setDither(true); // 防抖
        paint.setSubpixelText(true); // 像素自处理
    }

    private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            int mVelocityX = mOrientation == ORIENTATION.VERTICAL ? 0 : (int) -velocityX;
            int mVelocityY = mOrientation == ORIENTATION.HORIZONTAL ? 0 : (int) -velocityY;
            scrollX = 0;
            scrollY = 0;
            scroller.fling(0, 0, mVelocityX, mVelocityY, -0x7FFFFFFF, 0x7FFFFFFF, -0x7FFFFFFF, 0x7FFFFFFF);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    /**
     * 滑动处理
     */
    private Handler slideHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int mScrollX = scroller.getCurrX();
            int mScrollY = scroller.getCurrY();
            int deltaX = scrollX - mScrollX;
            int deltaY = scrollY - mScrollY;
            scrollX = mScrollX;
            scrollY = mScrollY;

            doScroll(deltaX, deltaY);

            if (Math.abs(mScrollX - scroller.getFinalX()) < MIN_SCROLL_VALUE && mOrientation == ORIENTATION.HORIZONTAL) {
                scrollX = scroller.getFinalX();
                scroller.forceFinished(true);
            } else if (Math.abs(mScrollY - scroller.getFinalY()) < MIN_SCROLL_VALUE && mOrientation == ORIENTATION.VERTICAL) {
                scrollY = scroller.getFinalY();
                scroller.forceFinished(true);
            }

            if (!scroller.isFinished()) {
                setNextMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
                setNextMessage(MESSAGE_JUSTIFY);
            } else {
                finished();
            }
            return true;
        }
    });

    private void justify() {
        if (needScroll()) {
            return;
        }
        if (Math.abs(scrollOffset) > MIN_SCROLL_VALUE) {
            int scrollX;
            int scrollY;
            if (scrollOffset < -rulerSpace / 2) {
                scrollX = mOrientation == ORIENTATION.VERTICAL ? 0 : scrollOffset + rulerSpace;
                scrollY = mOrientation == ORIENTATION.HORIZONTAL ? 0 : scrollOffset + rulerSpace;
            } else if (scrollOffset > rulerSpace / 2) {
                scrollX = mOrientation == ORIENTATION.VERTICAL ? 0 : scrollOffset - rulerSpace;
                scrollY = mOrientation == ORIENTATION.HORIZONTAL ? 0 : scrollOffset - rulerSpace;
            } else {
                scrollX = mOrientation == ORIENTATION.VERTICAL ? 0 : scrollOffset;
                scrollY = mOrientation == ORIENTATION.HORIZONTAL ? 0 : scrollOffset;
            }
            scrollXY(scrollX, scrollY, 0);
        }

    }


    private void finished() {
        if (needScroll()) {
            return;
        }
        scrollOffset = 0;
        invalidate();
    }


    /**
     * 开始滑动
     *
     * @param deltaX
     * @param deltaY
     */
    private void doScroll(int deltaX, int deltaY) {
        if (deltaX == 0 && deltaY == 0) {
            return;
        }
        if (mOrientation == ORIENTATION.VERTICAL) {
            scrollOffset += deltaY;
        } else {
            scrollOffset += deltaX;
        }

        int offsetCount = scrollOffset / rulerSpace;
        if (0 != offsetCount) {
            currentValue -= offsetCount;
            scrollOffset -= offsetCount * rulerSpace;
            if (onRulerListener != null) {
                int value = currentValue;
                if(currentValue > maxValue) {
                    value = maxValue;
                } else if(currentValue <= minValue){
                    value = minValue;
                }
                onRulerListener.onValueChanged(value);
            }
        }

        invalidate();
    }

    private void setNextMessage(int MESSAGE) {
        clearMessage();
        slideHandler.sendEmptyMessage(MESSAGE);
    }

    private void clearMessage() {
        slideHandler.removeMessages(MESSAGE_SCROLL);
        slideHandler.removeMessages(MESSAGE_JUSTIFY);
    }


    private void scrollX(int distance, int duration) {
        scrollX = 0;
        scroller.forceFinished(true);
        scroller.startScroll(0, 0, distance, 0, duration != 0 ? duration : SCROLL_DURATION);
        setNextMessage(MESSAGE_SCROLL);
    }

    private void scrollY(int distance, int duration) {
        scrollY = 0;
        scroller.forceFinished(true);
        scroller.startScroll(0, 0, 0, distance, duration != 0 ? duration : SCROLL_DURATION);
        setNextMessage(MESSAGE_SCROLL);
    }

    private void scrollXY(int distanceX, int distanceY, int duration) {
        scrollX = 0;
        scrollY = 0;
        scroller.forceFinished(true);
        scroller.startScroll(0, 0, distanceX, distanceY, duration != 0 ? duration : SCROLL_DURATION);
        setNextMessage(MESSAGE_SCROLL);
    }

    /**
     * 是否可以继续滑动
     *
     * @return
     */
    private boolean needScroll() {
        int outRange = 0;
        if (currentValue < minValue) {
            outRange = (minValue - currentValue) * rulerSpace;
        } else if (currentValue > maxValue) {
            outRange = (maxValue - currentValue) * rulerSpace;
        }
        if (0 != outRange) {
            scrollOffset = 0;
            if (mOrientation == ORIENTATION.VERTICAL) {
                scrollY(outRange, 100);
            } else {
                scrollX(outRange, 100);
            }
            return true;
        }

        return false;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h - getPaddingBottom() - getPaddingTop();
        initProperty();
    }

    private void initProperty() {

        float defaultMinDistance = getRawSize(TypedValue.COMPLEX_UNIT_DIP, 2);
        float defaultMaxDistance = mOrientation == ORIENTATION.VERTICAL ? 2 * mWidth / 3 : 2 * mHeight / 3;

        pointerWidth = (int) (mOrientation == ORIENTATION.VERTICAL ? defaultMaxDistance : defaultMinDistance);
        pointerHeight = (int) (mOrientation == ORIENTATION.HORIZONTAL ? defaultMaxDistance : defaultMinDistance);

        // 设置pointer样式
        if (pointId == -1) {
            Paint tempPaint = new Paint();
            tempPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            tempPaint.setColor(Color.rgb(250, 124, 0)); // 默认橘黄色
            paintInit(tempPaint);
            Bitmap result = Bitmap.createBitmap(pointerWidth, pointerHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawRect(0, 0, pointerWidth, pointerHeight, tempPaint);
            pointer = result;
        } else {
            pointer = BitmapFactory.decodeResource(getResources(), pointId);
            pointerWidth = pointer.getWidth();
            pointerHeight = pointer.getHeight();
            // TODO: 2016/8/10 do some size fit
//                     int outWidth = pointer.getWidth();
//            int outHeight = pointer.getHeight();
//
//            float scale;
//            float offsetX = 0;
//            float offsetY = 0;
//
//            if(outWidth/pointerWidth > outHeight/pointerHeight) {
//                scale = outWidth/pointerWidth;
//                offsetX = (pointerWidth-outWidth*scale)*0.5f;
//            } else {
//                scale = outHeight/pointerHeight;
//                offsetY = (pointerHeight-outHeight*scale)*0.5f;
//            }
//
//            Matrix pointerMatrix = new Matrix();
//            pointerMatrix.set(null);
//
//            pointerMatrix.postScale(scale,scale);
//            pointerMatrix.postTranslate(offsetX,offsetY);

        }

        // 计算尺寸
        if (maxValue == -1) {
            maxValue = 200;
        }
        // 分割线间隔默认为4dp
        rulerSpace = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 4);
        rulerDistance = (mOrientation == ORIENTATION.VERTICAL ? mWidth : mHeight) / 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.saveLayer(getPaddingLeft(), getPaddingTop(), getMeasuredWidth(), getMeasuredHeight(), rulerPaint, Canvas.ALL_SAVE_FLAG);

        int halfCount = (int) Math.ceil((mOrientation == ORIENTATION.VERTICAL?mHeight:mWidth) / 2f / rulerSpace);

        if (mOrientation == ORIENTATION.VERTICAL) {
            drawVertical(canvas, halfCount);
        } else {
            drawHorizontal(canvas, halfCount);
        }
        drawPointer(canvas);
        canvas.restore();
    }

    /**
     * 绘制指针
     *
     * @param canvas
     */
    private void drawPointer(Canvas canvas) {
        if (mOrientation == ORIENTATION.VERTICAL) {
            canvas.drawBitmap(pointer, 0, mHeight / 2-pointerHeight/2, rulerPaint);
        } else {
            canvas.drawBitmap(pointer, mWidth/ 2-pointerWidth/2, mHeight / 3, rulerPaint);
        }
    }

    /**
     * 画水平尺子
     */
    private void drawHorizontal(Canvas canvas, int halfCount) {
        canvas.drawLine(getPaddingLeft(), mHeight, mWidth, mHeight, labelPaint);
        float startX;
        int value;
        for (int i = 0; i < halfCount; i++) {
            // right
            startX = scrollOffset + i * rulerSpace + mWidth / 2f;
            value = currentValue + i;
            if (startX <= mWidth && value >= minValue && value <= maxValue) {
                if (value % 5 == 0 || value == 0) {
                    canvas.drawLine(startX, mHeight - labelPaint.getStrokeWidth() / 2, startX,
                            mHeight - (rulerDistance * 4) / 3 - labelPaint.getStrokeWidth() / 2, rulerPaint);
                    canvas.drawText(String.valueOf(value), startX, rulerTextPaint.getFontSpacing(),
                            rulerTextPaint);
                } else {
                    canvas.drawLine(startX, mHeight - labelPaint.getStrokeWidth() / 2, startX,
                            mHeight - rulerDistance - labelPaint.getStrokeWidth() / 2, rulerPaint);
                }
            }

            // left
            startX = mWidth / 2f - i * rulerSpace + scrollOffset;
            value = currentValue - i;
            if (startX >= getPaddingLeft() && value >= minValue && value <= maxValue) {
                if (value % 5 == 0 || value == 0) {
                    canvas.drawLine(startX, mHeight - labelPaint.getStrokeWidth() / 2, startX,
                            mHeight - (rulerDistance * 4) / 3 - labelPaint.getStrokeWidth() / 2, rulerPaint);
                    canvas.drawText(String.valueOf(value), startX, rulerTextPaint.getFontSpacing(),
                            rulerTextPaint);
                } else {
                    canvas.drawLine(startX, mHeight - labelPaint.getStrokeWidth() / 2, startX,
                            mHeight - rulerDistance - labelPaint.getStrokeWidth() / 2, rulerPaint);
                }
            }

        }
    }

    /**
     * 画垂直尺子
     */
    private void drawVertical(Canvas canvas, int halfCount) {
        canvas.drawLine(getPaddingLeft(), getPaddingTop(), 0, mHeight, labelPaint);

        // bottom
        float startY;
        int value;
        for (int i = 0; i < halfCount; i++) {
            value = currentValue + i;
            startY = i * rulerSpace + mHeight / 2 + scrollOffset;

            if (startY <= mHeight && value >= minValue && value <= maxValue) {
                if (value % 5 == 0 || value == 0) {
                    canvas.drawLine(labelPaint.getStrokeWidth() / 2, startY, labelPaint.getStrokeWidth() / 2 +
                            (rulerDistance * 4) / 3, startY, rulerPaint);
                    canvas.drawText(String.valueOf(value), mWidth-rulerTextPaint.measureText(String.valueOf(maxValue))
                            , startY, rulerTextPaint);
                } else {
                    canvas.drawLine(labelPaint.getStrokeWidth() / 2, startY, labelPaint.getStrokeWidth() / 2 +
                            rulerDistance, startY, rulerPaint);
                }
            }

            // top
            value = currentValue - i;
            startY = mHeight / 2 - i * rulerSpace + scrollOffset;
            if (startY >= getPaddingTop() && value >= minValue && value <= maxValue) {
                if (value % 5 == 0 || value == 0) {
                    canvas.drawLine(labelPaint.getStrokeWidth() / 2, startY, labelPaint.getStrokeWidth() / 2 +
                            (rulerDistance * 4) / 3, startY, rulerPaint);
                    canvas.drawText(String.valueOf(value), mWidth-rulerTextPaint.measureText(String.valueOf(maxValue))
                            , startY, rulerTextPaint);
                } else {
                    canvas.drawLine(labelPaint.getStrokeWidth() / 2, startY, labelPaint.getStrokeWidth() / 2 +
                            rulerDistance, startY, rulerPaint);
                }

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastMotionX = event.getX();
                lastMotionY = event.getY();
                scroller.forceFinished(true);
                clearMessage();
                break;
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                int delayY = 0;
                int delayX = 0;
                if (mOrientation == ORIENTATION.VERTICAL) {
                    final float motionY = event.getY();
                    delayY = (int) (motionY - lastMotionY);
                    lastMotionY = motionY;
                } else {
                    final float motionX = event.getX();
                    delayX = (int) (motionX - lastMotionX);
                    lastMotionX = motionX;
                }

                if (delayX != 0 || delayY != 0) {
                    doScroll(delayX, delayY);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }
        return true;
    }


    public float getRawSize(int unit, float value) {
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, metrics);
    }

    public void initRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Paint getRulerPaint() {
        return rulerPaint;
    }

    public TextPaint getRulerTextPaint() {
        return rulerTextPaint;
    }

    public Paint getLabelPaint() {
        return labelPaint;
    }

    public OnRulerListener getOnRulerListener() {
        return onRulerListener;
    }

    public void setRulerListener(OnRulerListener onRulerListener) {
        this.onRulerListener = onRulerListener;
    }

    /**
     * 方向枚举
     *
     * @author wangk
     */
    public static enum ORIENTATION {
        VERTICAL, HORIZONTAL
    }


    /**
     * 外部接口
     */
    public interface OnRulerListener {
        void onValueChanged(int value);
    }

}
