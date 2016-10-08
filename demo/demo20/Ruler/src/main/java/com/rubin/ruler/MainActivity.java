package com.rubin.ruler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.seek.ruler.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvHorizontal;
    private TextView mTvVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvHorizontal = (TextView) findViewById(R.id.tv_horizontal);
        mTvVertical = (TextView) findViewById(R.id.tv_vertical);
        mTvHorizontal.setOnClickListener(this);
        mTvVertical.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_horizontal) {
            Intent intent = new Intent(this, HorizontalRulerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, VerticalRulerActivity.class);
            startActivity(intent);
        }
    }
}
