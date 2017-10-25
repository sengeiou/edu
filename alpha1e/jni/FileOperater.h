#ifndef FileOperater_h
#define FileOperater_h

#include "stdio.h"

unsigned short File_Write(char* filePath, char* buf, unsigned long count, unsigned long* WriteCount);

unsigned short File_GetSize(char* filePath, unsigned long* FileSize);

unsigned short File_Read(char* filePath, char* buf, unsigned long count, unsigned long* ReadCount);

#endif