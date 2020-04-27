package com.ve.view.editor;
import android.graphics.*;
import com.ve.view.editor.GuitarTab.Row;
public class Cursor {
	protected TabEditor tabView;
	protected int row,column;
	protected int line;
	protected Paint paint;
	protected boolean inChord;
	protected GuitarTab tab;
	public Cursor(TabEditor tabView) {
		this.tabView = tabView;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
	}

	

	public void setTab(GuitarTab tab) {
		this.tab = tab;
		row=0;
		column=0;
		line=0;
	}

	public GuitarTab getTab() {
		return tab;
	}
	public Note getCursorNote() {
		return tab.getRow(row).getNote(column);
	}

	public Row getCursorRow(){
		return tab.getRow(row);
	}

	public boolean deleteRow(){
		if (tab.getRowCount()>1){
			tab.removeRow(row);
			if (tab.getRowCount()<=row){
				row--;
			}
			//cursor.row 
			return true;
		}
		return false;
	}
	public boolean deleteNote(){

		if (getCursorRow().length()>1){
			getCursorRow().getNotes().remove(column);
			if (getCursorRow().length()<=column){
				column--;
			}
			return true;
		}else{
			return deleteRow();
		}

	}
	public void addNoteBefore(Note note) {
		getCursorRow().addNote(column,note);
	}
	public void addNoteAfter(Note note) {
		getCursorRow().addNote(column+1,note);
		column++;
	}
	public void addRowBefore(){
		Row r=new Row();
		r.addNote(new Note());
		tab.addRow(row,r);
		column=0;
	}
	public void addRowAfter(){
		Row r=new Row();
		r.addNote(new Note());
		tab.addRow(row+1,r);
		column=0;
		row++;
	}
	public void onDrawInChrod(Canvas canvas, float x, float top) {
		
		float rowWidth=tabView.getContentWidth();
		
		paint.setColor(0xff8b90af);
		canvas.drawRoundRect(x, top, x + rowWidth, top + tabView.chordHeight, 16, 16, paint);
		paint.setColor(0xff444153);
		canvas.drawCircle(x+rowWidth/2,top+tabView.chordHeight/2,tabView.chordHeight/2,paint);
		
	}

	public void setInChord(boolean inChord) {
		if (this.inChord!=inChord&&tabView.cursorListener!=null){
			tabView.cursorListener.onChangeMode(inChord);
		}
		this.inChord = inChord;
	}

	public boolean isInChord() {
		return inChord;
	}

	public int getLine() {
		return line;
	}

	

	public void set(int row, int column) {
		this.row = row;
		this.column = column;
	}



	public void onDraw(Canvas canvas, float x, float y) {
		float dy=tabView.chordHeight+ tabView.rowPadding+ tabView.lineHeight / 2;
		float noteLength=tabView.getNoteLengthOfRow(row);
		float top =y- dy;
		paint.setColor(0xff8b90af);
		canvas.drawRoundRect(x, top+tabView.chordHeight, x + noteLength, top + tabView.rowHeight, 16, 16, paint);
		paint.setColor(0xff444153);
		canvas.drawCircle(x+noteLength/2,y+tabView.lineHeight*line,tabView.textWidth/2,paint);
		
	}

}
