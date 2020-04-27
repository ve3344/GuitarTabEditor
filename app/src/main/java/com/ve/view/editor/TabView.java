package com.ve.view.editor;
import android.content.*;
import android.graphics.*;
import android.util.*;

public class TabView extends ScrollableView {
	protected GuitarTab tab;
	protected float lineWidth=1.5f;
	protected float chordHeight=26;
	protected float rowPadding=8;
	protected float textSize=20;
	protected float lineHeight;
	protected float textWidth;
	protected float rowHeight;
	private int contentWidth,contentHeight=0;
	protected Paint paint;
	public final static int NOTE_COUNT_PRE_ROW=16;
	public TabView(Context context) {
		super(context);
		init(context);
	}
	public TabView(Context context, AttributeSet attrs, int defStyle) { 
		super(context, attrs, defStyle); 
		init(context);
	} 
	public TabView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		init(context);
	}

	public void setTab(GuitarTab tab) {
		this.tab = tab;
	}

	public GuitarTab getTab() {
		return tab;
	}
	private void init(Context context) {
		tab = new GuitarTab();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setStrokeWidth(lineWidth);
		paint.setTextSize(textSize);

		setZoomRange(1, 1);

		
		textWidth = (int) (getMaxNumberWidth() * 2);
		lineHeight = (int) (paint.descent() - paint.ascent());
		rowHeight = lineHeight * GuitarTab.LINE_COUNT + chordHeight + rowPadding * 2;
		
	}

	
	private float getMaxNumberWidth() {
		float max=0;
		for (int i=0;i < 10;i++) {
			max = Math.max(paint.measureText("" + i), max);
		}
		return max;
	}
	@Override
	public int getContentWidth() {
		return getVisiableWidth();
	}

	@Override
	public int getContentHeight() {
		return contentHeight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.clipRect(getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop(),
						getScrollX() + getWidth() - getPaddingRight(), getScrollY() + getHeight() - getPaddingBottom());
		canvas.translate(getPaddingLeft(), getPaddingTop());
		drawContent(canvas);
		canvas.restore();
	}
	
	@Override
	public void onZoom(float zoomValue) {
		
	}
	private void drawContent(Canvas canvas) {


		float x=0,y=rowPadding + chordHeight + lineHeight / 2,top=0;
		int rowIndex=0,columnIndex=0;
		float lineLength= getLineLength();
		float noteLength= getNoteLengthOfRow();
		for (GuitarTab.Row row:tab.getRows()) {
			
			paint.setColor(0xff383547);
			drawChord(canvas, row.getChord(), top,x,row.getNotes().size()*(int)noteLength);
			canvas.drawLine(x,y,x,y+lineHeight*(GuitarTab.LINE_COUNT-1),paint);
			for (Note note:row.getNotes()) {
				paint.setColor(0xff383547);
				drawNote(canvas, note, x, y, lineLength, noteLength);

				x += noteLength;
				columnIndex++;
				if (columnIndex>=NOTE_COUNT_PRE_ROW){
					columnIndex=0;
					x=0;
					rowIndex++;
					y += rowHeight;
					top += rowHeight;
				}
			}
			canvas.drawLine(x,y,x,y+lineHeight*(GuitarTab.LINE_COUNT-1),paint);
		}
		contentHeight=(int)(top+rowHeight);
	}

	private void drawChord(Canvas canvas, GuitarTab.Chord chord, float top,float left,int size) {
		float x= left+ size/ 2;
		if (x>getVisiableWidth()){
			x=(left+getVisiableWidth())/2;
		}
		float y=top + chordHeight / 2 - (paint.descent() + paint.ascent()) / 2;
		if (chord != GuitarTab.Chord.NULL) {
			canvas.drawText(chord.toString(), x, y, paint);
		}
	}







	public float getNoteLengthOfRow() {
		return (textWidth + 2 * getLineLength());
	}
	private float getLineLength() {
		int noteCount=NOTE_COUNT_PRE_ROW;

		return (getContentWidth() - noteCount * textWidth) / (noteCount * 2);

	}


	private void drawNote(Canvas canvas, Note note, float x, float top, float lineLength, float noteLength) {
		int dx=0,dy=0;

		for (int i=0;i < GuitarTab.LINE_COUNT;i++) {
			if (note.isEmpty(i)) {
				canvas.drawLine(x, top + dy, x + noteLength, top + dy, paint);
				
			} else {

				dx = 0;
				canvas.drawLine(x + dx, top + dy, x + dx + lineLength, top + dy, paint);
				dx += lineLength;
				canvas.drawText(note.getLineText(i), x + dx + textWidth / 2, top + dy - (paint.descent() + paint.ascent()) / 2, paint);
				dx += textWidth;
				canvas.drawLine(x + dx, top + dy, x + dx + lineLength, top + dy, paint);
			}
			dy += lineHeight;

		}
	}
}
