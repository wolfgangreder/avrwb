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

#ifndef GLOBALS_H
#define GLOBALS_H

#include <jni.h>

class Globals {
public:

  static JavaVM* getJVM() {
    return vm;
  }
  
  static jclass getAwbSerialPortClass()
  {
    return classAwbSerialPort;
  }
  static void setJVM(JavaVM* vm);
  static void throwJavaException(JNIEnv* env,const char* clazzName,const char* msg);
  static void throwIOException(JNIEnv* env,const char* msg);
private:
  static JavaVM* vm;
  static jclass clazzIOException;
  static jclass classAwbSerialPort;
  
  static void throwException(JNIEnv* env,jclass clazz,const char* msg);
};

#endif /* GLOBALS_H */

