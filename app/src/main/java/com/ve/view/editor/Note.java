package com.ve.view.editor;

public class Note {
	//音位
	public final static int systemNum=32;//进制([22,63])
	public int line[];
	public Note(){
		line=new int[]{-1,-1,-1,-1,-1,-1};
	}

	public void set(Note note) {
		for (int i=0;i<line.length;i++){
			line[i]=note.line[i];
		}
	}
	public boolean isEmpty(int i){
		return line[i]<0;
	}
	public String getLineText(int i){
		return line[i]==0?"X":""+line[i];
	}
	
	public int toInt(){
		int bitIndex=1, r=0;
		for (int i=0;i<GuitarTab.LINE_COUNT;i++){
			r+=(line[i]+1)*bitIndex;
			bitIndex*=systemNum;
			
		}
		return r;
	}
	public void fromInt(int num){
		for (int i=0;i<GuitarTab.LINE_COUNT&&num>0;i++){
			line[i]= num%systemNum-1;
			num/=systemNum;
		}
	}
	
	 
}
