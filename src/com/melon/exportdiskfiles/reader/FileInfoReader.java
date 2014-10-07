package com.melon.exportdiskfiles.reader;

/**
 * @author Read file info
 *
 */
public interface FileInfoReader {
	public static final String IGNORFILES="$RECYCLE.BIN $recycle.bin eclipse hybris springsource .png .class ";  
	public void ReadFileInfo(String filepath);
	
}
