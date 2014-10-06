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

/**
 * this thread is used to put all read files into a thread safe queue
 * and drain it into temp list let a write into csv thread to process.
 * @author Robin
 *
 */
public class WatchBlockThread extends Thread{  
	private int maxBlock;
	private FileChannel filechannel ; 
	private RandomAccessFile outputfile = null;
	private LinkedBlockingQueue<FileAttribute> attrs; 
	private int totalrecords=0;
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
			e.printStackTrace();
		} 
	}
	
	
	@Override
	public void run() { 
		while(true){  // do until system exit
			
			if (attrs.size() < this.maxBlock && this.isdone.get()==false) { 
				try {
					synchronized (this) {  
						this.wait();
					}
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
			}
			if(attrs!= null && attrs.size() !=0){ //if out class notifiedAll then execute this step
				ArrayList<FileAttribute> temp=new ArrayList<FileAttribute>(); 
				WriteIntoFileThread ww=null;  
				attrs.drainTo(temp,this.maxBlock);   //remove some elements to temp list  
				final FileAttribute[] attrarrays= new FileAttribute[temp.size()];
				temp.toArray(attrarrays);  
				totalrecords+=temp.size();  
				if (this.isdone.get() == true && attrs.size() ==0) {
					System.out.println("total files:" + totalrecords);
				}
				int	totalsize=calculateArraySize(attrarrays);  
			    ww= new WriteIntoFileThread(attrarrays,filechannel,position,totalsize);  
				threadPool.submit(ww);  
			} 
			
			/*if(this.isdone.get()==true && this.maxBlock>attrs.size()){  
				System.out.println("read files into queue is done, waiting process by work thread");
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
		System.out.println("total process size "+position.get());
	}
}
