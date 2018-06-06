LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)  
LOCAL_MODULE := HtsHelper
LOCAL_SRC_FILES := HTSFileArchive.c\
                   com_ubt_alpha1e_edu_business_HtsHelper.cpp\
				   FileOperater.cpp\
				   HtsFileReader.cpp
                   
LOCAL_LDLIBS+= -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)  