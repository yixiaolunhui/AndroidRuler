package com.seek.ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SimpleRulerView.OnValueChangeListener{
    private  TextView textView1;
    private  TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SimpleRulerView)findViewById(R.id.simple_ruler)).setOnValueChangeListener(this);
        ((SimpleRulerView)findViewById(R.id.height_ruler)).setOnValueChangeListener(this);
        textView1 = (TextView)findViewById(R.id.text1);
        textView2 = (TextView)findViewById(R.id.text2);
        textView1.setText("you have 0 dollars");
        textView2.setText("Your height is 0.00 meters");
    }


    @Override
    public void onChange(SimpleRulerView view, int position, float value) {
        switch (view.getId()){
            case R.id.simple_ruler:
                textView1.setText("you have " + value + " dollars");
                break;
            case R.id.height_ruler:
                textView2.setText("Your height is "+value+" meters");
                break;
        }
    }
}
