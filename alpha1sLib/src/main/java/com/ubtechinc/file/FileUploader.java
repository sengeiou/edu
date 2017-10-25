
package com.ubtechinc.file;

/**
 * [文件上传器]
 * 
 * @author zengdengyi
 * @version 1.0
 * @date 2015年3月23日 下午6:01:50
 * 
 **/

public class FileUploader {
	public final static int defFrameLen = 245;

	private FileUploadThread thread;
	/* 停止下载 */
	private boolean exit;

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}

	public FileUploader(FileUploadProgressListener listener, String path,
			String saveDir) {
		thread = new FileUploadThread(this, listener, path, saveDir);
	}

	public void download() {
		thread.start();
	}

	public void exit() {
		this.setExit(true);
		thread.interrupt();
	}

	public void notityThread() {
		thread.notityFileUploader();
	}
}

