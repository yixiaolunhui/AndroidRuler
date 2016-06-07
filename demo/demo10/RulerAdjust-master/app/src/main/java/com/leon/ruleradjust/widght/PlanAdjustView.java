package com.leon.ruleradjust.widght;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.ruleradjust.R;

/**
 * Created by leon on 16-2-26.
 **/
public class PlanAdjustView extends LinearLayout implements View.OnClickListener{
    private TextView planTimeOne,planTimeTwo,planTimeThree;
    private AdjustView adjustView;
    private int feedValue;
    private float[] rates = new float[]{};

    public PlanAdjustView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        setGravity(Gravity.CENTER);
        setVisibility(INVISIBLE);
        setBackgroundColor(getResources().getColor(R.color.white));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_plan_adjust,this);
        planTimeOne = (TextView) view.findViewById(R.id.plan_time_one);
        planTimeOne.setOnClickListener(this);
        planTimeOne.setText(getContext().getString(R.string.Feed_times_everyday,1));
        setTimesEveryday(planTimeOne,1);
        planTimeTwo = (TextView) view.findViewById(R.id.plan_time_two);
        planTimeTwo.setOnClickListener(this);
        planTimeTwo.setText(getContext().getString(R.string.Feed_times_everyday,2));
        setTimesEveryday(planTimeTwo,2);
        planTimeThree = (TextView) view.findViewById(R.id.plan_time_three);
        planTimeThree.setOnClickListener(this);
        planTimeThree.setText(getContext().getString(R.string.Feed_times_everyday,3));
        setTimesEveryday(planTimeThree,3);
        adjustView = (AdjustView) view.findViewById(R.id.adjust_view);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.plan_time_one:
                if(adjustView.getTimes()!=1){
                    updatePlanTime(1);
                    updatePlanView(feedValue,1,new float[]{});
                }
                break;
            case R.id.plan_time_two:
                if(adjustView.getTimes()!=2){
                    updatePlanTime(2);
                    updatePlanView(feedValue,2,rates.length==2?rates:new float[]{});
                }
                break;
            case R.id.plan_time_three:
                if(adjustView.getTimes()!=3){
                    updatePlanTime(3);
                    updatePlanView(feedValue,3,rates.length==3?rates:new float[]{});
                }
                break;
        }
    }

    private void updatePlanTime(int count){
        if(count <=1 || count>3){
            planTimeOne.setTextColor(getResources().getColor(R.color.feed_plan_btn_color));
            planTimeTwo.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
            planTimeThree.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
        }else if(count == 2){
            planTimeOne.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
            planTimeTwo.setTextColor(getResources().getColor(R.color.feed_plan_btn_color));
            planTimeThree.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
        }else if(count == 3){
            planTimeOne.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
            planTimeTwo.setTextColor(getResources().getColor(R.color.feed_plan_time_color));
            planTimeThree.setTextColor(getResources().getColor(R.color.feed_plan_btn_color));
        }
    }

    /**
     * 更新view，切换view
     * @param feedValue feedValue
     * @param times 次数
     * @param rates 比例
     */
    private void updatePlanView(int feedValue,int times,float[] rates){
        setVisibility(VISIBLE);
        adjustView.setPlanRate(feedValue,times,rates);
    }

    /**
     * activity调用，显示view
     */
    public void setPlanRate(int feedValue,float[] rates){
        this.feedValue = feedValue;
        this.rates = rates;
        setVisibility(VISIBLE);
        int count = rates.length;
        updatePlanTime(count);
        adjustView.setPlanRate(feedValue,count,rates);
    }

    private void setTimesEveryday(TextView textView, int times){
        String tvEveryDay = getResources().getString(R.string.Feed_times_everyday,times);
        int timeIndex = tvEveryDay.indexOf(String.valueOf(times));
        SpannableString sb = new SpannableString(tvEveryDay);
        sb.setSpan(new RelativeSizeSpan(2f), timeIndex,timeIndex+1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sb);
    }

    /**
     * 获取最终设置的比例
     * @return 比例
     */
    public String getFeedingRatios(){
        return adjustView.getFeedingRatios();
    }
}
