package com.melon.exportdiskfiles.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.filechooser.FileSystemView;

/**
 * 
 * this class is used to export the disk file to a csv file. <br>
 * you can used the java -jar exportdiskfiles.jar disk path. <br>
 * the output file path is c:\\fileDB\\xxx.csv <br> 
 * 
 * </br>
 * Main method in this class.
 * 
 * @author Robin(luobintianya@sina.com)
 *
 */
public class ExportDiskFiles { 
	private static String outfile = "c:\\fileDB\\%1$s.csv"; 
	private static String ignorfile="$RECYCLE.BIN $recycle.bin eclipse hybris springsource .png .class ";  
	private static final String DATETIMEFORMATTER="%1$tY-%1$tm-%1$te-%1$tT";
	private static ExportDiskFilesIntoCSV exportCsv=new ExportDiskFilesIntoCSV();
	public  static void main(String args[]){ 
		 System.out
		 .println("       this tools will used to export disk files to csv files(c://filedDB/xxx.csv)");
		 System.out
		 .println("       author:lixin wang(luobintianya@sina.com) ");
		 if (args.length != 1) {
			 System.out
			 .println("       Usage: java -jar exportdiskfiles.jar disk(E://) ");
			 System.out
			 .println("       which disk files will be export"); 
	
		       return;
		     }
		 System.out
		 .println("       ================================================");
		 String rootpath = args[0]; 
		 System.out.println("the Root path is:"+rootpath); 
 		 FileSystemView fsv = FileSystemView.getFileSystemView(); 
	   	 File rootfile=new  File(rootpath);  
		 String rootpan=fsv.getSystemDisplayName(rootfile);
		 System.out.println(rootpan);
		 if(rootpan.indexOf(" ")>0){
			 rootpan=rootpan.substring(0,rootpan.indexOf(" "));
		 }
		System.out.println(rootpan); 
		outfile = String.format(outfile, rootpan);
		System.out.println(outfile);
		exportCsv.initialize(outfile); 
		ExportDiskFiles ds = new ExportDiskFiles();
		Calendar start = Calendar.getInstance(); 
		exportCsv.setIsdone(ds.enterRoot(rootpath));
	  	exportCsv.closeFile();
		Calendar end = Calendar.getInstance();
		System.out.println("start time:"+ String.format(DATETIMEFORMATTER, start));
		System.out.println("end time:" + String.format(DATETIMEFORMATTER, end));
		System.out.println("total cost time:"+ (end.getTimeInMillis() - start.getTimeInMillis())); 
		System.exit(-1);

	}

	public void exportToCsv(FileAttribute attr) {  
		exportCsv.exportToCsv(attr);  
	};

	public boolean enterRoot(String rootPath) {
		if (rootPath == null || rootPath.length() < 0)throw new IllegalArgumentException();
		 File rootFile = new File(rootPath); 
		 FileSystemView fsv = FileSystemView.getFileSystemView(); 
		 String pan=fsv.getSystemDisplayName(rootFile);
		 outfile = String.format(outfile,pan);
		 ArrayList<FileAggregate> list=new ArrayList<FileAggregate>(); 
		 return startIterate(rootFile,list); 
	}
 
	/**
	 * start iterate
	 * @param path
	 * @param list
	 */
	public boolean startIterate(File path,ArrayList<FileAggregate> list){
	    Boolean isdone=false;
		if(path.isFile()){
			FileAttribute fileAttr=new FileAttribute();
			Calendar ca= Calendar.getInstance();
		    ca.setTimeInMillis(path.lastModified());
			String fileName=path.getName();
			fileAttr.setDataTime(String.format("%1$tY-%1$te-%1$tm-%1$tT", ca));
			fileAttr.setFileName(fileName);
			fileAttr.setFilePath(path.getAbsolutePath()); 
	 
		 //	System.out.println(fileName);
			if(fileName.lastIndexOf('.')>0){
			fileAttr.setSuffix(fileName.substring(fileName.lastIndexOf('.'),fileName.length())); 
			}
			if(ignorfile.indexOf(fileAttr.getSuffix().toLowerCase())<0 ){
				exportToCsv(fileAttr);
			}else{
				//System.out.println("ignore file name:"+fileName);
			}
			isdone=true;
		}  
		if (path.isDirectory()){// if file is directory then iterate method
			File[] subFiles = path.listFiles();
			if(subFiles==null) return isdone; 
			for(File file:subFiles){
				String fileName=file.getName();  
				if(ignorfile.indexOf(fileName)>=0 ) { 
					continue;
				}   
				isdone= startIterate(file,list); 
			}
		}   
		return isdone;
	} 	

	  class FileAggregate { 
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

}
