package com.ggx.ruler_lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by John on 2016/3/5.
 *
 */
public class RulerView extends RelativeLayout implements View.OnTouchListener,OnScrollViewChanged{

    private static final String TAG="RulerView";

    private int defaultValue=0;

    private int direction=0;
    private int startValue=0;//起始刻度
    private int endValue=100;//结束刻度

    //回调接口
    private RulerCallback callback;

    CustomeScrollView ruler_vertical;

    CustomeHorizontalScrollView ruler_horizontal;


    LinearLayout ruler_layout;

    public RulerView(Context context) {
        super(context);
        init(context,null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs){
        //获取指定参数
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        //获取指定的尺子方向 default垂直
        direction=ta.getInt(R.styleable.RulerView_direction,0);
        endValue=ta.getInt(R.styleable.RulerView_endValue,100)/10;
        defaultValue=ta.getInt(R.styleable.RulerView_defaultValue,direction==0?17:60);
        ta.recycle();
        //装载基础界面
       switch (direction){
           case 0:
               //垂直布局
               View vertical=LayoutInflater.from(context).inflate(R.layout.ruler_layout_vertical,this,true);
               ruler_vertical= (CustomeScrollView) vertical.findViewById(R.id.ruler);
               ruler_layout= (LinearLayout) vertical.findViewById(R.id.ruler_layout);
               ruler_vertical.setOnTouchListener(this);
               ruler_vertical.setScrollViewChanged(this);
               break;
           case 1:
               //水平布局
               View horizontal=LayoutInflater.from(context).inflate(R.layout.ruler_layout_horizontal,this,true);
               ruler_horizontal= (CustomeHorizontalScrollView) horizontal.findViewById(R.id.ruler);
               ruler_layout= (LinearLayout) horizontal.findViewById(R.id.ruler_layout);
               ruler_horizontal.setOnTouchListener(this);
               ruler_horizontal.setScrollViewChanged(this);
               break;
       }
        //Log.e(TAG, "获取完成参数" + direction + ";" + startValue + ";" + endValue);
        //Log.e(TAG,"属性值>>>"+ruler+";/n"+ruler_layout);
    }


    private boolean isFirst=true;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(isFirst){
            isFirst=false;
            switch (direction){
                case 0:
                    //垂直刻度
                    constructRulerVertical();
                    break;
                case 1:
                    //水平刻度
                    constructRulerHorizontal();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ruler_horizontal.smoothScrollTo(defaultValue*10,0);
                        }
                    }, 200);
                    break;
            }
        }
    }



    //垂直刻度构造
    private void constructRulerVertical() {
        int rulerHeight = ruler_vertical.getHeight();
        View topview = new View(getContext());
        topview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                rulerHeight / 2));
        ruler_layout.addView(topview);
        for (int i = startValue; i < endValue; i--) {
            View view =LayoutInflater.from(getContext()).inflate(
                    R.layout.ruler_unit_vertical, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
            TextView tv = (TextView) view.findViewById(R.id.ruler_num);
            tv.setText(String.valueOf(i*10));
            ruler_layout.addView(view);
        }
        View bottomview = new View(getContext());
        bottomview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                rulerHeight / 2));
        ruler_layout.addView(bottomview);
    }
    //水平刻度构造
    private void constructRulerHorizontal(){
        int ruleWidth=ruler_horizontal.getWidth();
        //填充左边的空白
        View leftBlank=new View(getContext());
        leftBlank.setLayoutParams(new LayoutParams(ruleWidth / 2, LayoutParams.MATCH_PARENT));
        ruler_layout.addView(leftBlank);
        //填充正常的刻度 100----250
        for(int i=startValue;i<endValue;i++){
            View unit=LayoutInflater.from(getContext()).inflate(R.layout.ruler_unit_horizontal,null);
            unit.setLayoutParams(new LayoutParams(100,LayoutParams.MATCH_PARENT));
            TextView tv = (TextView) unit.findViewById(R.id.ruler_num);
            tv.setText(String.valueOf(i*10));
            ruler_layout.addView(unit);
        }

        //填充右边的空白
        View rightBlank=new View(getContext());
        rightBlank.setLayoutParams(new LayoutParams(ruleWidth/2,LayoutParams.MATCH_PARENT));
        ruler_layout.addView(rightBlank);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

                if(callback!=null){
                    int num=direction==0?ruler_vertical.getScrollY()/10:ruler_horizontal.getScrollX()/10;
                    callback.resultNum(num);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getEndValue() {
        return endValue;
    }


    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public RulerCallback getCallback() {
        return callback;
    }

    public void setCallback(RulerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onScrollChanged( int x, int y, int oldx, int oldy) {
        if(getCallback()!=null){
            int num=direction==0?y/10:x/10;
            if(num<0){
                num=0;
            }else if(num>endValue*10){
                num=endValue;
            }
            getCallback().resultNum(num);
        }
    }


    public interface RulerCallback{

        void resultNum(int num);
    }
}
