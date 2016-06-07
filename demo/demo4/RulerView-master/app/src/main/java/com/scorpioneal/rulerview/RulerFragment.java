package com.scorpioneal.rulerview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ScorpioNeal on 15/7/29.
 */
public class RulerFragment extends Fragment {

    private TextView mBirthTV, mHeightTV, mWeightTv;
    private RulerView mBirthView, mHeightView, mWeightView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ruler_layout, container, false);
        mBirthTV = (TextView)view.findViewById(R.id.birth_tv);
        mHeightTV = (TextView)view.findViewById(R.id.height_tv);
        mWeightTv = (TextView)view.findViewById(R.id.weight_tv);
        mBirthView = (RulerView)view.findViewById(R.id.birthRulerView);
        mHeightView = (RulerView)view.findViewById(R.id.heightRulerView);
        mWeightView = (RulerView)view.findViewById(R.id.weightRulerView);

        mBirthView.setStartValue(0);
        mBirthView.setEndValue(10000);
        mBirthView.setOriginValue(2000);
        mBirthView.setOriginValueSmall(0);
        mBirthView.setPartitionWidthInDP(106.7f);
        mBirthView.setPartitionValue(1000);
        mBirthView.setSmallPartitionCount(1);
        mBirthView.setmValue(1990);
        mBirthView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mBirthTV.setText(intVal + " " + fltval);
            }
        });

        mHeightView.setStartValue(50);
        mHeightView.setEndValue(250);
        mHeightView.setPartitionWidthInDP(40);
        mHeightView.setPartitionValue(1);
        mHeightView.setSmallPartitionCount(1);
        mHeightView.setmValue(170);
        mHeightView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mHeightTV.setText(intVal + " " + fltval);
            }
        });

        mWeightView.setStartValue(20);
        mWeightView.setEndValue(250);
        mWeightView.setPartitionWidthInDP(36.7f);
        mWeightView.setPartitionValue(1);
        mWeightView.setSmallPartitionCount(2);
        mWeightView.setmValue(106);
        mWeightView.setOriginValueSmall(1);
        mWeightView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                mWeightTv.setText(intVal + " " + fltval);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
