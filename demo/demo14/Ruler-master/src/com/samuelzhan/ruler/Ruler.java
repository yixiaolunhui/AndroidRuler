package com.samuelzhan.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class Ruler extends View {
	
	private final int VERTICAL=0;
	private final int HORIZONTAL=1;
	
	//间隔，即两条刻度之间的距离
	private int interval;
	//起始值
	private int fromValue;
	//结束值
	private int toValue;
	//每两个值之间的间隔数,也指多少个最小单位，比如0cm到1cm有10个最小单位1mm
	private int intervalsBetweenValues;
	//相邻两个值的跳跃间隔，如上面第一张图的10000到11000，中间的跳跃值就是1000
	private int valuesInterval;
	//当前值
	private int currentValue;
	//值的文本大小
	private int valuesTextSize;
	//值的文本颜色
	private int valuesTextColor;
	//刻度的宽度
	private int linesWidth;
	//刻度的颜色
	private int linesColor;
	//刻度尺是vertical还是horizontal,上面第一张图的就是horizontal
	private int orientation;
	
	private Paint paint;
	
	private OnValueChangeListener listener;
	
	private int currentPosition;
	private int textHeight;
	private int offset;
	private int oldX;
	private int oldY;

	public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.Ruler);
		
		interval=array.getDimensionPixelSize(R.styleable.Ruler_interval, dp2px(intervalsBetweenValues));
		fromValue=array.getInt(R.styleable.Ruler_fromValue, 0);
		toValue=array.getInt(R.styleable.Ruler_toValue, intervalsBetweenValues);
		currentValue=array.getInt(R.styleable.Ruler_currentValue, (fromValue+toValue)/2);
		intervalsBetweenValues=array.getInt(R.styleable.Ruler_intervalsBetweenValues, intervalsBetweenValues);
		valuesInterval=array.getInt(R.styleable.Ruler_valuesInterval, 1);
		valuesTextSize=array.getDimensionPixelSize(R.styleable.Ruler_valuesTextSize, sp2px(16));
		valuesTextColor=array.getColor(R.styleable.Ruler_valuesTextColor, Color.BLACK);
		linesWidth=array.getDimensionPixelSize(R.styleable.Ruler_linesWidth, dp2px(1));
		linesColor=array.getColor(R.styleable.Ruler_linesColor, Color.BLACK);
		orientation=array.getInt(R.styleable.Ruler_orientation, HORIZONTAL);
		
		array.recycle();
		
		paint=new Paint();
		paint.setTextSize(valuesTextSize);
		
		//文本高度
		FontMetrics fm=paint.getFontMetrics();
		textHeight=(int)(fm.bottom-fm.top);
		
		//当前所指的刻度位置，即中间红色指针指向的值
		currentPosition=currentValue/valuesInterval*intervalsBetweenValues;
	}

	public Ruler(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public Ruler(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		if(orientation==VERTICAL){
			//画中间的指针,就在中间画，很简单
			paint.setColor(Color.RED);
			paint.setStrokeWidth(dp2px(2));
			canvas.drawLine(getWidth(), getHeight()/2, getWidth()/4, getHeight()/2, paint);
			
			//刻度线分两部分画，一半是上部分，一半是下部分
			//画中间以上部分的刻度
			paint.setColor(linesColor);
			paint.setStrokeWidth(linesWidth);
			int height=getHeight()/2+offset;
			int position=currentPosition;
			
			//循环画刻度，当画到上边界或起始值时则退出循环，去画下半部分刻度
			while(true){		
				//intervalsBetweenValues/2是指两个相邻值之间距离的中间那条稍微长一点的刻度的位置
				if(position%(intervalsBetweenValues/2)==0){
					//当刻度值的位置为刻度值旁边的时候则画长一点，并在旁边画上数字，否则就按普通刻度长度画
					if(position%intervalsBetweenValues==0){
						canvas.drawLine(getWidth(), height, getWidth()/2, height, paint);
						
						String valueString=Integer.toString(position/intervalsBetweenValues*valuesInterval);
						paint.setColor(valuesTextColor);
						canvas.drawText(valueString, getWidth()/2-paint.measureText(valueString)-dp2px(5), height+textHeight/3, paint);
						paint.setColor(linesColor);
					}else{
						canvas.drawLine(getWidth(), height, getWidth()*3/5, height, paint);
					}
					
				}else{
					canvas.drawLine(getWidth(), height, getWidth()*4/5, height, paint);
				}			
				
				//每画完一条刻度则递减position和height,当position=起始值，或height低于0，即超出边界时，退出循环
				position--;
				if(position<fromValue/valuesInterval*intervalsBetweenValues) break;
				height-=interval;						
				if(height<0-textHeight) break;
			}
			
			//画中间以下部分的刻度，和画上半部同理
			height=getHeight()/2+offset;
			position=currentPosition;
			while(true){
				position++;
				if(position>toValue/valuesInterval*intervalsBetweenValues) break;
				height+=interval;						
				if(height>getHeight()+textHeight) break;
				if(position%(intervalsBetweenValues/2)==0){
					if(position%intervalsBetweenValues==0){
						canvas.drawLine(getWidth(), height, getWidth()/2, height, paint);
						
						String valueString=Integer.toString(position/intervalsBetweenValues*valuesInterval);
						paint.setColor(valuesTextColor);
						canvas.drawText(valueString, getWidth()/2-paint.measureText(valueString)-dp2px(5), height+textHeight/3, paint);
						paint.setColor(linesColor);
					}else{
						canvas.drawLine(getWidth(), height, getWidth()*3/5, height, paint);
					}
					
				}else{
					canvas.drawLine(getWidth(), height, getWidth()*4/5, height, paint);
				}

			}
		}else{
			//画中间的指针
			paint.setColor(Color.RED);
			paint.setStrokeWidth(dp2px(2));
			canvas.drawLine(getWidth()/2, getHeight(), getWidth()/2, getHeight()/2, paint);
			
			//画中间左边部分的刻度
			paint.setColor(linesColor);
			paint.setStrokeWidth(linesWidth);
			int width=getWidth()/2+offset;
			int position=currentPosition;
			while(true){							
				if(position%(intervalsBetweenValues/2)==0){
					if(position%intervalsBetweenValues==0){
						canvas.drawLine(width, getHeight(), width, getHeight()/2, paint);
						
						String valueString=Integer.toString(position/intervalsBetweenValues*valuesInterval);
						paint.setColor(valuesTextColor);
						canvas.drawText(valueString, width-paint.measureText(valueString)/2, getHeight()/2-textHeight/2, paint);
						paint.setColor(linesColor);
					}else{
						canvas.drawLine(width, getHeight(), width, getHeight()*3/5, paint);
					}
					
				}else{
					canvas.drawLine(width, getHeight(), width, getHeight()*4/5, paint);
				}			
				position--;
				if(position<fromValue/valuesInterval*intervalsBetweenValues) break;
				width-=interval;						
				//这里需要额外加上以文本“10000”的长度作为偏移量，防止值文本很长的时候，文本还没完全退出边界就消失了
				if(width<0-paint.measureText("10000")) break;
			}
			
			//画中间右边部分的刻度
			width=getWidth()/2+offset;
			position=currentPosition;
			while(true){
				position++;
				if(position>toValue/valuesInterval*intervalsBetweenValues) break;
				width+=interval;						
				if(width>getWidth()+paint.measureText("10000")) break;
				if(position%(intervalsBetweenValues/2)==0){
					if(position%intervalsBetweenValues==0){
						canvas.drawLine(width, getHeight(), width, getHeight()/2, paint);
						
						String valueString=Integer.toString(position/intervalsBetweenValues*valuesInterval);
						paint.setColor(valuesTextColor);
						canvas.drawText(valueString, width-paint.measureText(valueString)/2, getHeight()/2-textHeight/2, paint);
						paint.setColor(linesColor);
					}else{
						canvas.drawLine(width, getHeight(), width, getHeight()*3/5, paint);
					}
					
				}else{
					canvas.drawLine(width, getHeight(), width, getHeight()*4/5, paint);
				}
			}
		}
	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//记录初始位置
			if(orientation==HORIZONTAL){
				oldX=(int)event.getX();
			}else{
				oldY=(int)event.getY();
			}			
			break;

		case MotionEvent.ACTION_MOVE:
			if(orientation==HORIZONTAL){
				//滑动的距离
				offset=(int)(event.getX()-oldX);
				//滑动的距离除以每个刻度间隔，得出滑动了多少个间隔，即在相应的刻度position上加上或减去该间隔数				
				if(Math.abs(offset)>=interval){
					currentPosition-=offset/interval;
					if(currentPosition>toValue/valuesInterval*intervalsBetweenValues) 
						currentPosition=toValue/valuesInterval*intervalsBetweenValues;
					if(currentPosition<fromValue/valuesInterval*intervalsBetweenValues) 
						currentPosition=fromValue/valuesInterval*intervalsBetweenValues;
					//记录好当前的偏移位置作为下次滑动偏移量的起始位置oldX
					oldX=(int)event.getX();
				}		
				//取余算好不够一个间隔的偏移量，用于ACTION_UP计算四舍五入
				offset%=interval;
			}else{
				//同HORIZONTAL模式
				offset=(int)(event.getY()-oldY);
				if(Math.abs(offset)>=interval){
					currentPosition-=offset/interval;
					if(currentPosition>toValue/valuesInterval*intervalsBetweenValues) 
						currentPosition=toValue/valuesInterval*intervalsBetweenValues;
					if(currentPosition<fromValue/valuesInterval*intervalsBetweenValues)
						currentPosition=fromValue/valuesInterval*intervalsBetweenValues;
					oldY=(int)event.getY();
				}		
				offset%=interval;
			}			
			//重绘，达到滑动动画效果
			invalidate();
			if(listener!=null){
				//通过一个接口将数据暴露出去
				listener.onValueChange(currentPosition*valuesInterval/intervalsBetweenValues);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			//根据offset处理，若大于间隔的一半，那么+1，否则-1
			if(offset>0 && offset>interval/2){
				currentPosition--;
				if(currentPosition<fromValue/valuesInterval*intervalsBetweenValues){
					currentPosition=fromValue/valuesInterval*intervalsBetweenValues;
				}
			}else if(offset<0 && Math.abs(offset)>interval/2){
				currentPosition++;				
				if(currentPosition>toValue/valuesInterval*intervalsBetweenValues){
					currentPosition=toValue/valuesInterval*intervalsBetweenValues;					
				}
			}
			//偏移量置0
			offset=0;	
			
			invalidate();
			if(listener!=null){
				//通过一个接口将数据暴露出去
				listener.onValueChange(currentPosition*valuesInterval/intervalsBetweenValues);
			}
			break;
		}

		return true;
	}
	
	private int sp2px(int sp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
	}
	
	private int dp2px(int dp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
	
	public void setOnValueChangeListener(OnValueChangeListener listener){
		this.listener=listener;
	}
	
	public void setValue(int value){
		currentPosition=value/valuesInterval*intervalsBetweenValues;
		invalidate();
	}
	
	public void setFromValue(int fromValue){
		this.fromValue=fromValue;
		invalidate();
	}
	
	public void setToValue(int toValue){
		this.toValue=toValue;
		invalidate();
	}

}
