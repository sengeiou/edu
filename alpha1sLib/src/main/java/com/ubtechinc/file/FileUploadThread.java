package com.ubtechinc.file;

import android.util.Log;

import com.ubtechinc.base.ByteHexHelper;
import com.ubtechinc.base.ConstValue;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * [文件传输线程]
 * 
 * @author zengdengyi
 * @version 1.0
 * @date 2015年3月23日 下午5:56:55
 * 
 **/

public class FileUploadThread extends Thread {
	private String path;
	private int maxFrame;
	private FileUploader mFileUploader;
	private FileUploadProgressListener listener;
	private String saveDir;
	private int currentFrame = 1;

	/**
	 * @param mFileUploader
	 * @param listener
	 * @param path
	 *            SDCard中的文件绝对路径
	 * @param saveDir
	 *            保存到机器人的绝对路径
	 */

	public FileUploadThread(FileUploader mFileUploader,
			FileUploadProgressListener listener, String path, String saveDir) {
		this.path = path;
		this.saveDir = saveDir;
		this.mFileUploader = mFileUploader;
		this.listener = listener;
	}

	public void run() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(path);
			int nLens = 0;
			nLens = fin.available();
			maxFrame = 0;
			if (nLens % FileUploader.defFrameLen == 0) {
				maxFrame = nLens / FileUploader.defFrameLen;
			} else {
				maxFrame = nLens / FileUploader.defFrameLen + 1;
			}
			// listener.sendCMD(ConstValue.DV_FILE_UPLOAD_START,
			// packData(saveDir));
			byte[] bys = new byte[FileUploader.defFrameLen];
			int readLen;
			// 循环读取文件并发送
			while ((!mFileUploader.isExit()) && (readLen = fin.read(bys)) != -1) {
				System.out.println(readLen);
				if (currentFrame == maxFrame) {
					listener.sendCMD(ConstValue.DV_FILE_UPLOAD_END,
							packData(bys, readLen, currentFrame));
					listener.onDownloadSize(currentFrame);
					return;
				}
				listener.sendCMD(ConstValue.DV_FILE_UPLOADING,
						packData(bys, readLen, currentFrame));
				listener.onDownloadSize(currentFrame);
				currentFrame++;
				synchronized (mFileUploader) {
					try {
						mFileUploader.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			Log.i("fileload", "++++++++read end+++++++++");

		} catch (IOException e) {
			listener.UploadFail();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (fin != null){
				fin.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public byte[] packData(byte[] data, int len, int currentFrame) {

		byte[] currentFrameData = ByteHexHelper.intToTwoHexBytes(currentFrame);// 当前帧数
		byte[] data2 = new byte[len + 2];
		data2[0] = currentFrameData[1];
		data2[1] = currentFrameData[0];
		System.arraycopy(data, 0, data2, 2, len);
		return data2;
	}

	public void notityFileUploader() {
		synchronized (mFileUploader) {
			mFileUploader.notifyAll();
		}
	}
}
