package com.ve.view.editor;

import android.content.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

abstract public class ScrollableView extends View {
	private Scroller scroller;
	private TouchHandler toucher;
	private float zoomValue=1;
	private long lastScrollTime;
	private float zoomMin=0.5f,zoomMax=2;
	
	public ScrollableView(Context context) {
		super(context);
		init(context);
	}
	public ScrollableView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		init(context);
	}
	public ScrollableView(Context context, AttributeSet attrs, int defStyle) { 
		super(context,attrs,defStyle); 
		init(context);
	}

	public void onSingleTapUp(MotionEvent e) {
	}
	private void init(Context context) {
		scroller=new Scroller(context);
		toucher=new TouchHandler();
	}
	private void flingScroll(int velocityX, int velocityY) {
		scroller.fling(getScrollX(), getScrollY(), velocityX, velocityY,0, getMaxScrollX(), 0, getMaxScrollY());
		postInvalidate();
	}
	private void scrollView(float distanceX, float distanceY) {
		int newX = (int) distanceX + getScrollX();
		int newY = (int) distanceY + getScrollY();

		int maxWidth = Math.max(getMaxScrollX(), getScrollX());
		if (newX > maxWidth) {
			newX = maxWidth;
		} else if (newX < 0) {
			newX = 0;
		}
		int maxHeight = Math.max(getMaxScrollY(), getScrollY());
		if (newY > maxHeight) {
			newY = maxHeight;
		} else if (newY < 0) {
			newY = 0;
		}
		smoothScrollTo(newX, newY);

	}
	private void zoom(float zoomValue){
		if (zoomValue>=zoomMin&&zoomValue<=zoomMax){
			this.zoomValue=zoomValue;
			onZoom(zoomValue);
		}
	}
	
	
	public void onZoom(float zoomValue) {
	}
	public float getZoomValue() {
		return zoomValue;
	}
	public int getMaxScrollX() {
		return Math.max(0,getContentWidth()-getVisiableWidth());
	}
	public int getMaxScrollY() {
		return Math.max(0,getContentHeight()-getVisiableHeight());
	}
	public abstract int getContentWidth();
	public abstract int getContentHeight();
	
	
	public final int getVisiableHeight() {
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}
	public final int getVisiableWidth(){
		return getWidth()-getPaddingLeft()-getPaddingRight();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		toucher.onTouchEvent(event);
		return true;
	}
	
	
	@Override
	protected int computeVerticalScrollOffset() {
		return getScrollY();
	}
	@Override
	protected int computeVerticalScrollRange() {
		return getContentHeight() + getPaddingTop() + getPaddingBottom();
	}
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}
    public final void smoothScrollBy(int dx, int dy) {
        if (getHeight() == 0) {
            return;
        }
        long duration = AnimationUtils.currentAnimationTimeMillis() - lastScrollTime;
        if (duration > 250) {
            final int scrollY = getScrollY();
			final int scrollX = getScrollX();
            scroller.startScroll(scrollX, scrollY, dx, dy);
            postInvalidate();
        } else {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        lastScrollTime = AnimationUtils.currentAnimationTimeMillis();
    }
    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
    }
	public final boolean isFlingScrolling() {
		return !scroller.isFinished();
	}
	public final void stopFlingScrolling() {
		scroller.forceFinished(true);
	}
	public final void setZoomRange(float min,float max){
		this.zoomMin=min;
		this.zoomMax=max;
	}

	
	private class TouchHandler extends GestureDetector.SimpleOnGestureListener {
		private GestureDetector gestureDetector;
		private float lastDist;
		private float lastX;
		private float lastY;
		private float lastSize;
		private int fling;

		public TouchHandler() {
			gestureDetector = new GestureDetector(getContext(), this);
			gestureDetector.setIsLongpressEnabled(true);
		}
		public boolean onUp(MotionEvent e) {
			lastDist = 0;
			fling = 0;
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (e2.getPointerCount() == 1) {
				if (fling == 0) {
					fling = Math.abs(distanceX) > Math.abs(distanceY) ?1: -1;
				}
				if (fling == 1) {
					distanceY = 0;
				} else if (fling == -1) {
					distanceX = 0;
				}
				scrollView(distanceX, distanceY);
			}
			if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				onUp(e2);
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			ScrollableView.this.onSingleTapUp(e);
			return true;
		}

		

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (fling == 1){
					velocityY = 0;
				}else if (fling == -1){
					velocityX = 0;
				}
				flingScroll((int) -velocityX, (int) -velocityY);
			onUp(e2);
			return true;
		}

		private float fingerSpace(MotionEvent event) { 
			float x = event.getX(0) - event.getX(1); 
			float y = event.getY(0) - event.getY(1); 
			return (float)Math.sqrt(x * x + y * y); 
		}  

		private boolean onTouchZoom(MotionEvent e) {
			if (e.getAction() == MotionEvent.ACTION_MOVE) {
				if (e.getPointerCount() == 2) {
					if (lastDist == 0) {
						float x = e.getX(0) - e.getX(1); 
						float y = e.getY(0) - e.getY(1); 
						lastDist = (float) Math.sqrt(x * x + y * y);
						lastX = (e.getX(0) + e.getX(1)) / 2; 
						lastY = (e.getY(0) + e.getY(1)) / 2; 
						lastSize = zoomValue;
					}

					float dist=fingerSpace(e);
					if (lastDist != 0) {
						zoom(lastSize * (dist / lastDist));
						
					}

					return true;
				}
			}
			lastDist = 0;
			return false;
		}
		private boolean onTouchEvent(MotionEvent event) {
			onTouchZoom(event);
			boolean handled = gestureDetector.onTouchEvent(event);
			if (!handled && (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				handled = onUp(event);
			}
			return handled;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}

	}
}
