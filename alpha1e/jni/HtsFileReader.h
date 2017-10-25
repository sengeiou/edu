#ifndef HtsFileReader_h
#define HtsFileReader_h

#include "HTSFileArchive.h"

bool ReadFrameDataFromBuf(char* hts_buf, unsigned long hts_buf_size, FrameData** rtn, int* rtn_size);

#endif