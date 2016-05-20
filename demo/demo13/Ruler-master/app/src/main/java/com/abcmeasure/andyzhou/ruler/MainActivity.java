package com.abcmeasure.andyzhou.ruler;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.abcmeasure.andyzhou.ruler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RulerRecyclerAdapter recyclerAdapter;
    TouchPanelViewModel mTouchPanelViewModel;
    int currentViewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentViewType = savedInstanceState.getInt("CURRENT_VIEW_TYPE",Constants.INCH_VIEWTYPE);
        }else{
            currentViewType = Constants.INCH_VIEWTYPE;
        }
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mTouchPanelViewModel = new TouchPanelViewModel(metrics.xdpi);
        binding.setTouchPanelModel(mTouchPanelViewModel);

        recyclerAdapter = new RulerRecyclerAdapter(100, metrics.xdpi, currentViewType);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_ruler);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unit_switch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.switch_unit) {
            recyclerAdapter.changeCurrentViewType();
            currentViewType = recyclerAdapter.getCurrentViewType();
            mTouchPanelViewModel.setViewType(currentViewType);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("CURRENT_VIEW_TYPE", currentViewType);
        super.onSaveInstanceState(savedInstanceState);
    }
}
