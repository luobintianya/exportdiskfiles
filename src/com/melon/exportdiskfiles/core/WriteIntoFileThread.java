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
	/**
	 * init thread and map the block of arrays confirm the file channel position
	 * @param attrs
	 * @param filechannel
	 * @param atomposition
	 * @param totalsize
	 */
	public WriteIntoFileThread(FileAttribute[] attrs,FileChannel filechannel,AtomicInteger atomposition,int totalsize){ 
	try {
		
		ReentrantLock lock =new ReentrantLock();
		lock.lock(); 
		try{  
				this.attrs = attrs;
				this.filechannel = filechannel;   
				this.mb = this.filechannel.map(MapMode.READ_WRITE, 	atomposition.get(), totalsize);
				atomposition.addAndGet(totalsize) ;   
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
		   	for(int i=0;i<attrs.length;i++){
	 			byte[] bytes=attrs[i].toString().getBytes("gbk"); 
	 			mb.put(bytes);   
	 		} 
	 		mb.force();
 	 //	} 
	}catch (Exception e) { 
		e.printStackTrace(); 
	} finally{ 
		lock.unlock();
	}  
	return true;
};
}