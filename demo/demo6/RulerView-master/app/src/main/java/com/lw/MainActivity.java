package com.lw;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.lw.widget.RulerView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        init();
    }

    private void init(){

        final RulerView rulerView = (RulerView) findViewById(R.id.ruler_view);
        final EditText positionEditText = (EditText) findViewById(R.id.et_pos);
        final EditText valueEditText = (EditText) findViewById(R.id.et_value);
        final EditText widthEditText = (EditText) findViewById(R.id.et_indicate_width);
        final EditText paddingEditTExt = (EditText) findViewById(R.id.et_indicate_padding);
        CheckBox    withText = (CheckBox) findViewById(R.id.cb_with_text);
        CheckBox    autoAlign = (CheckBox) findViewById(R.id.cb_auto_align);


        Button topBtn = (Button) findViewById(R.id.btn_top);
        Button bottomBtn = (Button) findViewById(R.id.btn_bottom);

        //init
        rulerView.post(new Runnable() {
            @Override
            public void run() {
                int position = Integer.valueOf(positionEditText.getText().toString());
                rulerView.smoothScrollTo(position);
            }
        });


        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rulerView.setGravity(Gravity.TOP);
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rulerView.setGravity(Gravity.BOTTOM);
            }
        });


        rulerView.setOnScaleListener(new RulerView.OnScaleListener() {
            @Override
            public void onScaleChanged(int scale) {

            }
        });


        positionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s))
                    return;

                rulerView.smoothScrollTo(Integer.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s))
                    return;

                rulerView.smoothScrollTo(Integer.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        widthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s))
                    return;

                rulerView.setIndicateWidth(Integer.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        paddingEditTExt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s))
                    return ;

                rulerView.setIndicatePadding(Integer.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        withText.setChecked(rulerView.isWithText());
        autoAlign.setChecked(rulerView.isAutoAlign());

        withText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rulerView.setWithText(isChecked);
            }
        });

        autoAlign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rulerView.setAutoAlign(isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
