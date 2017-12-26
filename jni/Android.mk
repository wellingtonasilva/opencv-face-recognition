LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE=STATIC

include /opt/opencv2.4.1/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := face_recognition
LOCAL_SRC_FILES := face_recognition.cpp
LOCAL_LDLIBS    +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
