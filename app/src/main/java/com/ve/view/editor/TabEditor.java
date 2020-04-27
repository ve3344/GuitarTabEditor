package com.ve.view.editor;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.ve.view.editor.GuitarTab.Row;
import com.ve.view.editor.GuitarTab.*;

public class TabEditor extends ScrollableView {

	protected GuitarTab tab;
	protected float lineWidth=2;
	protected float chordHeight=80;
	protected float rowPadding=20;
	protected float textSize=28;
	protected float lineHeight;
	protected float textWidth;
	protected float rowHeight;
	protected int contentWidth;
	protected Paint paint;
	
	protected Cursor cursor;
	protected CursorListener cursorListener;


	public TabEditor(Context context) {
		super(context);
		init(context);
	}
	public TabEditor(Context context, AttributeSet attrs, int defStyle) { 
		super(context, attrs, defStyle); 
		init(context);
	} 
	public TabEditor(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		init(context);
	}

	public void setTab(GuitarTab tab) {
		if (tab!=null){
		this.tab = tab;
		cursor.setTab(tab);
		
		}
	}

	public GuitarTab getTab() {
		return tab;
	}
	private void init(Context context) {

		tab = new GuitarTab();
		cursor = new Cursor(this);
		cursor.setTab(tab);
		addRowBeforeCursor();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setStrokeWidth(lineWidth);
		paint.setTextSize(textSize);

		setZoomRange(1, 2f);

		onZoom(1);
	}
	@Override
	public int getContentWidth() {
		return getVisiableWidth();
	}

	@Override
	public int getContentHeight() {
		return (int)(rowHeight * tab.getRowCount());
	}

	@Override
	public void onZoom(float zoomValue) {
		paint.setTextSize(28 * zoomValue);
		lineWidth = zoomValue * 2;
		chordHeight = zoomValue * 80;
		rowPadding = zoomValue * 20;
		textWidth = (int) (getMaxNumberWidth() * 2);
		lineHeight = (int) (paint.descent() - paint.ascent());
		rowHeight = lineHeight * GuitarTab.LINE_COUNT + chordHeight + rowPadding * 2;
		invalidate();
	}

	public float getNoteLengthOfRow(int row) {
		return getNoteLengthOfRow(tab.getRow(row));
	} 
	public void shortToast(String s) {
		Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
	}
	public void setCursorLine(int value) {
		cursor.getCursorNote().line[cursor.line] = value;
		invalidate();
	}
	public void setCursorNote(Note note) {
		cursor.getCursorNote().set(note);
		invalidate();
	}
	public Note getCursorNote() {
		return cursor.getCursorNote();
	}
	public void setCursorChord(Chord chord) {
		cursor.getCursorRow().setChord(chord);
	}
	public Chord getCursorChord() {
		return cursor.getCursorRow().getChord();
	}
	public Row getCursorRow() {
		return cursor.getCursorRow();
	}
	private float getMaxNumberWidth() {
		float max=0;
		for (int i=0;i < 10;i++) {
			max = Math.max(paint.measureText("" + i), max);
		}
		return max;
	}

	public void deleteNote() {
		if (cursor.deleteNote()) {
			invalidate();
		}
	}
	public void addRowBeforeCursor() {
		cursor.addRowBefore();
		invalidate();
	}
	public void addRowAfterCursor() {
		cursor.addRowAfter();
		invalidate();
	}

	public void addNoteBeforeCursor(Note note) {
		cursor.addNoteBefore(note);
		invalidate();
	}


	public void addNoteAfterCursor(Note note) {
		cursor.addNoteAfter(note);
		invalidate();
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursorChrod(int index) {
		if (index >= 0 && index < Chord.values().length) {
			cursor.getCursorRow().setChord(Chord.values()[index]);
			invalidate();
		}
	}

	public void setCursorListener(CursorListener cursorListener) {
		this.cursorListener = cursorListener;
	}

	public interface CursorListener {
		void onChangeMode(boolean inChord);
		void onSkip(Cursor cursor, Note note);
	}

	@Override
	public void onSingleTapUp(MotionEvent event) {
		int x=screenToViewX((int) event.getX());
		int y=screenToViewY((int) event.getY());
		int rowIndex=(int)((y - lineHeight / 2) / (rowHeight));

		if (rowIndex >= 0 && rowIndex < tab.getRowCount()) {

			float noteLength= getNoteLengthOfRow(rowIndex);
			cursor.column = (int)(x / noteLength);
			cursor.row = rowIndex;
			float lineY=y - rowIndex * rowHeight - rowPadding - chordHeight;
			if (lineY < 0) {
				cursor.setInChord(true);
			} else {
				cursor.setInChord(false);
				cursor.line = (int)(lineY / lineHeight);
				if (cursor.line < 0) {
					cursor.line = 0;
				} else if (cursor.line >= GuitarTab.LINE_COUNT) {
					cursor.line = GuitarTab.LINE_COUNT - 1;
				}
			}

			if (cursorListener != null) {
				cursorListener.onSkip(cursor, cursor.getCursorNote());
			}
			invalidate();
		}
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

	private void drawContent(Canvas canvas) {
		float x=0,y=rowPadding + chordHeight + lineHeight / 2,top=0;
		int rowIndex=0,columnIndex=0;

		for (Row row:tab.getRows()) {
			columnIndex = 0;
			x = 0;
			float lineLength= getLineLength(row);
			float noteLength= getNoteLengthOfRow(row);
			if (cursor.row == rowIndex && cursor.isInChord()) {
				cursor.onDrawInChrod(canvas, x, top);
				paint.setColor(0xfffefefe);
				drawChord(canvas, row.getChord(), top);
			} else {
				paint.setColor(0xff383547);
				drawChord(canvas, row.getChord(), top);

			}

			for (Note note:row.getNotes()) {

				if (rowIndex == cursor.row  && columnIndex == cursor.column && !cursor.isInChord()) {

					cursor.onDraw(canvas, x, y);
					paint.setColor(0xfffefefe);
					drawNote(canvas, note, x, y, lineLength, noteLength);
				} else {
					paint.setColor(0xff383547);
					drawNote(canvas, note, x, y, lineLength, noteLength);
				}


				x += noteLength;
				columnIndex++;
			}

			rowIndex++;
			y += rowHeight;
			top += rowHeight;
		}
	}

	private void drawChord(Canvas canvas, Chord chord, float top) {
		float x=getContentWidth() / 2;
		float y=top + chordHeight / 2 - (paint.descent() + paint.ascent()) / 2;
		if (chord == Chord.NULL) {
			canvas.drawCircle(x, top + chordHeight / 2, 3, paint);
		} else {
			canvas.drawText(chord.toString(), x, y, paint);
		}
	}







	public float getNoteLengthOfRow(Row row) {
		return (textWidth + 2 * getLineLength(row));
	}
	private float getLineLength(Row row) {
		int noteCount=row.length();
		if (noteCount == 0) {
			//理应不能到这
			return 0;
		} else {
			return (getContentWidth() - noteCount * textWidth) / (noteCount * 2);
		}

	}


	private void drawNote(Canvas canvas, Note note, float x, float top, float lineLength, float noteLength) {
		int dx=0,dy=0;

		for (int i=0;i < GuitarTab.LINE_COUNT;i++) {
			if (note.isEmpty(i)) {
				canvas.drawLine(x, top + dy, x + noteLength, top + dy, paint);
				canvas.drawCircle(x + noteLength / 2, top + dy, 3, paint);
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

	protected int screenToViewX(int x) {
		return x - getPaddingLeft() + getScrollX();
	}
	protected int screenToViewY(int y) {
		return y - getPaddingTop() + getScrollY();
	}

}
