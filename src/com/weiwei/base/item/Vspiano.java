package com.weiwei.base.item;



public class Vspiano {
	private int id;
	private String pianoName;//曲名
	private boolean ischeck;//是否选中
	private int[] pianoId;//曲谱	
	private boolean ischecked;
    public Vspiano(){
    	
    }
       
	
	public Vspiano(int id, String pianoName, int[] pianoId) {
		super();
		this.id = id;
		this.pianoName = pianoName;
		this.pianoId = pianoId;
	}


	public Vspiano(String pianoName, boolean ischeck, int[] pianoId) {
		super();
		this.pianoName = pianoName;
		this.ischeck = ischeck;
		this.pianoId = pianoId;
	}
	public String getPianoName() {
		return pianoName;
	}
	public void setPianoName(String pianoName) {
		this.pianoName = pianoName;
	}
	public boolean isIscheck() {
		return ischeck;
	} 
	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	public int[] getPianoId() {
		return pianoId;
	}
	public void setPianoId(int[] pianoId) {
		this.pianoId = pianoId;		
	}	
	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
		
	}			
