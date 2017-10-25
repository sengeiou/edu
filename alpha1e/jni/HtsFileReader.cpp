#include "HtsFileReader.h"
#include "HTSFileArchive.h"
#include <android/log.h>

bool ReadFrameDataFromBuf(char* hts_buf, unsigned long hts_buf_size, FrameData** rtn, int* rtn_size)
{
	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : ReadFrameDataFromBuf start");

	const int size = (hts_buf_size - 66) / 33;

	if (size <= 0)
		return false;

	*rtn = (FrameData*)malloc(sizeof(FrameData)*size);

	int start = 0;
	for (int i = 0; i < size; i++){
		start += 33;
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "start=%i", start);
		int r_start = start + 8;
		for (int j = 0; j < 16; j++){
			(*rtn)[i].steeringIdentifiers[j] = j + 1;
			(*rtn)[i].steeringAngles[j] = (unsigned char)hts_buf[r_start + j];
		}
		(*rtn)[i].runtime = hts_buf[start + 28] * 20;
		(*rtn)[i].totaltime = ((hts_buf[start + 29] << 8) | hts_buf[start + 30]) * 20;
	}

	*rtn_size = size;
	return true;
}