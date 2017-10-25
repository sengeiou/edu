package com.ubtechinc.file;

import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodSubtype;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class FileSendWithSocket extends Thread{

    private static final String TAG = FileSendWithSocket.class.getSimpleName();
    private static final String SEND_SUCCESS= "send_success";
    private static final String SEND_FAILED = "send_failed";

    private String fileName;  //文件名
    private String path;      //机器人内部的保存路径
    private String ipAddress; //机器人IP地址
    private int port;         //socket服务端口
    private SendListener sendListener;





    public FileSendWithSocket(String fileName, String path, String ipAddress, int port, SendListener sendListener) {
        this.fileName = fileName;
        this.path = path;
        this.ipAddress = ipAddress;
        this.port = port;
        this.sendListener = sendListener;
    }


    @Override
    public void run() {
        String tag = sendFile(fileName, path, ipAddress, port);
        if(tag.equals(SEND_SUCCESS)) {
            sendListener.sendSuccessed();
        }else{
            sendListener.sendFailed();
        }
    }





    //作为client端发送文件
    public String sendFile(String fileName, String path, String ipAddress, int port) {
        try {

            //发送文件数据
            Log.d(TAG, "connect socket");
            Socket data = new Socket(ipAddress, port);
            //创建输出流
            OutputStream outputData = data.getOutputStream();
            Log.d(TAG, "send write");
            //从文件系统中的某个文件中获得输入字节
            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024*10];
            while ((size = fileInput.read(buffer, 0, 1024*10)) != -1) {
                Log.d(TAG, "size:" + size);
                outputData.write(buffer, 0, size);
                outputData.flush();
            }

            outputData.close();
            fileInput.close();
            data.close();
            return SEND_SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
            return SEND_FAILED;
        }
    }


    public interface SendListener{
        void sendSuccessed();
        void sendFailed();

    }








}
