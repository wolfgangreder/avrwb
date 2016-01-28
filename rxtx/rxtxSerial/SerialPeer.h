/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   SerialPeer.h
 * Author: wolfi
 *
 * Created on 27. Januar 2016, 21:59
 */

#ifndef SERIALPEER_H
#define SERIALPEER_H
#include <string>

class SerialPeer {
public:

  static void init(JNIEnv* env);
  static int getPeer(JNIEnv* env,jobject obj);

  static std::string getName(JNIEnv* env,jobject obj);

private:
  SerialPeer(){};
  static jclass clazzAwbSerialPort;
  static jfieldID fldPeer;
  static jfieldID fldName;
  
};

#endif /* SERIALPEER_H */

