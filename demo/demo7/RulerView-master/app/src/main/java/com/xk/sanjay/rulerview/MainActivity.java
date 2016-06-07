package com.xk.sanjay.rulerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xk.sanjay.rulberview.RulerWheel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RulerWheel rulerView;
    private RulerWheel rulerView2;
    private String TAG = this.getClass().getSimpleName();

    private TextView tvCurValue;
    private TextView tvCurValue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> list = new ArrayList<>();
        for (int i = 30; i < 150; i += 1) {
            list.add(i + "");
            for (int j = 1; j < 10; j++) {
                list.add(i + "." + j);
            }
        }

        tvCurValue = (TextView) findViewById(R.id.curValue_tv);
        rulerView = (RulerWheel) findViewById(R.id.ruler_view);
        rulerView.setData(list);
        rulerView.setScrollingListener(new RulerWheel.OnWheelScrollListener<String>() {
            @Override
            public void onChanged(RulerWheel wheel, String oldValue, String newValue) {
                tvCurValue.setText(newValue + "");
            }

            @Override
            public void onScrollingStarted(RulerWheel wheel) {

            }

            @Override
            public void onScrollingFinished(RulerWheel wheel) {

            }
        });


        List<String> list2 = new ArrayList<>();
        for (int i = 1000; i < 50000; i += 500) {
            list2.add(i + "");
        }
        tvCurValue2 = (TextView) findViewById(R.id.curValue2_tv);
        rulerView2 = (RulerWheel) findViewById(R.id.ruler_view2);
        rulerView2.setData(list2);
        rulerView2.setDataModel(RulerWheel.DATA_SET);
        rulerView2.setSelectedValue("8000");
        //不默认数据的画可以加下面的方式设置
//        rulerView2.setData(list2);
        rulerView2.setScrollingListener(new RulerWheel.OnWheelScrollListener<String>() {
            @Override
            public void onChanged(RulerWheel wheel, String oldValue, String newValue) {
                tvCurValue2.setText(newValue + "");
            }

            @Override
            public void onScrollingStarted(RulerWheel wheel) {

            }

            @Override
            public void onScrollingFinished(RulerWheel wheel) {

            }
        });


    }


    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        for (int i = 10; i < 200; i += 1) {
            list.add(i + "");
            for (int j = 1; j < 10; j++) {
                list.add(i + "." + j);
            }
        }
        System.out.println(String.valueOf(list));


    }
}
