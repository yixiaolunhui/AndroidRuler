package com.study.horizontalruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    private MyHorizontalScrollView horizontalScrollView;

    private RuleView ruleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        ruleView = (RuleView) findViewById(R.id.rule_view);

        horizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.hor_scrollview);
        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 去掉超出滑动后出现的阴影效果

        tv.setText("1.0");

        // 设置水平滑动
        ruleView.setHorizontalScrollView(horizontalScrollView);

        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(new MyHorizontalScrollView.OnScrollListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

                ruleView.setScrollerChanaged(l, t, oldl, oldt);
            }
        });

        final DecimalFormat format = new DecimalFormat("0.0");

        ruleView.onChangedListener(new RuleView.onChangedListener() {
            @Override
            public void onSlide(float number) {

                int num = (int) (number * 100);// 获取十位数字

                String text = format.format((number + 1.0f)) + "";

                tv.setText(text);
            }
        });

    }
}
