package com.wawi.cycleruler;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.wawi.cycleruler.camera.CameraManager;

public class MainActivity extends Activity implements Callback,
		OnCheckedChangeListener {
	private Camera camera;
	SurfaceView surfaceView;
	private boolean hasSurface;
	private ToggleButton tb;
	private int w;
	private int h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CameraManager.init(getApplication());
		hasSurface = false;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
		h = dm.heightPixels;
		tb = (ToggleButton) findViewById(R.id.camera_swicth);
		tb.setOnCheckedChangeListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startPreview();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		hasSurface = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		CameraManager.get().stopPreview();
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			if (surfaceView != null) {
				surfaceView.setVisibility(View.VISIBLE);
			}
			startPreview();
		} else {
			if (surfaceView != null) {
				surfaceView.setVisibility(View.INVISIBLE);
			}
			stopPreview();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
	}

	private void startPreview() {
		surfaceView = (SurfaceView) findViewById(R.id.surface);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	private void stopPreview() {
		CameraManager.get().stopPreview();
		CameraManager.get().closeDriver();
	}
}
