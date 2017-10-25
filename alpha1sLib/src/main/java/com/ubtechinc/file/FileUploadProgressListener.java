package com.ubtechinc.file;

/**
 * [上传进度监听n]
 * 
 * @author zengdengyi
 * @version 1.0
 * @date 2015年3月23日 下午6:01:34
 * 
 **/

public interface FileUploadProgressListener {
	public void onDownloadSize(int size);

	public void sendCMD(byte flag, byte[] data);

	public void UploadFail();
}
