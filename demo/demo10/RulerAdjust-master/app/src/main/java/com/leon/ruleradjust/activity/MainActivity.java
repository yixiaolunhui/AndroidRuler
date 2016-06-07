package com.leon.ruleradjust.activity;

import android.app.Activity;
import android.os.Bundle;

import com.leon.ruleradjust.R;
import com.leon.ruleradjust.widght.PlanAdjustView;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlanAdjustView planAdjustView = (PlanAdjustView) findViewById(R.id.plan_adjust_view);
        planAdjustView.setPlanRate(505,new float[]{0.1f,0.2f,0.7f});
    }

}
