/*
 *
 *  *
 *  *  *
 *  *  * Copyright (c) 2008-2017 UBT Corporation.  All rights reserved.  Redistribution,
 *  *  *  modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *  *  *
 *  *
 *
 */

package com.ubt.factorytest.bluetooth.ubtbtprotocol;

import com.ubt.factorytest.utils.ByteHexHelper;

import java.nio.ByteBuffer;


/**
 * @desc : 蓝牙通讯协议,格式：
 *         1B   HEADER1  头1
 *         1B   HEADER2  头2
 *         1B   LENGTH   长: 不包含结束码
 *         1B   CMD       命令码
 *         ?B   PARAM     参数
 *         1B   CHECKSUM  校验码：计算校验码时，不包含字头和结束码
 *         1B   ENDCODE   结束码 0XED
 *
  */
public class UbtBTProtocol {
    private static final String TAG = "UbtBTProtocol";

    private static byte header1 = (byte) 0xFB;
    private static byte header2 = (byte) 0xBF;
    private static byte end = (byte) 0xED;
    /**
     * 包长度计算：字头(2B) + 长度(1B)  + CMD(1B)
     *           +校验码(1B)
     *           +(参数长度==0? 1: 参数长度)
     *
     *  不包含结束码
     */
    private byte mLength;
    /**
     * 命令码
     */
    private byte mCmd;
    /**
     * 校验码：计算校验码时，不包含字头、校验码和结束码
     */
    private byte mCheckSum = -1;
    /**
     * 参数
     */
    private byte[] mParam;

    /**
     *  传入一个完成的串口协议包数据
     *
     * @param in 一个完整的有效的串口协议包
     * @throws InvalidPacketException
     */
    public UbtBTProtocol(byte[] in) throws InvalidPacketException {
        if (getBytes(in) != null) {
            if (checkBytes(in)) {
                parseData(in);
                return;
            }
            throw new InvalidPacketException("蓝牙数据包错误");
        }
    }

    public UbtBTProtocol(byte cmd, byte[] param){
        this.mCmd = cmd;
        this.mParam = param;
        this.mLength = calPackectLength(param);
        //checkSum未计算
    }

    private ByteBuffer toByteBuffer(){
        //字头(2B) 长度(1B)  命令(1B)
        short totalLen = (short)(2 +  1 + 1);
        if (mParam.length > 0){
            totalLen += mParam.length;
        }else {
            totalLen += 1;
        }
        ByteBuffer bb = ByteBuffer.allocate(totalLen);//可写
        bb.put(header1);
        bb.put(header2);
        bb.put((byte) (totalLen + 1));//包含校验码
        bb.put(mCmd);
        if (mParam.length == 0){
            bb.put((byte) 0);
        }else {
            bb.put(mParam);
        }

        bb.rewind();//标记为可读
        byte[] bytes = new byte[bb.remaining()];
        bb.get(bytes);//数据读完

        ByteBuffer newBB = ByteBuffer.allocate(bytes.length + 2);//可写
        newBB.limit(bytes.length + 2);
        newBB.put(bytes);
        newBB.put(generateCheckSum(bytes, 2, bytes.length));
        newBB.put(end);
        newBB.rewind();//标记为可读
        return newBB;
    }


    public byte getCmd() {
        return mCmd;
    }

    public byte[] getParam() {
        return mParam;
    }


    private static byte calPackectLength(byte[] param){
        byte totalLen = (byte) (2 + 1 + 1 + 1 + 1);
        if (param.length > 0){
            totalLen += param.length;
        }else {
            totalLen += 1;
        }
        return (byte) (totalLen - 1);
    }

    public byte[] toRawBytes(){
        ByteBuffer bb = toByteBuffer();
        byte[] bytes = new byte[bb.remaining()];
        bb.get(bytes);
        return bytes;
    }

    private void parseData(byte[] in) {
        ByteBuffer buffer = ByteBuffer.allocate(in.length);
        buffer.put(in);
        buffer.rewind();

        buffer.get();
        buffer.get();//字头
        mLength = buffer.get();//包长
        mCmd = buffer.get();//命令码
        int paramLength = mLength - 5;
        if (paramLength > 0) {
            mParam = new byte[paramLength];
            buffer.get(mParam);
        }else {
            mParam = new byte[0];
        }

        mCheckSum = buffer.get();//校验码
        buffer.get();//结束码
    }

    private static boolean checkBytes(byte[] in) {
//        Timber.d("head1=%s,head2=%s,(length:%d,%d),(checksum=%d,%d),(end=%s)", ConvertUtils.byte2HexString(in[0]),
//                ConvertUtils.byte2HexString(in[1]),in[2],in.length-1,
//                in[in.length-2],generateCheckSum(in, 2, in.length - 2),
//                ConvertUtils.byte2HexString(in[in.length-1]));
        return in[0] == header1 &&
                in[1] == header2 &&
                in[2] == (in.length - 1) &&
                in[in.length - 2] == generateCheckSum(in, 2, in.length - 2) &&
                in[in.length - 1] == end;
    }

    private static byte generateCheckSum(byte[] in, int start, int end) {
        byte checkSum = 0;
        //校验码计算：不包含结束码和校验码本身
        for (int i = start; i < end; i++) {
            checkSum += in[i];
        }
        return checkSum;
    }

    private byte[] getBytes(byte[] in){
        byte[] data = null;
        if(in.length <= 0){
            return null;
        }
        try {
            if (in[2] == (in.length - 1)) {
                data = in;
            } else if (in[2] < (in.length - 1)) {
                int inLen = in[2];
                data = new byte[inLen+1];
                System.arraycopy(in, 0, data, 0, inLen + 1);
                in[inLen] = 0;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public String toString() {
        return String.format("Packet:[cmd=%02X, raw=%s]",mCmd,  ByteHexHelper.bytesToHexString(toRawBytes()));
    }
}
