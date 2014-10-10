package com.melon.exportdiskfiles.reader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
	private ExportDiskFilesIntoCSV exportCsv=null; 
	private ArrayList<FileAggregate> list=new ArrayList<FileAggregate>();   
	private Queue<File> unreadfiles=new LinkedBlockingQueue<File>();    
	private ExecutorService threadPool=Executors.newFixedThreadPool(10);
	public static final int MAX_THREAD_READ_FILEFOLDERS=50;
	public FileBreadthReader(String outpath){
		exportCsv=new ExportDiskFilesIntoCSV(outpath);
	}

	public FileBreadthReader(ExportDiskFilesIntoCSV exportCsv){
		this.exportCsv=exportCsv;
	}
	
	@Override
	public void ReadFileInfo(String filepath) {  
		startReadFile(new File(filepath),list);
		exportCsv.closeFile();  
	}  
	
	 
	private void startReadFile(File path,ArrayList<FileAggregate> list){
		unreadfiles.add(path);  
		AtomicInteger deep=new AtomicInteger(0);
		Stack<Future<Boolean>> subresult=new Stack<Future<Boolean>>();
		int threadrdedfolders=0;  
		while(!unreadfiles.isEmpty()){
			File file=	unreadfiles.remove();    
			if(file.isDirectory() &&  deep.get()>10 && deep.get()<15){ 
				Future<Boolean> re	=threadPool.submit(new WatchReadSubFoldersThread(new ReadSubFilesThread(this.exportCsv,file)));
				subresult.add(re);	 
				threadrdedfolders++;
				continue;
			} 
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
					deep.addAndGet(1);
				} 
			}  
		}    
		
		for(Future<Boolean> re:subresult){
			try {
				re.get();// waiting finished 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		threadPool.shutdown();
		exportCsv.setIsdone(true);
	 }
	
	
	class WatchReadSubFoldersThread implements Callable<Boolean> {
		ReadSubFilesThread subread; 
		WatchReadSubFoldersThread(ReadSubFilesThread _subread) {
			this.subread = _subread;
		} 
		public Boolean call() throws Exception {
			this.subread.start();
			this.subread.join();
			return Boolean.TRUE;
		}
	}
	
	class ReadSubFilesThread extends Thread{ 
		private File filepath;
		private ArrayList<FileAggregate> list;
		private FileRecursionReader frr=null; 
		ReadSubFilesThread(ExportDiskFilesIntoCSV _export,File _path){ 
			this.frr=new FileRecursionReader(_export);  
			this.filepath=_path;  
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			this.frr.ReadFileInfo(this.filepath);  
			
		}
		public ArrayList<FileAggregate> getList() {
			this.list=	frr.getList();
			return list;
		}
		public void setList(ArrayList<FileAggregate> list) {
			frr.setList(list); 
		} 
	} 
}
