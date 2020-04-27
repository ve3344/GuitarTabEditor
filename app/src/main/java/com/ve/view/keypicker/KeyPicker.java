package com.ve.view.keypicker;


import android.animation.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;

public class KeyPicker extends View implements KeyItemListener {

	public void setSelectable(boolean selectable) {
		if (selectable!=this.selectable){
		this.selectable = selectable;
		invalidate();
		}
		radius=selectable?radiusMin:0;
	}
	@Override
	public void onItemClick(KeyPicker keyPicker, int index) {}
	@Override
	public String onGetKeyText(KeyPicker keyPicker, int index) {
		return "";
	}


	public void setItemListener(KeyItemListener itemListener) {
		this.itemListener = itemListener;
	}
	private KeyItemListener itemListener=this;
	private Paint paint;
	private int index=0;
	private int rowCount=2,columnCount=11;

	private float keySpace,keyWidth,keyHeight;
	private float radius,radiusMin,radiusMax;
	private ObjectAnimator animator;
	private boolean touching=false;
	private boolean selectable=true;
	public KeyPicker(Context context) {
		super(context);
		init(context);
	}
	public KeyPicker(Context context, AttributeSet attrs, int defStyle) { 
		super(context, attrs, defStyle); 
		init(context);
	} 
	public KeyPicker(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		init(context);
	}
	public void setBody(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		invalidate();
	}
	private void init(Context context) {
		paint = new Paint();
		paint.setTextSize(30);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setFakeBoldText(true);

		keyWidth = paint.measureText("  ");
		radiusMax = keyWidth * 4;
		radiusMin = keyWidth * 2;
		radius = radiusMin;
		animator = ObjectAnimator.ofFloat(this, "radius", 0f, 0f);
		animator.setInterpolator(new AccelerateInterpolator());

	}
	public void setRadius(float radius) {
		this.radius = radius;
		invalidate();
	}
	public void setSelectItem(int index) {
		this.index = index;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				animator.setFloatValues(radiusMin, radiusMax);
				animator.setDuration(200);
				animator.start();
				int row =(int)((event.getY()) / keyHeight);
				int column = (int)((event.getX() - keySpace / 2) / keySpace);
				index = row * columnCount + column;
				itemListener.onItemClick(this, index);
				
				touching = true;
				break;
			case MotionEvent.ACTION_UP:
				if (selectable){
					animator.setFloatValues(radius, radiusMin);
					animator.setDuration(500);
				}else{
					animator.setFloatValues(radius, 0);
					animator.setDuration(300);
				}
				

				animator.start();
				touching = false;
				break;
		}


		return true;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		keyHeight = getHeight() / rowCount;
		keySpace = getWidth() / (columnCount + 1);
		canvas.clipRect(0, 0, getWidth(), getHeight());
		paint.setColor(0x1d122310);

		float paintY=keyHeight / 2,paintX;
		int paintIndex=0;
		for (int y=0;y < rowCount;y++) {
			paintX=keySpace / 2;
			for (int x=0;x < columnCount;x++) {

				if ( paintIndex == index) {
					paint.setColor(0x8cafafaf);
					canvas.drawCircle(paintX + keySpace / 2, paintY, radius, paint);
					if (touching) {
						paint.setColor(0x2cffffff);
						canvas.drawCircle(paintX + keySpace / 2, paintY, radiusMax, paint);

					}
				}

				paint.setColor(0xfff9f9f9);
				String text=itemListener.onGetKeyText(this, paintIndex);
				canvas.drawText(text, paintX + keySpace / 2, paintY - (paint.descent() + paint.ascent()) / 2, paint);
				paintX += keySpace;
				paintIndex++;
			}
			paintY += keyHeight;
		}

	}



}
