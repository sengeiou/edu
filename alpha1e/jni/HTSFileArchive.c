//
//  HTSFileArchive.c
//  Alpha1S_NewInteractionDesign
//
//  Created by Glen on 16/5/4.
//  Copyright © 2016年 Ubtechinc. All rights reserved.
//

#include "HTSFileArchive.h"
#include <android/log.h>
#include <jni.h>


unsigned char * GetHtsFrameData(int nFrameType, int nTotalFrame, int nCurFrame, int* pMoveData, int nMotorDataLen, int* pRemain)
{
	static unsigned char m_byte[33] = {
		0xFB, 0xBF, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x5A, 0x5A, 0x5A,
		0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A,
		0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x5A, 0x00, 0x00, 0x00, 0x00, 0xED };

	m_byte[3] = nFrameType; //帧状态位

	if (nTotalFrame < 256)
	{
		m_byte[5] = 0x00;
		m_byte[4] = nTotalFrame;    //总帧（低位在前，高位在后）
	}
	else
	{
		m_byte[5] = nTotalFrame / 256;
		m_byte[4] = nTotalFrame % 256;
	}

	if (nCurFrame < 256)
	{
		m_byte[7] = 0x00;
		m_byte[6] = nCurFrame;      //当前帧
	}
	else
	{
		m_byte[7] = nCurFrame / 256;
		m_byte[6] = nCurFrame % 256;
	}

	int i;

	for (i = 0; i < nMotorDataLen - 2; i++)  //装载20个舵机数据
	{
		m_byte[8 + i] = pMoveData[i];
	}

	m_byte[28] = pMoveData[nMotorDataLen - 2] / 20;   //运行时间

	//-----------------------------------------
	if (pMoveData[nMotorDataLen - 1] >= 60)
	{
		pMoveData[nMotorDataLen - 1] = pMoveData[nMotorDataLen - 1] - 40;
	}
	//-----------------------------------------

	// 总时间
	if ((pMoveData[nMotorDataLen - 1] / 20) < 256)
	{
		m_byte[29] = 0;
		m_byte[30] = pMoveData[nMotorDataLen - 1] / 20;
		pRemain[0] += pMoveData[nMotorDataLen - 1] % 20;
		if (pRemain[0] >= 20)
		{
			pRemain[0] = pRemain[0] - 20;
			m_byte[30] = m_byte[30] + 1;
		}

		if (m_byte[30] == 0xff)
		{
			m_byte[29] = m_byte[29] + 1;
			m_byte[30] = 0x00;
		}
		else
		{
			if (m_byte[30] == 0x00)
			{
				m_byte[30] = 1;
			}
		}
	}
	else // 暂时
	{
		m_byte[29] = (pMoveData[nMotorDataLen - 1] / 20) / 256;
		m_byte[30] = (pMoveData[nMotorDataLen - 1] / 20) % 256;

		pRemain[1] += pMoveData[nMotorDataLen - 1] % 20;
		if (pRemain[1] >= 20)
		{
			pRemain[1] = pRemain[1] - 20;
			m_byte[30] = m_byte[30] + 1;
		}

		if (m_byte[30] == 0xff)
		{
			m_byte[29] = m_byte[29] + 1;
			m_byte[30] = 0x00;
		}
		else
		{
			if (m_byte[30] == 0x00)
			{
				m_byte[30] = 1;
			}
		}
	}

	// 校验位
	char n_byte[1] = { 0 };
	for (i = 2; i < 31; i++)
	{
		n_byte[0] += m_byte[i];
	}
	m_byte[31] = n_byte[0];

	return m_byte;
}


bool CreateHtsFileBuffer(char** buf, int *bufsize, FrameData *fdata, unsigned int fdatalength) {

	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "CreateHtsFileBuffer start");

	int nFrameCount = fdatalength; //帧数
	if (nFrameCount > 65535) //最大帧数
	{
		return false;
	}

	bool bRet = false;
	const int nHtsFrameSize = 33;//hts文件每帧的长度
	const int nHtsFileSize = nHtsFrameSize * (2 + nFrameCount); //帧长度＋前后各33个字节
	char* rtn = (char *)malloc(nHtsFileSize*sizeof(char));

	memset(rtn, 0x00, nHtsFileSize);    //清空
	char *pDataCursor = rtn + nHtsFrameSize;    //数据游标（偏移33个字节）

	int nAllTime = 0;//总时间
	int aRemain[2] = { 0 };//动作补偿
	int nCurFrame = 1;
	int i;

	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "CreateHtsFileBuffer for start");

	for (i = 0; i < fdatalength; i++)
	{
		FrameData frame = *(fdata + i);
		nAllTime += frame.totaltime;

		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : nAllTime = %i", nAllTime);

		int nFrameType = 1;//帧类型 1--首帧, 2--中间帧, 3--尾帧
		if (nCurFrame == 1)
		{
			if (1 == nFrameCount)
			{
				nFrameType = 3;
			}
			else
			{
				nFrameType = 1;
			}
		}
		else if (nCurFrame == nFrameCount)
		{
			nFrameType = 1;
		}
		else
		{
			nFrameType = 2;
		}

		const int nMotorDataLen = 22; //舵机数据长度，舵机数量加运行时间和总时间
		int movedata[22] = { 0 };
		int j;
		for (j = 0; j < nMotorDataLen - 2; j++)
		{
			movedata[j] = 90;
		}
		int k;
		for (k = 0; k < sizeof(frame.steeringAngles) / sizeof(int) && k < nMotorDataLen; k++)
		{
			movedata[k] = frame.steeringAngles[k];
		}
		movedata[nMotorDataLen - 2] = frame.runtime;//运行时间
		movedata[nMotorDataLen - 1] = frame.totaltime;//总时间

		memcpy(pDataCursor, GetHtsFrameData(nFrameType, nFrameCount, nCurFrame, movedata, nMotorDataLen, aRemain), nHtsFrameSize);
		pDataCursor += nHtsFrameSize;
		nCurFrame++;

		bRet = true;
	}

	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "CreateHtsFileBuffer for end");

	if (nAllTime < 256)
	{
		pDataCursor[29] = nAllTime;
	}
	else if (nAllTime > 255 && nAllTime < 65536)
	{
		pDataCursor[30] = nAllTime / 256;
		pDataCursor[29] = nAllTime % 256;
	}
	else if (nAllTime > 65535)
	{
		pDataCursor[31] = nAllTime / 65536;
		pDataCursor[30] = (nAllTime - 65536) / 256;
		pDataCursor[29] = (nAllTime - 65536) % 256;
	}

	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "CreateHtsFileBuffer end");

	*buf = rtn;
	*bufsize = nHtsFileSize;

	return bRet;
}










