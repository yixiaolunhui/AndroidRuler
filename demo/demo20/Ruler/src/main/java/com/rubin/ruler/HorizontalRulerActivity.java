package com.rubin.ruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.seek.ruler.R;

public class HorizontalRulerActivity extends AppCompatActivity implements RulerView.OnValueChangeListener{

    private  TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal);
        ((RulerView)findViewById(R.id.height_ruler)).setOnValueChangeListener(this);
        mTextView = (TextView)findViewById(R.id.text);
    }


    @Override
    public void onChange(RulerView view,  float value) {
        switch (view.getId()){
            case R.id.height_ruler:
                mTextView.setText("Your height is "+value+" meters");
                break;
        }
    }
}
