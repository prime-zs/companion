//
// Created by sheik on 30-11-2021.
//
//#include <jni.h>

#ifndef COMPANION_AMPLITUDES_CALCULATE_H
#define COMPANION_AMPLITUDES_CALCULATE_H

#include <jni.h>
//#include <android/log.h>
#include <string>
#include <vector>
#include "error_code.h"
#include "compress_type.h"

extern "C" {
#include "libavutil/timestamp.h"
#include "libavutil/samplefmt.h"
#include "libavformat/avformat.h"
}


jobject calculate(JNIEnv *env, jobject thiz, jstring path,
                  jint compression_type, jint fps);


#endif //COMPANION_AMPLITUDES_CALCULATE_H
