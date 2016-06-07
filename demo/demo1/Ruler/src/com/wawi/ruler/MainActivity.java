package com.wawi.ruler;

import android.app.Activity;
import android.os.Bundle;

import com.wawi.ruler.view.RulerView;

public class MainActivity extends Activity {
	private RulerView rulerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(rulerView = new RulerView(this));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (rulerView != null) {
			rulerView.setLineX(savedInstanceState.getFloat("lineX"));
			rulerView.setKedu(savedInstanceState.getInt("kedu"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (rulerView != null) {
			outState.putFloat("lineX", rulerView.getLineX());
			outState.putInt("kedu", rulerView.getKedu());
		}
		super.onSaveInstanceState(outState);
	}

	// class RulerView extends View {
	// boolean unlockLineCanvas = false;
	// float lineX;
	// float lineOffset;
	// float startX;
	// float lastX;
	// int kedu;
	// Paint paint;
	// Paint linePaint;
	// Paint fontPaint;
	//
	// public int getKedu() {
	// return kedu;
	// }
	//
	// public void setKedu(int kedu) {
	// this.kedu = kedu;
	// invalidate();
	// }
	//
	// public float getLineX() {
	// return lineX;
	// }
	//
	// public void setLineX(float lineX) {
	// this.lineX = lineX;
	// invalidate();
	// }
	//
	// private void onTouchBegain(float x, float y) {
	// lineOffset = Math.abs(x - lineX);
	// if (lineOffset <= PADDING * 2) {
	// startX = x;
	// unlockLineCanvas = true;
	// }
	// }
	//
	// private void onTouchMove(float x, float y) {
	// if (unlockLineCanvas) {
	// lineX += x - startX;
	// if (lineX < HALF_PADDING) {
	// lineX = HALF_PADDING;
	// } else if (lineX > lastX) {
	// lineX = lastX;
	// }
	// kedu = Math.round((lineX - HALF_PADDING) / unit_mm);
	// startX = x;
	// invalidate();
	// }
	// }
	//
	// private void onTouchDone(float x, float y) {
	// unlockLineCanvas = false;
	// startX = -1;
	// invalidate();
	// }
	//
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// // TODO Auto-generated method stub
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_CANCEL:
	// case MotionEvent.ACTION_UP:
	// onTouchDone(event.getX(), event.getY());
	// break;
	// case MotionEvent.ACTION_DOWN:
	// onTouchBegain(event.getX(), event.getY());
	// break;
	// case MotionEvent.ACTION_MOVE:
	// onTouchMove(event.getX(), event.getY());
	// break;
	// }
	// return true;
	// }
	//
	// @Override
	// protected void onDraw(Canvas canvas) {
	// // TODO Auto-generated method stub
	// float left = HALF_PADDING;
	// for (int i = 0; SCREEN_W - HALF_PADDING - left > 0; i++) {
	// scale = 0.5f;
	// if (i % 5 == 0) {
	// if ((i & 0x1) == 0) {
	// // Å¼Êý
	// scale = 1f;
	// String txt = String.valueOf(i / 10);
	// Rect bounds = new Rect();
	// fontPaint.getTextBounds(txt, 0, txt.length(), bounds);
	// canvas.drawText(txt, left - bounds.width() / 2, rule
	// + PADDING / 2 + bounds.height(), fontPaint);
	// } else {
	// // ÆæÊý
	// scale = 0.75f;
	// }
	// }
	// RectF rect = new RectF();
	// rect.left = left - 1;
	// rect.top = 0;
	// rect.right = left + 1;
	// rect.bottom = rect.top + rule * scale;
	// canvas.drawRect(rect, paint);
	// left += unit_mm;
	// }
	// lastX = left - unit_mm;
	// canvas.drawLine(lineX, 0, lineX, SCREEN_H, linePaint);
	// canvas.drawText(
	// String.valueOf(kedu / 10) + "cm, "
	// + String.valueOf(kedu % 10) + "mm", HALF_PADDING,
	// SCREEN_H - HALF_PADDING, fontPaint);
	// }
	//
	// private void init(Context context) {
	// paint = new Paint();
	// paint.setColor(0xffff0000);
	// linePaint = new Paint();
	// linePaint.setColor(0xfff15f5f);
	// linePaint.setStrokeWidth(4);
	// fontPaint = new Paint();
	// fontPaint.setTextSize(PADDING);
	// fontPaint.setAntiAlias(true);
	// setBackgroundColor(0xffefefef);
	// lineX = HALF_PADDING;
	// kedu = 0;
	// }
	//
	// public RulerView(Context context) {
	// super(context);
	// // TODO Auto-generated constructor stub
	// init(context);
	// }
	//
	// public RulerView(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// // TODO Auto-generated constructor stub
	// init(context);
	// }
	//
	// public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
	// super(context, attrs, defStyleAttr);
	// // TODO Auto-generated constructor stub
	// init(context);
	// }
	//
	// }
}