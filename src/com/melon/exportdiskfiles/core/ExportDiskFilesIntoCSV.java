package com.melon.exportdiskfiles.core;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this util class used to export file to csv
 * 
 * @author Robin
 *
 */
public class ExportDiskFilesIntoCSV { 
	private LinkedBlockingQueue<FileAttribute> bq = new LinkedBlockingQueue<FileAttribute>();
	private final int MAXBATCH =100000;
	public final static String HEADLINE ="filename,suffix,filepath,datatime\n";
	private WatchBlockThread obthread = null; 
	//private Lock lock = new ReentrantLock();
	//private Condition isfull= lock.newCondition();
	public ExportDiskFilesIntoCSV() { 
	} 
	public  void initialize(String path){
		File outfile=new File(path); 
		try {
				if(!outfile.exists()) {outfile.getParentFile().mkdirs(); } 
				RandomAccessFile	outputfile = new RandomAccessFile(outfile,"rw");//write the first row
				FileChannel	filechannel=outputfile.getChannel();     
			 	ByteBuffer bytebuffer=ByteBuffer.wrap(HEADLINE.getBytes());
			 	filechannel.write(bytebuffer);
			 	filechannel.close();
			 	outputfile.close();  
			 	//obthread=new WatchBlockThread(MAXBATCH, bq,outfile,lock,isfull);
			 	obthread=new WatchBlockThread(MAXBATCH, bq,outfile);
				obthread.start(); 
				
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exportToCsv(FileAttribute attr) {    
		try {  
			//System.out.println(Thread.currentThread().getId()+" 放入文件"+attr.toString()); 
			bq.put(attr);  
			//lock.lock(); 
			if(bq.size()>MAXBATCH){    
				synchronized (obthread) {
					obthread.notifyAll();
				}
			} 
			 
			/*try{
			
				if(bq.size()>MAXBATCH){   
					//System.out.println("wo tongzhi");
					isfull.signalAll();
					
					synchronized (obthread) {
						obthread.notifyAll();
					}
				} 
			}finally{
				lock.unlock();
			} */
		
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		}  
		 
	}  
		
	
	public void setIsdone(boolean newValue) {
		obthread.setIsdone(true); 
		try { 
			synchronized (obthread) { 
				obthread.notifyAll();	//notifyAll waiting thread, to process not reach the max block   
				obthread.join();// waiting the write into csv finished. 
			}  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		/*lock.lock();
		try {
			isfull.signalAll();
		} finally {
			lock.unlock();
		} */
	}

	public void closeFile(){ 
		/*lock.lock();
		try {
			isfull.signalAll();
		} finally {
			lock.unlock();
		}*/
		/*	try { 
			synchronized (obthread) { 
				obthread.notifyAll();	//notifyAll waiting thread, to process not reach the max block 
			}  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
	}

	/**
	 * used to cac
	 * @param attrs
	 * @return
	 */
	public synchronized static int arraySize(FileAttribute[] attrs) { 
		int count = 0; 
		for (FileAttribute attr : attrs) {
			count += attr.getSize();
		} 
		return count;
	}
	
}
