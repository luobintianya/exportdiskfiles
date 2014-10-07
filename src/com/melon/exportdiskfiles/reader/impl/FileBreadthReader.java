package com.melon.exportdiskfiles.reader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.melon.exportdiskfiles.core.ExportDiskFilesIntoCSV;
import com.melon.exportdiskfiles.core.FileAggregate;
import com.melon.exportdiskfiles.core.FileAttribute;
import com.melon.exportdiskfiles.reader.FileInfoReader;

/**
 * BFS
 * 
 * @author Robin
 *
 */
public class FileBreadthReader implements FileInfoReader {
	private ExportDiskFilesIntoCSV exportCsv=new ExportDiskFilesIntoCSV(); 
	private ArrayList<FileAggregate> list=new ArrayList<FileAggregate>();   
	private Queue<File> unreadfiles=new LinkedBlockingQueue<File>();    
	public FileBreadthReader(String outpath){
		exportCsv.initialize(outpath);  
	} 
	@Override
	public void ReadFileInfo(String filepath) {  
		startReadFile(new File(filepath),list);
		exportCsv.closeFile();  
	}  
	private void startReadFile(File path,ArrayList<FileAggregate> list){ 
		unreadfiles.add(path);  
		while(!unreadfiles.isEmpty()){
			File file=	unreadfiles.remove(); 
			if(file.isFile()){ 
				FileAttribute fileAttr=new FileAttribute();
				Calendar ca= Calendar.getInstance();
			    ca.setTimeInMillis(file.lastModified());
				String fileName=file.getName();
				fileAttr.setDataTime(String.format("%1$tY-%1$te-%1$tm-%1$tT", ca));
				fileAttr.setFileName(fileName);
				fileAttr.setFilePath(file.getAbsolutePath());  
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
			if(file.isDirectory()){ 
				File[] subFiles = file.listFiles(); 
				if(subFiles!=null){  
					for(File subfile:subFiles){
						String fileName=subfile.getName();   
						if(IGNORFILES.indexOf(fileName)>=0 ) {
							continue;
						}  
						unreadfiles.add(subfile);
					}
				} 
			} 
			
		} 
		
		exportCsv.setIsdone(true);
	 }
}
