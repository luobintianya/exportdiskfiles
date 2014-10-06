package com.melon.exportdiskfiles.core;

import java.io.UnsupportedEncodingException;

public class FileAttribute { 
	private String fileName;
	private String suffix="";
	private String filePath;
	private String dataTime;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	@Override
	public String toString() {
		if(fileName.indexOf(",")>0 && fileName.indexOf("\"")<0 ){// only plus one time
			fileName="\""+fileName+"\"";
			filePath="\""+filePath+"\"";
		} 
		return  fileName + "," + suffix + "," + filePath + "," + dataTime + "\n";
	}
	public int getSize(){
		try {
			return toString().getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return toString().getBytes().length;
		}	
		
	}


}
