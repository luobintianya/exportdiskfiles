package com.melon.exportdiskfiles.core;

import java.util.ArrayList;

 
 
public class FileAggregate { 
	private String fileSuffix;
	private Integer count;// file times
	private ArrayList<String> paths = new ArrayList<String>();// store the file  paths  same  
	public String getFileSuffix() {
		return fileSuffix;
	}
	public Integer getCount() {
		return count;
	}
	public ArrayList<String> getPaths() {
		return paths;
	}
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public void setPaths(ArrayList<String> paths) {
		this.paths = paths;
	} 

}
