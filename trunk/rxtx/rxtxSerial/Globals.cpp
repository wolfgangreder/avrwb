/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Globals.cpp
 * Author: wolfi
 * 
 * Created on 26. Januar 2016, 20:15
 */

#include "Globals.h"
#include "SerialPeer.h"
JNIEXPORT jint JNICALL JNI_OnLoad (JavaVM* vm, void* reserved)
{
  Globals::setJVM (vm);
  return JNI_VERSION_1_8;
}

JavaVM* Globals::vm;
jclass Globals::clazzIOException;

void Globals::setJVM (JavaVM* pvm)
{
  if (vm == NULL) {
    vm = pvm;
    JNIEnv* env;
    if (vm->GetEnv ((void**)&env, JNI_VERSION_1_8) == JNI_OK) {
      SerialPeer::init(env);
      clazzIOException = env->FindClass ("java/io/IOException");
      if (clazzIOException != NULL) {
        clazzIOException = reinterpret_cast<jclass>(env->NewWeakGlobalRef (clazzIOException));
      }
    }
  }
}

void Globals::throwJavaException (JNIEnv* env, const char* clazzName, const char* msg)
{
  jclass clazz = env->FindClass (clazzName);
  if (clazz != NULL) {
    throwException (env, clazz, msg);
    env->DeleteLocalRef (clazz);
  }
}

void Globals::throwIOException (JNIEnv* env, const char* msg)
{
  throwException (env, clazzIOException, msg);
}

void Globals::throwException (JNIEnv* env, jclass clazz, const char* msg)
{
  if (clazz != NULL) {
    env->ThrowNew (clazz, msg);
  }
}