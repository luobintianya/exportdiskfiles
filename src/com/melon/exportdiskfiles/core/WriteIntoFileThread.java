package com.melon.exportdiskfiles.core;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * the write into csv file thread
 * @author Robin
 *
 */
class WriteIntoFileThread implements Callable<Boolean>{
	private	MappedByteBuffer mb;
	private FileAttribute[] attrs = null ;  
	private FileChannel filechannel;  
	public WriteIntoFileThread(FileAttribute[] attrs,FileChannel filechannel,AtomicInteger position,int totalsize){ 
	try {
		ReentrantLock lock =new ReentrantLock();
		lock.lock(); 
		try{  
				this.attrs = attrs;
				this.filechannel = filechannel;
				System.out.println(position.get());
			 
				this.mb = this.filechannel.map(MapMode.READ_WRITE, 	position.get(), totalsize);
			    position.addAndGet(totalsize) ; 
				//position.addAndGet(totalsize);
				System.out.println(totalsize); 
		}finally{
			lock.unlock();
		}
	} catch (Exception e) {
		e.getStackTrace();
	}  
}
 

	
	
@Override
public Boolean call() throws Exception {
	// TODO Auto-generated method stub
	ReentrantLock lock =new ReentrantLock();
	lock.lock(); 
	try{   
 	 // synchronized (mb) { 
		  // System.out.println(Thread.currentThread().getId()+attrs[0].toString()+"要写入的大小"+attrs.length); 
	 		for(int i=0;i<attrs.length;i++){  
	 		    System.out.println(Thread.currentThread().getId()+" 写入"+this.filechannel.isOpen()+"第 "+i+" :"+attrs[i].toString());  
	 			//System.out.println(Thread.currentThread().getId()+" OK "+i+" :"+attrs[i].toString());    
	 			//System.out.println("position"+mb.position()); 
	 			mb.put(attrs[i].toString().getBytes("gbk")); 
	 			mb.force();//must put here if not will have BufferOverflowException
	 		} 
 	 //	} 
	}catch (Exception e) { 
		e.printStackTrace(); 
	} finally{ 
		lock.unlock();
	}
	
	return true;
};
}