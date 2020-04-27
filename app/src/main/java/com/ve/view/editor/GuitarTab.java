package com.ve.view.editor;
import java.util.*;
import java.io.*;


public class GuitarTab {
	public static final int LINE_COUNT=6;
	private String title="";
	private String path="";
	private LinkedList<Row> rows;

	public GuitarTab(String path) {
		rows = new LinkedList<Row>();
		this.path = path;
		load(new File(path));
	}
	

	public GuitarTab() {
		rows = new LinkedList<Row>();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	public void reload(){
		load(new File(path));
	}
	public void save(){
		save(new File(path));
	}
	public LinkedList<Row> getRows() {
		return rows;
	}
	public int getRowCount() {
		return rows.size();
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void removeRow(int row) {
		rows.remove(row);
	}
	public void addRow(int index, Row row) {
		rows.add(index, row);
	}
	public void addRow(Row row) {
		rows.add(row);
	}
	protected int getNoteCountOfRow(int row) {
		return rows.get(row).length();
	}

	public Row getRow(int row) {
		return rows.get(row);
	}
	public void save(File file) {
		OutputStreamWriter out=null;
		try {
			 out=new OutputStreamWriter(new FileOutputStream(file));
			for (Row row:rows) {
				out.append(row.chord.toString()).append(":");
				for (Note note:row.notes) {
					out.append(String.valueOf( note.toInt())).append(",");
				}
				out.append("\n");
			}
			out.close();
			
			} catch (IOException e2) {}
		
	}
	public void load(File file) {
		rows.clear();
		try {
			InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String buff="";
			while ((buff = bufReader.readLine()) != null) {
				addRow(parse(buff));
			}
			inputReader.close();
			bufReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	private Row parse(String buff) {
		Row row=new Row();
		
		String[] info=buff.split(":");
		if (info.length>0){
		row.chord=Chord.valueOf(info[0]);
		}
		String[] notes=info[1].split(",");
		for (int i=0;i<notes.length;i++){
			Note note=new Note();
			note.fromInt(Integer.parseInt(notes[i]));
			row.addNote(note);
		}
		return row;
	}
	
	public static enum Chord {
		NULL,
		C,
		C7,
		F,
		G,
		G7,
		B7,
		D,
		Dm,
		D7,
		A,
		Am,
		A7,
		E,
		Em,
		E7

		}


	public static class Row {
		private Chord chord=Chord.NULL;
		private LinkedList<Note> notes;
		public Row() {
			notes = new LinkedList<Note>();
			//notes.add(new Note());
		}

		public void addNote(int column, Note note) {
			notes.add(column, note);
		}
		public void addNote( Note note) {
			notes.add( note);
		}
		public void setChord(Chord chord) {
			this.chord = chord;
		}

		public Chord getChord() {
			return chord;
		}

		public LinkedList<Note> getNotes() {
			return notes;
		}
		public Note getNote(int column) {
			return notes.get(column);
		}
		public int length() {
			return notes.size();
		}

	}
}
