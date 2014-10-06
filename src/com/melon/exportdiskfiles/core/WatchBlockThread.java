package com.melon.exportdiskfiles.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WatchBlockThread extends Thread{ 
	
	private int maxBlock;
	private FileChannel filechannel ; 
	private RandomAccessFile outputfile = null;
	private LinkedBlockingQueue<FileAttribute> attrs; 
	private AtomicInteger position =new AtomicInteger(ExportDiskFilesIntoCSV.HEADLINE.getBytes().length); 
	private ExecutorService threadPool=Executors.newFixedThreadPool(8);
	private AtomicBoolean isdone=new AtomicBoolean(false);
	public WatchBlockThread(int maxBlock,LinkedBlockingQueue<FileAttribute> attrs,File outfile){
		this.maxBlock=maxBlock;
		this.attrs=attrs; 
		try {
			outputfile = new RandomAccessFile(outfile,"rw");
			filechannel=outputfile.getChannel();   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	@Override
	public void run() { 
		while(true){  
			if(attrs!= null && attrs.size() !=0){ //if out class notifiedAll then execute this step
				ArrayList<FileAttribute> temp=new ArrayList<FileAttribute>(); 
				WriteIntoFileThread ww=null;  
				attrs.drainTo(temp,this.maxBlock);  
				//System.out.println("吃掉"+temp.size());;
				//System.out.println("剩余" +attrs.size());
				final FileAttribute[] attrarrays= new FileAttribute[temp.size()];
				temp.toArray(attrarrays);  
				int	totalsize=calculateArraySize(attrarrays);  
			    ww= new WriteIntoFileThread(attrarrays,filechannel,position,totalsize);  
				threadPool.submit(ww);  
				
			} 
		/*	if(this.isdone.equals(false) && this.maxBlock>attrs.size()){  
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} */
		}
	  
		 
	}
	public static int calculateArraySize(FileAttribute[] attrs){
		int count=0; 
		for(FileAttribute attr:attrs){
			count+=attr.getSize();
		} 
		return count;
	}
	
	
	public void setIsdone(boolean newValue) {
		this.isdone.set(newValue);
	}

	@Override
	public void destroy() { 
		super.destroy();
		System.out.println("totally process size "+position.get());
	}
}
