#include <jni.h>
#include <string>
#include "amplitudes/amplitudes_calculate.h"


extern "C"
JNIEXPORT jobject JNICALL
Java_com_primex_toolkit_Toolkit_amplitudesFromJNI(JNIEnv *env, jobject thiz, jstring path,
                                                  jint compression_type, jint fps) {
    return calculate(env, thiz, path, compression_type, fps);
}