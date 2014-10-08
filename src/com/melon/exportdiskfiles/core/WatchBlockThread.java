package com.melon.exportdiskfiles.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
//	private Lock lock = new ReentrantLock();  
	//private Condition isfull;
	public WatchBlockThread(int maxBlock,LinkedBlockingQueue<FileAttribute> attrs,File outfile){
		this.maxBlock=maxBlock;
		this.attrs=attrs; 
		try {
			this.outputfile = new RandomAccessFile(outfile,"rw");
			this.filechannel=outputfile.getChannel();   
			//this.lock=lock;
			//this.isfull=isfull;
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} 
	}
	
	
	@Override
	public void run() { 
		while(true){  // do until system exit 
			if (this.isdone.get()==false && attrs.size() < this.maxBlock ) { 
				try {
					synchronized (this) { 
						this.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*lock.lock();
				try { 
					isfull.await(); 
				
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}finally{
					lock.unlock();
				}*/
			} 
	
		
			if(attrs!= null && attrs.size()!=0){ //if out class notifiedAll then execute this step  
				ArrayList<FileAttribute> temp=new ArrayList<FileAttribute>(); 
				attrs.drainTo(temp,this.maxBlock);   //remove some elements to temp list   
				final FileAttribute[] attrarrays= new FileAttribute[temp.size()];
				temp.toArray(attrarrays);  
				totalrecords+=temp.size();    
				int	totalsize=calculateArraySize(attrarrays);   
				WriteIntoFileThread ww= new WriteIntoFileThread(attrarrays,filechannel,position,totalsize);  
				threadPool.submit(ww);
				if (this.isdone.get() == true && attrs.size() ==0) {
					System.out.println("total files:" + totalrecords);  
					threadPool.shutdown();// thread pools close
					break;// break the loop 
				}
			}
		 
			
		
			
			/*if(this.isdone.get()==true && this.maxBlock>attrs.size()){  
				System.out.println("read files into queue is done, waiting process by work thread");
			} */
			

			
		}
	  
		 
	}
	public  synchronized int calculateArraySize(FileAttribute[] attrs){
		//AtomicInteger count=new AtomicInteger();  
		int count=0;
		//int i = 0;
		//System.out.println("calculateArraySize start线程");
		for(FileAttribute attr:attrs){
			 count+=attr.getSize();
			//count.addAndGet(attr.getSize());
			//i++;
		//	System.out.println(i+"calculateArraySize "+attr.getSize());
		} 
		//System.out.println("calculateArraySize end线程"+i);
		return count;
		//count.get();
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
