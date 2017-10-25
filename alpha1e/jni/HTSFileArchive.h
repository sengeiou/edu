//
//  HTSFileArchive.h
//  Alpha1S_NewInteractionDesign
//
//  Created by Glen on 16/5/4.
//  Copyright © 2016年 Ubtechinc. All rights reserved.
//

#ifndef HTSFileArchive_h
#define HTSFileArchive_h

#include <stdio.h>
#include <stdbool.h>
#include "string.h"
#include <stdlib.h>

//帧数据
typedef struct {
	int steeringAngles[16];         //16个舵机角度
	int steeringIdentifiers[16];    //16个舵机id
	float runtime;
	float totaltime;
}FrameData;


bool CreateHtsFileBuffer(char **buf, int *bufsize, FrameData *fdata, unsigned int fdatalength);


/**
 *  创建帧数据
 *
 */
unsigned char * GetHtsFrameData(int nFrameType, int nTotalFrame, int nCurFrame, int* pMoveData, int nMotorDataLen, int* pRemain);




#endif /* HTSFileArchive_h */
