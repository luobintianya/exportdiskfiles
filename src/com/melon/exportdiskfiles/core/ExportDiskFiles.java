package com.melon.exportdiskfiles.core;

import java.io.File;
import java.util.Calendar;

import javax.swing.filechooser.FileSystemView;

import com.melon.exportdiskfiles.reader.FileInfoReader;
import com.melon.exportdiskfiles.reader.impl.FileBreadthReader;

/**
 * 
 * this class is used to export the disk file to a csv file. <br>
 * you can used the java -jar exportdiskfiles.jar disk path. <br>
 * the output file path is c:\\fileDB\\xxx.csv <br> 
 * 
 * </br>
 * Main method in this class.
 * @author Robin(luobintianya@sina.com)
 * 
 */
public class ExportDiskFiles { 
	private static String outfile = "c:\\fileDB\\%1$s.csv"; 
	private static final String DATETIMEFORMATTER="%1$tY-%1$tm-%1$te-%1$tT";
	public  static void main(String args[]){ 
		 System.out.println("       this tools will used to export disk files to csv files(c://fileDB/xxx.csv)");
		 System.out.println("       author:lixin wang(luobintianya@sina.com) ");
		 if (args.length != 1) {
			 System.out.println("       Usage: java -jar exportdiskfiles.jar disk(E://) ");
			 System.out.println("       which disk files will be export");  return;
		     }
		 System.out.println("       ================================================");
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
	
		ExportDiskFiles ds = new ExportDiskFiles();
		Calendar start = Calendar.getInstance(); 
		
		ds.enterRoot(rootpath,outfile); 
		Calendar end = Calendar.getInstance();
		System.out.println("start time:"+ String.format(DATETIMEFORMATTER, start));
		System.out.println("end time:" + String.format(DATETIMEFORMATTER, end));
		System.out.println("total cost time:"+ (end.getTimeInMillis() - start.getTimeInMillis())); 
		System.exit(-1);

	}
 

	public void enterRoot(String rootPath,String outpath) {
		if (rootPath == null || rootPath.length() < 0)throw new IllegalArgumentException();
		 File rootFile = new File(rootPath); 
		 FileSystemView fsv = FileSystemView.getFileSystemView(); 
		 String pan=fsv.getSystemDisplayName(rootFile);
		 outfile = String.format(outfile,pan);
		 FileInfoReader fread= new FileBreadthReader(outpath);
		 fread.ReadFileInfo(rootPath);
		 
	}
 

}
