/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Globals.h
 * Author: wolfi
 *
 * Created on 26. Januar 2016, 20:15
 */

#ifndef GLOBALS_H
#define GLOBALS_H

#include <jni.h>

class Globals {
public:

  static JavaVM* getJVM() {
    return vm;
  }
  
  static void setJVM(JavaVM* vm);
  static void throwJavaException(JNIEnv* env,const char* clazzName,const char* msg);
  static void throwIOException(JNIEnv* env,const char* msg);
private:
  static JavaVM* vm;
  static jclass clazzIOException;
  
  static void throwException(JNIEnv* env,jclass clazz,const char* msg);
};

#endif /* GLOBALS_H */

