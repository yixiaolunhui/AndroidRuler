package com.kongdy.hasee.noendlessruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kongdy.hasee.noendlessruler.view.NoEndlessRuler;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private NoEndlessRuler ruler;

    private NoEndlessRuler.OnRulerListener onRulerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.initScreenSize(getApplicationContext());


        text = (TextView) findViewById(R.id.text);
        ruler = (NoEndlessRuler) findViewById(R.id.ruler);

        onRulerListener = new NoEndlessRuler.OnRulerListener() {
            @Override
            public void onValueChanged(int value) {
                text.setText(value+"");
            }
        };

        ruler.setOnRulerListener(onRulerListener);
    }
}
