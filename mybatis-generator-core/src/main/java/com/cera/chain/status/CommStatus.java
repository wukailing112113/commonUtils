package com.cera.chain.status;

public enum CommStatus {
	UNUSE((byte) 0, "弃用"), INUSE((byte) 1, "在用");
	
	private Byte index;
	private String name;
	
	private CommStatus(byte index, String name){
		this.index =  index ;
		this.name = name ;
	}
	
	
	public Byte getIndex() {
		return index;
	}
	public void setIndex(Byte index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
