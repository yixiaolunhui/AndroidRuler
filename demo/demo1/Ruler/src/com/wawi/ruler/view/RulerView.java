package com.wawi.ruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class RulerView extends SurfaceView implements Callback {
	public float UNIT_MM;
	public float RULE_HEIGHT;
	public float RULE_SCALE;
	public int SCREEN_W;
	public int SCREEN_H;
	public float FONT_SIZE;
	public float PADDING;
	public float RADIUS_BIG;
	public float RADIUS_MEDIUM;
	public float RADIUS_SMALL;
	public float CYCLE_WIDTH;
	public float DISPLAY_SIZE_BIG;
	public float DISPLAY_SIZE_SMALL;

	private SurfaceHolder holder;
	boolean unlockLineCanvas = false;
	float lineX;
	float lineOffset;
	float startX;
	float lastX;
	int kedu;
	Paint paint;
	Paint linePaint;
	Paint fontPaint;

	public int getKedu() {
		return kedu;
	}

	public void setKedu(int kedu) {
		this.kedu = kedu;
		draw();
	}

	public float getLineX() {
		return lineX;
	}

	public void setLineX(float lineX) {
		this.lineX = lineX;
		draw();
	}

	private void onTouchBegain(float x, float y) {
		lineOffset = Math.abs(x - lineX);
		if (lineOffset <= PADDING * 2) {
			startX = x;
			unlockLineCanvas = true;
		}
	}

	private void onTouchMove(float x, float y) {
		if (unlockLineCanvas) {
			lineX += x - startX;
			if (lineX < PADDING) {
				lineX = PADDING;
			} else if (lineX > lastX) {
				lineX = lastX;
			}
			kedu = Math.round((lineX - PADDING) / UNIT_MM);
			startX = x;
			draw();
		}
	}

	private void onTouchDone(float x, float y) {
		unlockLineCanvas = false;
		startX = -1;
		draw();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			onTouchDone(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_DOWN:
			onTouchBegain(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(event.getX(), event.getY());
			break;
		}
		return true;
	}

	private void init(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		RADIUS_BIG = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
				dm);
		RADIUS_MEDIUM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				40, dm);
		RADIUS_SMALL = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				20, dm);
		CYCLE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
				dm);
		DISPLAY_SIZE_BIG = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 40, dm);
		DISPLAY_SIZE_SMALL = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 20, dm);
		UNIT_MM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
		RULE_HEIGHT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				30, dm);
		FONT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
				dm);
		PADDING = FONT_SIZE / 2;
		SCREEN_W = dm.widthPixels;
		SCREEN_H = dm.heightPixels;
		holder = getHolder();
		holder.addCallback(this);
		paint = new Paint();
		paint.setColor(0xff1eb8f8);
		linePaint = new Paint();
		linePaint.setColor(0xff1eb8f8);
		linePaint.setStrokeWidth(4);
		fontPaint = new Paint();
		fontPaint.setTextSize(FONT_SIZE);
		fontPaint.setAntiAlias(true);
		fontPaint.setColor(0xff1eb8f8);
		lineX = PADDING;
		kedu = 0;
	}

	private void drawDisplay(Canvas canvas) {
		String cm = String.valueOf(kedu / 10);
		String mm = String.valueOf(kedu % 10);
		Paint displayPaint1 = new Paint();
		displayPaint1.setAntiAlias(true);
		displayPaint1.setColor(0xff1eb8f8);
		displayPaint1.setTextSize(DISPLAY_SIZE_BIG);
		float cmWidth = displayPaint1.measureText(cm);
		Rect bounds1 = new Rect();
		displayPaint1.getTextBounds(cm, 0, cm.length(), bounds1);
		Paint displayPaint2 = new Paint();
		displayPaint2.setAntiAlias(true);
		displayPaint2.setColor(0xff666666);
		displayPaint2.setTextSize(DISPLAY_SIZE_SMALL);
		float mmWidth = displayPaint2.measureText(mm);
		Rect bounds2 = new Rect();
		displayPaint2.getTextBounds(mm, 0, mm.length(), bounds2);
		canvas.drawLine(lineX, 0, lineX, SCREEN_H, linePaint);
		// canvas.drawText(
		// String.valueOf(kedu / 10) + "cm, " + String.valueOf(kedu % 10)
		// + "mm", PADDING, SCREEN_H - PADDING, fontPaint);
		Paint cyclePaint = new Paint();
		cyclePaint.setColor(0xffffffff);
		cyclePaint.setAntiAlias(true);
		cyclePaint.setStyle(Paint.Style.FILL);
		Paint strokPaint = new Paint();
		strokPaint.setAntiAlias(true);
		strokPaint.setColor(0xff999999);
		strokPaint.setStyle(Paint.Style.STROKE);
		strokPaint.setStrokeWidth(CYCLE_WIDTH);
		canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_BIG, cyclePaint);
		canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_MEDIUM, cyclePaint);
		canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_BIG, strokPaint);
		strokPaint.setColor(0xff666666);
		canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_MEDIUM, strokPaint);
		strokPaint.setColor(0xff999999);
		canvas.drawCircle(SCREEN_W / 2 + RADIUS_BIG, SCREEN_H / 2,
				RADIUS_SMALL, cyclePaint);
		canvas.drawCircle(SCREEN_W / 2 + RADIUS_BIG, SCREEN_H / 2,
				RADIUS_SMALL, strokPaint);
		canvas.drawText(cm, SCREEN_W / 2 - cmWidth / 2,
				SCREEN_H / 2 + bounds1.height() / 2, displayPaint1);
		canvas.drawText(mm, SCREEN_W / 2 + RADIUS_BIG - mmWidth / 2, SCREEN_H
				/ 2 + bounds2.height() / 2, displayPaint2);
	}

	private void draw() {
		Canvas canvas = null;
		try {
			canvas = holder.lockCanvas();
			canvas.drawColor(0xffffffff);
			float left = PADDING;
			for (int i = 0; SCREEN_W - PADDING - left > 0; i++) {
				RULE_SCALE = 0.5f;
				if (i % 5 == 0) {
					if ((i & 0x1) == 0) {
						// Å¼Êý
						RULE_SCALE = 1f;
						String txt = String.valueOf(i / 10);
						Rect bounds = new Rect();
						float txtWidth = fontPaint.measureText(txt);
						fontPaint.getTextBounds(txt, 0, txt.length(), bounds);
						canvas.drawText(txt, left - txtWidth / 2, RULE_HEIGHT
								+ FONT_SIZE / 2 + bounds.height(), fontPaint);
					} else {
						// ÆæÊý
						RULE_SCALE = 0.75f;
					}
				}
				RectF rect = new RectF();
				rect.left = left - 1;
				rect.top = 0;
				rect.right = left + 1;
				rect.bottom = rect.top + RULE_HEIGHT * RULE_SCALE;
				canvas.drawRect(rect, paint);
				left += UNIT_MM;
			}
			lastX = left - UNIT_MM;
			drawDisplay(canvas);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public RulerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RulerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RulerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				draw();
			};
		}.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
