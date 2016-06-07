package com.leon.ruleradjust.widght;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.leon.ruleradjust.R;
import com.leon.ruleradjust.util.CommonUtil;
import com.leon.ruleradjust.util.ConvertDipPx;

/**
 * Created by leon on 16-2-26.
 **/
public class AdjustView extends View{
    int width,height;
    float grayLine,bannerTop;
    private int bannerWidth;
    private int feedValue = 0;
    private int times = 1;
    float[] rates = new float[]{};
    private Paint paint;
    private Rect textRect,unitRect;//文本和单位区域
    private Rect bannerOneRect,bannerTwoRect;//第一个、第二个banner的区域
    private float bannerOnePos,bannerTwoPos;//第一个、第二个banner的位置
    private Bitmap bmpBanner;//banner图片
    private int radius;
    private RectF rectF;
    private boolean flag;//绘制标志
    private int bannerScrollWidth;//banner可变化的长度
    private int firstNum,secondNum,thirdNum;//三个分配的重量比例
    private int threshold;//体重阀值

    public AdjustView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        bmpBanner = BitmapFactory.decodeResource(getResources(),R.drawable.plan_rubber);
    }

    public int getTimes(){
        return times;
    }

    /**
     * 返回最终的设定值
     * @return str
     */
    public String getFeedingRatios(){
        if(times==1)
            return "1";
        if(times==2)
            return firstNum+","+secondNum;
        return firstNum+","+secondNum+","+thirdNum;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        grayLine = height/9*5;
        textRect = new Rect();
        unitRect = new Rect();
        bannerOneRect = new Rect();
        bannerTwoRect = new Rect();
        bannerTop = grayLine-bmpBanner.getHeight()/9*5;
        bannerWidth = bmpBanner.getWidth();
        setScrollWidth();
        radius = ConvertDipPx.dip2px(getContext(),3);
        rectF = new RectF(bannerWidth/2,grayLine-radius*2,width-bannerWidth/2,grayLine);
    }

    /**
     * 设置指针可滑动的距离和指针滑动的阀值
     */
    private void setScrollWidth(){
        if(width>0){
            bannerScrollWidth = width - bannerWidth - (times-2)*bannerWidth;
            threshold = bannerScrollWidth/feedValue;
            flag = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(flag){
            //绘制灰色的线
            paint.setColor(getResources().getColor(R.color.gray_bg));
            paint.setAlpha(255/2);
            canvas.drawRoundRect(rectF,radius,radius,paint);

            //绘制文字
            paint.setColor(getResources().getColor(R.color.feed_plan_time_color));
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            //计算单位的信息
            paint.setTextSize(ConvertDipPx.dip2px(getContext(),10));
            String tmpStr = getResources().getString(R.string.Unit_gram);
            paint.getTextBounds(tmpStr,0,tmpStr.length(),unitRect);
            if(times == 1){
                tmpStr = String.valueOf(feedValue);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/2-(textRect.width()+unitRect.width()+radius)/2);
            }else if(times == 2){
                firstNum = CommonUtil.doubleToInt(feedValue*rates[0]);
                tmpStr = String.valueOf(firstNum);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/4+bannerWidth/2-(textRect.width()+unitRect.width()+radius)/2);
                secondNum = feedValue - firstNum;
                tmpStr = String.valueOf(secondNum);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/4*3-bannerWidth/2-(textRect.width()+unitRect.width()+radius)/2);
            }else if(times == 3){
                dealSpecialStatus();
                tmpStr = String.valueOf(firstNum);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/6+bannerWidth/2-(textRect.width()+unitRect.width()+radius)/2);
                tmpStr = String.valueOf(secondNum);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/2-(textRect.width()+unitRect.width()+radius)/2);
                tmpStr = String.valueOf(thirdNum);
                calculateTextLength(tmpStr);
                drawText(canvas,tmpStr,width/6*5-bannerWidth/2-(textRect.width()+unitRect.width()+radius)/2);
            }

            //绘制banner
            if(times == 2){
                if(bannerOnePos == 0){
                    bannerOnePos = (width-bannerWidth)*rates[0];
                }
                int leftPos = (int) bannerOnePos;
                bannerOneRect.set(leftPos,(int)(bannerTop+bmpBanner.getHeight()/2),leftPos+bmpBanner.getWidth(),(int)(bannerTop+bmpBanner.getHeight()));
                canvas.drawBitmap(bmpBanner,leftPos,bannerTop,paint);
            }else if(times == 3){
                if(bannerOnePos == 0){
                    bannerOnePos  = (width-2*bannerWidth)*rates[0];
                }
                if(bannerTwoPos == 0){
                    bannerTwoPos = (width-2*bannerWidth)*(rates[0]+rates[1])+bannerWidth;
                }
                int leftPos = (int) bannerOnePos;
                bannerOneRect.set(leftPos,(int)(bannerTop+bmpBanner.getHeight()/2),leftPos+bmpBanner.getWidth(),(int)(bannerTop+bmpBanner.getHeight()));
                canvas.drawBitmap(bmpBanner,leftPos,bannerTop,paint);
                leftPos = (int) bannerTwoPos;
                bannerTwoRect.set(leftPos,(int)(bannerTop+bmpBanner.getHeight()/2),leftPos+bmpBanner.getWidth(),(int)(bannerTop+bmpBanner.getHeight()));
                canvas.drawBitmap(bmpBanner,leftPos,bannerTop,paint);
            }
        }
    }

    //处理一些边界值，主要是secondNum
    private void dealSpecialStatus(){
        firstNum = CommonUtil.doubleToInt(feedValue*rates[0]);
        if(bannerPos == CLICKPOS.FIRST){
            if(firstNum + thirdNum >= feedValue){
                secondNum = 1;
                firstNum = feedValue - thirdNum - secondNum;
            }else if(secondNum + thirdNum >= feedValue){
                firstNum = 1;
                secondNum = feedValue - firstNum - secondNum;
            }else {
                secondNum = feedValue - thirdNum - firstNum;
            }
        }else {
            secondNum = CommonUtil.doubleToInt(feedValue*rates[1]);
            if(secondNum <= 1){
                secondNum = 1;
            }
        }
        thirdNum = feedValue-firstNum-secondNum;
        if(thirdNum<=1){
            thirdNum = 1;
            secondNum = feedValue - firstNum - thirdNum;
        }
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        flag = false;
    }

    float lastX,lastY;
    private CLICKPOS bannerPos;//用于记录当前点击的banner

    enum CLICKPOS{
        NONE,
        FIRST,
        SECOND
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                bannerPos = CLICKPOS.NONE;
                lastX = event.getX();
                lastY = event.getY();
                if(bannerOneRect.contains((int)lastX,(int)lastY)){
                    bannerPos = CLICKPOS.FIRST;
                }else if(bannerTwoRect.contains((int)lastX,(int)lastY)){
                    bannerPos = CLICKPOS.SECOND;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                if(bannerPos == CLICKPOS.FIRST){
                    bannerOnePos += x - lastX;
                    judgeBannerOneEdge();
                }else if(bannerPos == CLICKPOS.SECOND){
                    bannerTwoPos += x - lastX;
                    judgeBannerTwoEdge();
                }
                lastX = x;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                bannerPos = CLICKPOS.NONE;
                break;
        }
        return true;
    }

    /**
     * 判断bannerOne的位置并赋值，滑动最左侧有1个偏差
     */
    private void judgeBannerOneEdge(){
        if(bannerOnePos<=threshold){
            bannerOnePos = threshold;
        }
        if(times==2){
            if(bannerOnePos>=width-bannerWidth-threshold){
                bannerOnePos = width-bannerWidth-threshold;
            }
        }else if(times==3){
            if(bannerOnePos>=bannerTwoPos-bannerWidth-threshold){
                bannerOnePos = bannerTwoPos-bannerWidth-threshold;
            }
        }
        rates[0] = bannerOnePos/bannerScrollWidth;
        if(times == 2){
            rates[1] = 1 - rates[0];
        }else if(times == 3){
            rates[1] = 1 - rates[2] - rates[0];
        }
    }

    /**
     * 判断bannerTwo的位置并赋值，滑动最右侧有1个偏差
     */
    private void judgeBannerTwoEdge(){
        if(bannerTwoPos<=bannerOnePos+bannerWidth+threshold){
            bannerTwoPos = bannerOnePos+bannerWidth+threshold;
        }
        if(bannerTwoPos>=width-bannerWidth-threshold){
            bannerTwoPos = width-bannerWidth-threshold;
        }
        rates[1] = (bannerTwoPos-bannerOnePos-bannerWidth)/bannerScrollWidth;
        rates[2] = 1 - rates[0] - rates[1];
    }

    /**
     * 用来计算数字的长度
     */
    private void calculateTextLength(String tmpStr){
        paint.setTextSize(ConvertDipPx.dip2px(getContext(),25));
        paint.getTextBounds(tmpStr,0,tmpStr.length(),textRect);
    }

    /**
     * 绘制文字
     * @param canvas canvas
     * @param feedValue 绘制的内容
     * @param startPos 开始的左起点
     */
    private void drawText(Canvas canvas, String feedValue, float startPos){
        String tmpStr = feedValue;
        paint.setTextSize(ConvertDipPx.dip2px(getContext(),25));
        float numTopStart = bannerTop+ConvertDipPx.dip2px(getContext(),15);
        canvas.drawText(tmpStr,startPos,numTopStart+unitRect.height(),paint);
        paint.setTextSize(ConvertDipPx.dip2px(getContext(),10));
        tmpStr = getResources().getString(R.string.Unit_gram);
        canvas.drawText(tmpStr,startPos+textRect.width()+radius,numTopStart+textRect.height()-unitRect.height(),paint);
    }

    /**
     * 设置喂养比例
     * @param feedValue 总的喂养量
     * @param times 当前选中的喂养次数
     * @param rates 喂养比例集合
     */
    public void setPlanRate(int feedValue,int times,float[] rates){
        reset();
        setRates(times,rates);
        this.feedValue = feedValue;
        if(times <= 1 || times > 3){
            this.times = 1;
        }else{
            this.times = times;
        }
        setScrollWidth();
        invalidate();
    }

    /**
     * 重置会影响的变量
     */
    private void reset(){
        bannerOnePos = 0;
        bannerTwoPos = 0;
        this.rates = null;
        this.times = 1;
    }

    /**
     * 将数据内容copy过来
     * @param rates 源rates
     */
    private void setRates(int times,float[] rates){
        this.rates = new float[times];
        if(rates.length!=0){
            System.arraycopy(rates, 0, this.rates, 0, rates.length);
        }else {
            if(times == 2){
                this.rates = new float[]{0.5f,0.5f};
            }else if(times == 3){
                this.rates = new float[]{0.33f,0.33f,0.33f};
            }
        }
    }
}
