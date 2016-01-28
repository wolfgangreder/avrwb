/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   SerialPeer.cpp
 * Author: wolfi
 * 
 * Created on 27. Januar 2016, 21:59
 */

#include "SerialPeer.h"
#include "Globals.h"
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>

jclass SerialPeer::clazzAwbSerialPort = 0;
jfieldID SerialPeer::fldName = 0;
jfieldID SerialPeer::fldPeer = 0;

SerialPeer::init (JNIEnv* env)
{
  clazzAwbSerialPort = env->FindClass ("com/avrwb/comm/impl/AwbSerialPort");
  if (clazzAwbSerialPort != NULL) {
    clazzAwbSerialPort = reinterpret_cast<jclass> (env->NewWeakGlobalRef (clazzAwbSerialPort));
    fldName = env->GetFieldID (clazzAwbSerialPort, "peer", "J");
    fldName = env->GetFieldID (clazzAwbSerialPort, "name", "Ljava/lang/String");
  }
}

int SerialPeer::getPeer (JNIEnv* env, jobject obj)
{
}
std::string SerialPeer::getName (JNIEnv* env, jobject obj);

SerialPeer::SerialPeer (const std::string& name) { }

SerialPeer::~SerialPeer ()
{
  if (fd > 0) {
    close (fd);
  }
}

int SerialPeer::openPort ()
{
  fd = open (name.c_str (), O_RDWR | O_NOCTTY | O_NDELAY);
  if (fd == -1) {
    return errno;
  }
  return 0;
}
