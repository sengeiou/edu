#include "com_ubt_alpha1s_business_HtsHelper.h"
#include "HTSFileArchive.c"
#include "FileOperater.h"
#include "HtsFileReader.h"


#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C"
{
#endif

	JNIEXPORT void JNICALL Java_com_ubt_alpha1s_business_HtsHelper_writeHts
		(JNIEnv * env, jclass j_class, jobject j_object, jstring j_file_path, jobject j_listener){

		jclass NewActionInfo_class = env->FindClass("com/ubt/alpha1s/data/model/NewActionInfo");
		//��class�ļ�����Ŀ¼��ִ��javap -s -p your_class_name �鿴ǩ����Ϣ
		jmethodID getsize = env->GetMethodID(NewActionInfo_class, "getSize", "()I");
		jint frame_size = env->CallIntMethod(j_object, getsize);
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %i", frame_size);

		jfieldID NewActionInfo_frameActions_id = env->GetFieldID(NewActionInfo_class, "frameActions", "Ljava/util/List;");
		jobject NewActionInfo_frameActions = env->GetObjectField(j_object, NewActionInfo_frameActions_id);
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", "Java_com_ubt_alpha1s_business_HtsHelper_getHtsFileData-->NewActionInfo_frameActions ok");

		jmethodID get_frame_index = env->GetMethodID(NewActionInfo_class, "getFrameIndex", "(I)Lcom/ubt/alpha1s/data/model/FrameActionInfo;");
		jclass FrameActionInfo_class = env->FindClass("com/ubt/alpha1s/data/model/FrameActionInfo");
		jmethodID get_data_int = env->GetMethodID(FrameActionInfo_class, "getDataInt", "()[I");
		jfieldID eng_angles = env->GetFieldID(FrameActionInfo_class, "eng_angles", "Ljava/lang/String;");
		jfieldID eng_time = env->GetFieldID(FrameActionInfo_class, "eng_time", "I");
		jfieldID totle_time = env->GetFieldID(FrameActionInfo_class, "totle_time", "I");

		FrameData *fdata;
		int fdatalength = (int)frame_size;
		fdata = (FrameData*)malloc(fdatalength*sizeof(FrameData));

		for (int i = 0; i < fdatalength; i++){

			jobject NewActionInfo_frameActions_i;
			__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp :index -> %i", i);
			NewActionInfo_frameActions_i = env->CallObjectMethod(j_object, get_frame_index, (jint)i);
			jint eng_time_i = env->GetIntField(NewActionInfo_frameActions_i, eng_time);
			jint totle_time_i = env->GetIntField(NewActionInfo_frameActions_i, totle_time);
			__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp :eng_time-->%i", eng_time_i);

			jstring eng_angles_i = (jstring)env->GetObjectField(NewActionInfo_frameActions_i, eng_angles);
			char* eng_angles_i_array = jstringTostring(env, eng_angles_i);
			__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : %s", eng_angles_i_array);

			jintArray datas_i = (jintArray)env->CallObjectMethod(NewActionInfo_frameActions_i, get_data_int);
			jsize alen = env->GetArrayLength(datas_i);
			jint* ba = env->GetIntArrayElements(datas_i, JNI_FALSE);

			fdata[i].runtime = (int)eng_time_i;
			fdata[i].totaltime = (int)totle_time_i;
			jintArrayTointArray(fdata[i].steeringAngles, env, datas_i);

			__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : steeringAngles --> %i", fdata[i].steeringAngles[15]);

		}

		char **buf = (char**)malloc(sizeof(char*));
		int *bufsize = (int*)malloc(sizeof(int));
		CreateHtsFileBuffer(buf, bufsize, fdata, fdatalength);

		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : steeringAngles --> %i", *bufsize);
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : buf --> %i", **buf);

		unsigned long *finish_size = (unsigned long*)malloc(sizeof(unsigned long));
		char* file_path = jstringTostring(env, j_file_path);

		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "write to %s", file_path);

		unsigned long size = File_Write(file_path, *buf, *bufsize, finish_size);
		free(*buf);

		jclass IHtsHelperListener_class = env->FindClass("com/ubt/alpha1s/business/IHtsHelperListener");
		jmethodID on_hts_write_finish = env->GetMethodID(IHtsHelperListener_class, "onHtsWriteFinish", "(Z)V");

		if (size == *bufsize){
			//success
			env->CallVoidMethod(j_listener, on_hts_write_finish, true);
		}
		else{
			env->CallVoidMethod(j_listener, on_hts_write_finish, false);
		}
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : finish");
	}

	JNIEXPORT void JNICALL Java_com_ubt_alpha1s_business_HtsHelper_getNewActionInfoFromHts
		(JNIEnv * env, jclass j_class, jobject j_object, jstring j_file_path, jobject j_listener)
	{

		unsigned long *file_size = (unsigned long*)malloc(sizeof(unsigned long));
		char* file_path = jstringTostring(env, j_file_path);
		File_GetSize(file_path, file_size);
		//__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : file_size is %i", *file_size);

		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : reading from %s", file_path);
		unsigned long *finish_size = (unsigned long*)malloc(sizeof(unsigned long));
		char* file_content = (char*)malloc(sizeof(char)*(*file_size));

		//modify by lihai on 2016/07/27 for file is not exist return false
		unsigned short file_strean = File_Read(file_path, file_content, *file_size, finish_size);
        if(file_strean == 100){
                jclass IHtsHelperListener_class = env->FindClass("com/ubt/alpha1s/business/IHtsHelperListener");
                jmethodID on_get_new_action_info_finish = env->GetMethodID(IHtsHelperListener_class, "onGetNewActionInfoFinish", "(Z)V");
                env->CallVoidMethod(j_listener, on_get_new_action_info_finish, false);
        }else{
                __android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : buf --> %i", *file_content);

        		//__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : convert size --> %i", *finish_size);
        		FrameData** rtn = (FrameData**)malloc(sizeof(FrameData*));
        		int* size = (int*)malloc(sizeof(int));
        		ReadFrameDataFromBuf(file_content, *finish_size, rtn, size);
        		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : first r=%i", (*rtn[0]).steeringAngles[0]);

        		FrameDatasToNewActionInfo(env, j_object, *rtn, *size);

        		jclass IHtsHelperListener_class = env->FindClass("com/ubt/alpha1s/business/IHtsHelperListener");
        		jmethodID on_get_new_action_info_finish = env->GetMethodID(IHtsHelperListener_class, "onGetNewActionInfoFinish", "(Z)V");
        		env->CallVoidMethod(j_listener, on_get_new_action_info_finish, true);
        }
		__android_log_print(ANDROID_LOG_INFO, "yuyong----------", "log from cpp : finish");
	}

	char* jstringTostring(JNIEnv* env, jstring jstr)
	{
		char* rtn = NULL;
		jclass clsstring = env->FindClass("java/lang/String");
		jstring strencode = env->NewStringUTF("utf-8");
		jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
		jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
		jsize alen = env->GetArrayLength(barr);
		jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
		if (alen > 0)
		{
			rtn = (char*)malloc(sizeof(char)*(alen + 1));
			memcpy(rtn, ba, alen);
			rtn[alen] = 0;
		}
		env->ReleaseByteArrayElements(barr, ba, 0);
		return rtn;
	}

	void jintArrayTointArray(int* result, JNIEnv* env, jintArray jintarray){
		jint* arr;
		jint length;
		arr = env->GetIntArrayElements(jintarray, JNI_FALSE);
		length = env->GetArrayLength(jintarray);
		for (int i = 0; i < length; i++){
			result[i] = (int)arr[i];
		}
		env->ReleaseIntArrayElements(jintarray, arr, 0);
	}

	void FrameDatasToNewActionInfo(JNIEnv* env, jobject j_object, FrameData* datas, int data_size){
		jclass NewActionInfo_class = env->FindClass("com/ubt/alpha1s/data/model/NewActionInfo");
		jmethodID addFrame = env->GetMethodID(NewActionInfo_class, "addFrame", "([I)V");
		for (int i = 0; i < data_size; i++){
			int i_datas[18];
			i_datas[0] = datas[i].runtime;
			i_datas[1] = datas[i].totaltime;
			for (int j = 2; j < 18; j++){
				i_datas[j] = datas[i].steeringAngles[j - 2];
			}
			env->CallVoidMethod(j_object, addFrame, intsTojintArray(env, i_datas, 18));
		}
	}

	jintArray intsTojintArray(JNIEnv* env, int* int_s, int size){

		jintArray jint_array = env->NewIntArray(size);
		jint* jint_s = (jint*)malloc(sizeof(jint)*size);
		for (int i = 0; i < size; i++){
			jint_s[i] = (jint)int_s[i];
		}
		env->SetIntArrayRegion(jint_array, 0, size, jint_s);
		return jint_array;
	}

#ifdef __cplusplus
}
#endif
