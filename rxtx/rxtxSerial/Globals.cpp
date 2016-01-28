/*
 * $Id$
 *
 * Copyright (C) 2016 Wolfgang Reder <wolfgang.reder@aon.at>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */

#include "Globals.h"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
  Globals::setJVM(vm);
  return JNI_VERSION_1_8;
}

JavaVM* Globals::vm;
jclass Globals::clazzIOException;
jclass Globals::classAwbSerialPort;

void Globals::setJVM(JavaVM* pvm)
{
  if (vm == NULL) {
    vm = pvm;
    JNIEnv* env;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_8) == JNI_OK) {
      classAwbSerialPort = env->FindClass("com/avrwb/comm/impl/AwbSerialPort");
      if (classAwbSerialPort != NULL) {
        classAwbSerialPort = reinterpret_cast<jclass> (env->NewWeakGlobalRef(classAwbSerialPort));
      }
      clazzIOException = env->FindClass("java/io/IOException");
      if (clazzIOException != NULL) {
        clazzIOException = reinterpret_cast<jclass> (env->NewWeakGlobalRef(clazzIOException));
      }
    }
  }
}

void Globals::throwJavaException(JNIEnv* env, const char* clazzName, const char* msg)
{
  jclass clazz = env->FindClass(clazzName);
  if (clazz != NULL) {
    throwException(env, clazz, msg);
    env->DeleteLocalRef(clazz);
  }
}

void Globals::throwIOException(JNIEnv* env, const char* msg)
{
  throwException(env, clazzIOException, msg);
}

void Globals::throwException(JNIEnv* env, jclass clazz, const char* msg)
{
  if (clazz != NULL) {
    env->ThrowNew(clazz, msg);
  }
}