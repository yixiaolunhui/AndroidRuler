package com.wawi.cycleruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class CycleRulerView extends View {
	private int kedu;
	private int width;
	private int height;
	private float radius;
	private float padding;
	private float fontSize;
	private float offset;
	private Coordinate point;

	public float RADIUS_BIG;
	public float RADIUS_MEDIUM;
	public float RADIUS_SMALL;
	public float CYCLE_WIDTH;
	public float DISPLAY_SIZE_BIG;
	public float DISPLAY_SIZE_SMALL;

	/**
	 * 坐标
	 * 
	 * @author Administrator
	 * 
	 */
	class Coordinate {
		public Coordinate() {
			// TODO Auto-generated constructor stub
		}

		public Coordinate(float x, float y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
		}

		private float x;
		private float y;

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "[x:" + x + ", y:" + y + "]";
		}
	}

	/**
	 * 计算出字体的path让字体跟着path走
	 * 
	 * @param text
	 * @param paint
	 * @param degree
	 * @param r
	 * @param canvas
	 * @return
	 */
	private Path getTextPath(String text, Paint paint, double degree, float r) {
		double pathDegree = Math.abs(90 - degree);
		float textWidth = paint.measureText(text);
		float y = Math.abs((float) (textWidth * Math.sin(pathDegree / 180
				* Math.PI)));
		float x = Math.abs((float) (textWidth * Math.cos(pathDegree / 180
				* Math.PI)));
		Coordinate coordinate = getCoordinate(r, degree);
		Coordinate start = new Coordinate();
		Coordinate end = new Coordinate();
		if (degree < 90) {
			end.setX(-coordinate.getX() + x / 2);
			end.setY(-coordinate.getY() - y / 2);
			start.setX(-coordinate.getX() - x / 2);
			start.setY(-coordinate.getY() + y / 2);
		} else {
			end.setX(-coordinate.getX() + x / 2);
			end.setY(-coordinate.getY() + y / 2);
			start.setX(-coordinate.getX() - x / 2);
			start.setY(-coordinate.getY() - y / 2);
		}
		// Paint paint2 = new Paint();
		// paint2.setColor(0xffffffff);
		// canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(),
		// paint2);
		Path path = new Path();
		path.moveTo(start.getX(), start.getY());
		path.lineTo(end.getX(), end.getY());
		return path;
	}

	private void drawDisplay(Canvas canvas) {
		String cm = String.valueOf(kedu);
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
		Paint cyclePaint = new Paint();
		cyclePaint.setColor(0xffffffff);
		cyclePaint.setAntiAlias(true);
		cyclePaint.setStyle(Paint.Style.FILL);
		Paint strokPaint = new Paint();
		strokPaint.setAntiAlias(true);
		strokPaint.setColor(0xff999999);
		strokPaint.setStyle(Paint.Style.STROKE);
		strokPaint.setStrokeWidth(CYCLE_WIDTH);
		canvas.drawCircle(width / 2, height * 3 / 5, RADIUS_BIG, cyclePaint);
		canvas.drawCircle(width / 2, height * 3 / 5, RADIUS_MEDIUM, cyclePaint);
		canvas.drawCircle(width / 2, height * 3 / 5, RADIUS_BIG, strokPaint);
		strokPaint.setColor(0xff666666);
		canvas.drawCircle(width / 2, height * 3 / 5, RADIUS_MEDIUM, strokPaint);
		strokPaint.setColor(0xff999999);
		// canvas.drawCircle(width / 2 + RADIUS_BIG, height * 3 / 5,
		// RADIUS_SMALL,
		// cyclePaint);
		// canvas.drawCircle(width / 2 + RADIUS_BIG, height * 3 / 5,
		// RADIUS_SMALL,
		// strokPaint);
		canvas.drawText(cm, width / 2 - cmWidth / 2,
				height * 3 / 5 + bounds1.height() / 2, displayPaint1);
		// canvas.drawText(mm, width / 2 + RADIUS_BIG - mmWidth / 2, height * 3
		// / 5 + bounds2.height() / 2, displayPaint2);
	}

	private Coordinate getCoordinate(float r, double degree) {
		float x = (float) (r * Math.cos(degree / 180 * Math.PI));
		float y = (float) (r * Math.sin(degree / 180 * Math.PI));
		return new Coordinate(x, y);
	}

	private void onTouchBegain(Coordinate coordinate) {
		caculatePoint(coordinate);
	}

	private void onTouchMove(Coordinate coordinate) {
		caculatePoint(coordinate);
	}

	private void onTouchDone(Coordinate coordinate) {
		// caculatePoint(coordinate);
	}

	private void caculatePoint(Coordinate coordinate) {
		float mx = width / 2f;
		float my = height - offset;
		if (coordinate.getY() > my) {
			coordinate.setY(my);
		}
		float dx = coordinate.getX() - mx;
		float dy = coordinate.getY() - my;
		double r = Math.sqrt(dx * dx + dy * dy);
		float x = (float) (dx / r * radius);
		float y = (float) (dy / r * radius);
		point = new Coordinate(x, y);
		kedu = (int) Math.round(Math.atan(dy /dx) / Math.PI * 180);
		if (dx >= 0) {
			kedu += 180;
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			onTouchDone(new Coordinate(event.getX(), event.getY()));
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(new Coordinate(event.getX(), event.getY()));
			break;
		case MotionEvent.ACTION_DOWN:
			onTouchBegain(new Coordinate(event.getX(), event.getY()));
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		width = getWidth();
		height = getHeight();
		radius = width / 2f;

		Paint paint = new Paint();
		paint.setColor(0xffffffff);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);

		offset = (height - (width / 2f)) / 2f;
		RectF oval = new RectF(0, offset, width, width + offset);
		canvas.drawArc(oval, 180, 180, true, paint);
		// 反向填充
		Paint xpaint = new Paint();
		xpaint.setAntiAlias(true);
		xpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
		xpaint.setStyle(Paint.Style.FILL);
		xpaint.setColor(0x5fffffff);
		canvas.drawRect(new RectF(0, 0, width, height), xpaint);
		canvas.save();
		canvas.translate(width / 2, height - offset);
		double degree = 10;
		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(0x6fffffff);
		paint2.setStrokeWidth(1);
		Paint degreePaint = new Paint();
		degreePaint.setAntiAlias(true);
		degreePaint.setTextSize(fontSize);
		degreePaint.setColor(0x6fffffff);
		for (int i = 1; i < 180; i++) {
			Coordinate coordinate = getCoordinate(radius, i);
			float x = coordinate.getX();
			float y = coordinate.getY();
			float r = radius - padding / 2;
			if ((i % 5) == 0) {
				if ((i & 0x1) == 0) {
					// 10
					r = radius - padding;
					String text = String.valueOf((int) (i));
					Path path = getTextPath(text, degreePaint, i, radius
							- padding - fontSize * 5 / 4);
					canvas.drawTextOnPath(text, path, 0, 0, degreePaint);
				} else {
					// 5
					r = radius - padding * 3 / 4;
				}
			}
			Coordinate coordinate1 = getCoordinate(r, i);
			float x1 = coordinate1.getX();
			float y1 = coordinate1.getY();
			canvas.drawLine(-x1, -y1, -x, -y, paint2);
		}
		Paint arcPaint = new Paint();
		arcPaint.setAntiAlias(true);
		arcPaint.setColor(0x6fffffff);
		arcPaint.setStrokeWidth(1);
		arcPaint.setStyle(Paint.Style.STROKE);
		RectF oval1 = new RectF();
		oval1.left = -width / 2f;
		oval1.top = offset * 2f - height;
		oval1.right = width / 2f;
		oval1.bottom = height - offset * 2f;
		canvas.drawArc(oval1, 180, 180, true, arcPaint);
		canvas.drawLine(0, 0, 0, -padding, paint2);
		if (point != null) {
			canvas.drawLine(0, 0, point.getX(), point.getY(), paint2);
		}
		canvas.restore();
		drawDisplay(canvas);
	}

	private void init(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		padding = TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
		fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
				dm);
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
	}

	public CycleRulerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CycleRulerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CycleRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
	}

}
