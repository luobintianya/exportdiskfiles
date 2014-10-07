package com.melon.exportdiskfiles.reader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.melon.exportdiskfiles.core.ExportDiskFilesIntoCSV;
import com.melon.exportdiskfiles.core.FileAggregate;
import com.melon.exportdiskfiles.core.FileAttribute;
import com.melon.exportdiskfiles.reader.FileInfoReader;

public class FileRecursionReader implements FileInfoReader{ 
	private ExportDiskFilesIntoCSV exportCsv=new ExportDiskFilesIntoCSV(); 
	private ArrayList<FileAggregate> list=new ArrayList<FileAggregate>();  
 
	
	public FileRecursionReader(String outpath){
		exportCsv.initialize(outpath);  
	}
	
	@Override
	public void ReadFileInfo(String filepath) { 
		// TODO Auto-generated method stub 
		 AtomicInteger deep=new AtomicInteger(0); 
		 startIterate(new File(filepath),deep,list);  
		 exportCsv.closeFile();
	}
	public void startIterate(File path,AtomicInteger deep,ArrayList<FileAggregate> list){  
		if(path.isFile()){
			FileAttribute fileAttr=new FileAttribute();
			Calendar ca= Calendar.getInstance();
		    ca.setTimeInMillis(path.lastModified());
			String fileName=path.getName();
			fileAttr.setDataTime(String.format("%1$tY-%1$te-%1$tm-%1$tT", ca));
			fileAttr.setFileName(fileName);
			fileAttr.setFilePath(path.getAbsolutePath());  
			//System.out.println(fileName);
			if(fileName.lastIndexOf('.')>0){
			fileAttr.setSuffix(fileName.substring(fileName.lastIndexOf('.'),fileName.length())); 
			} 
			if(IGNORFILES.indexOf(fileAttr.getSuffix().toLowerCase())<0 ){
				exportCsv.exportToCsv(fileAttr);  
			}else{
				//System.out.println("ignore file name:"+fileName);
			}
			
		}   
		if (path.isDirectory()){// if file is directory then iterate method
			File[] subFiles = path.listFiles();
			if(subFiles==null) return ; 
			deep.incrementAndGet();
			for(File file:subFiles){
				String fileName=file.getName();  
			//	System.out.println(fileName);
				if(IGNORFILES.indexOf(fileName)>=0 ) {
					continue;
				}
			  startIterate(file,deep,list); 
			}
			deep.decrementAndGet(); 
		}
		if(deep.get()==0){
			exportCsv.setIsdone(true);
		}  
	} 
}
