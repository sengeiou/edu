#include "FileOperater.h"
#include <android/log.h>
#include <sys/stat.h>
/**
* File_Write--��buf��д���ļ�
* count--д��ĳ���
* WriteCount--������д��ĳ���
*/
unsigned short File_Write(FILE * FileHandle, char* buf, unsigned long count, unsigned long* WriteCount)
{


	if (FileHandle == NULL) {
		return 100;
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "FileHandle == NULL");
	}
	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "FileHandle != NULL");
	unsigned short write_result = fwrite(buf, sizeof(char), count, FileHandle); // ����ֵ�ǳɹ�д�����Ŀ��
	fclose(FileHandle);
	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : write_result=%i", write_result);
	if (write_result == 1) {
		*WriteCount = write_result * count;

	}
	return write_result;
}

unsigned short File_Write(char* filePath, char* buf, unsigned long count, unsigned long* WriteCount)
{

	FILE* stream = fopen(filePath, "w");
	return File_Write(stream, buf, count, WriteCount);
}

/**
* File_Read--��ȡ�ļ���buf
* count--��ȡ�ĳ���
* ReadCount--�����Ѷ�ȡ�ĳ���
*/
unsigned short File_Read(FILE * FileHandle, char* buf, unsigned long count, unsigned long* ReadCount) {
	if (FileHandle == NULL) {
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : FileHandle is null");
		return 100;
	}
	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : FileHandle not null");
	*ReadCount = fread(buf, 1, count, FileHandle);
	fclose(FileHandle);
	return 0;
}

unsigned short File_Read(char* filePath, char* buf, unsigned long count, unsigned long* ReadCount){
	FILE* stream = fopen(filePath, "rt+");
	if(stream == NULL){
	    //stream = fopen(filePath, "wt+");
	    //mkdir(filePath,mode_t(0x777));
	}
	return File_Read(stream, buf, count, ReadCount);
}

/**
* File_GetSize--�õ��ļ�����
* FileSize--�����ļ�����
*/
unsigned short File_GetSize(FILE* FileHandle, unsigned long* FileSize) {
	if (FileHandle == NULL) {
		return 100;
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : FileHandle is null");
	}
	__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : FileHandle is not null");
	fseek(FileHandle, 0L, SEEK_END);
	*FileSize = ftell(FileHandle);
	fclose(FileHandle);
	return 0;
}

unsigned short File_GetSize(char* filePath, unsigned long* FileSize) {

	FILE* stream = fopen(filePath, "rt+");
	return File_GetSize(stream, FileSize);

}

