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
// Native methods implementation of
// /home/wolfi/projects/avrwb/rxtx/rxtx/src/com/avrwb/comm/impl/AwbSerialPort.java
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <termios.h>
#include <sys/ioctl.h>
#include <asm/ioctls.h>
#include <linux/serial.h>
#include <iostream>
#include <malloc.h>
#include "AwbSerialPort.h"
#include "Globals.h"
#include "enums.h"

jfieldID fldLock = NULL;
jfieldID fldFd = NULL;
jfieldID fldBaudRate = NULL;
jfieldID fldDataBits = NULL;
jfieldID fldStopBits = NULL;
jfieldID fldParity = NULL;
jclass clazzDataBits = NULL;
jclass clazzStopBits = NULL;
jclass clazzParity = NULL;

void interruptCurrentThread(JNIEnv* env)
{
  jclass clazzThread = env->FindClass("java/lang/Thread");
  if (clazzThread == NULL) {
    return;
  }
  jmethodID meth = env->GetStaticMethodID(clazzThread, "currentThread", "()Ljava/lang/Thread;");
  if (meth == NULL) {
    return;
  }
  jmethodID methInterrupt = env->GetMethodID(clazzThread, "interrupt", "()V");
  if (methInterrupt == NULL) {
    return;
  }
  jobject currentThread = env->CallStaticObjectMethod(clazzThread, meth);
  if (currentThread == NULL) {
    return;
  }
  env->CallVoidMethod(currentThread, methInterrupt);
}

jobject getLock(JNIEnv* env, jobject port)
{
  if (fldLock == NULL) {
    fldLock = env->GetFieldID(Globals::getAwbSerialPortClass(), "lock", "Ljava/lang/Object;");
  }
  if (fldLock == NULL) {
    return NULL;
  }
  return env->GetObjectField(port, fldLock);
}

int getFd(JNIEnv* env, jobject object)
{
  jobject lock = getLock(env, object);
  if (lock == NULL) {
    return -1;
  }
  if (env->MonitorEnter(lock) != JNI_OK) {
    return -1;
  }
  int result = -1;
  if (fldFd == NULL) {
    fldFd = env->GetFieldID(Globals::getAwbSerialPortClass(), "fd", "J");
  }
  if (fldFd != NULL) {
    result = env->GetLongField(object, fldFd);
  }
  env->MonitorExit(lock);
  return result;
}

int getEnumOrdinal(JNIEnv* env, jobject en)
{
  jclass clazz = env->GetObjectClass(en);
  if (clazz == NULL) {
    return -1;
  }
  jmethodID mId = env->GetMethodID(clazz, "ordinal", "()I");
  if (mId == NULL) {
    return -1;
  }
  return env->CallIntMethod(en, mId);
}

jobject getEnumByOrdinal(JNIEnv* env, jclass clazz, int ordinal, const char* sig)
{
  jmethodID mId = env->GetStaticMethodID(clazz, "values", sig);
  if (mId == NULL) {
    return NULL;
  }
  jobjectArray arr = (jobjectArray) env->CallStaticObjectMethod(clazz, mId);
  if (arr == NULL) {
    return NULL;
  }
  return env->GetObjectArrayElement(arr, ordinal);
}

jobject getDataBitsByOrdinal(JNIEnv* env, int ordinal)
{
  return getEnumByOrdinal(env, clazzDataBits, ordinal, "()[Lcom/avrwb/comm/DataBits;");
}

jobject getStopBitsByOrdinal(JNIEnv* env, int ordinal)
{
  return getEnumByOrdinal(env, clazzStopBits, ordinal, "()[Lcom/avrwb/comm/StopBits;");
}

jobject getParityByOrdinal(JNIEnv* env, int ordinal)
{
  return getEnumByOrdinal(env, clazzParity, ordinal, "()[Lcom/avrwb/comm/Parity;");
}

void throwIOException(JNIEnv* env)
{
  char* msg = strerror(errno);
  Globals::throwIOException(env, msg);
}

jclass loadClass(JNIEnv* env, const char* clazzName)
{
  jclass clazz = env->FindClass(clazzName);
  if (clazz != NULL) {
    return (jclass) env->NewWeakGlobalRef(clazz);
  }
  return NULL;
}

speed_t getSpeedParamenter(int speed)
{
  switch (speed)
  {
    case 50:
      return B50;
    case 75:
      return B75;
    case 110:
      return B110;
    case 134:
      return B134;
    case 150:
      return B150;
    case 200:
      return B200;
    case 300:
      return B300;
    case 600:
      return B600;
    case 1200:
      return B1200;
    case 1800:
      return B1800;
    case 2400:
      return B2400;
    case 4800:
      return B4800;
    case 9600:
      return B9600;
    case 19200:
      return B19200;
    case 38400:
      return B38400;
    case 57600:
      return B57600;
    case 115200:
      return B115200;
    case 230400:
      return B230400;
    case 460800:
      return B460800;
    case 576000:
      return B576000;
    case 921600:
      return B921600;
    case 1000000:
      return B1000000;
    case 1152000:
      return B1152000;
    case 1500000:
      return B1500000;
    case 2000000:
      return B2000000;
    case 2500000:
      return B2500000;
    case 3000000:
      return B3000000;
    case 3500000:
      return B3500000;
    case 4000000:
      return B4000000;
    default:
      return -1;
  }
}

int getSpeed(int speedConstant)
{
  switch (speedConstant)
  {
    case B0:
      return 0;
    case B1000000:
      return 1000000;
    case B110:
      return 110;
    case B115200:
      return 115200;
    case B1152000:
      return 1152000;
    case B1200:
      return 1200;
    case B134:
      return 134;
    case B150:
      return 150;
    case B1500000:
      return 1500000;
    case B1800:
      return 1800;
    case B19200:
      return 19200;
    case B200:
      return 200;
    case B2000000:
      return 2000000;
    case B230400:
      return 230400;
    case B2400:
      return 2400;
    case B2500000:
      return 2500000;
    case B300:
      return 300;
    case B3000000:
      return 3000000;
    case B3500000:
      return 3500000;
    case B38400:
      return 38400;
    case B4000000:
      return 4000000;
    case B460800:
      return 460800;
    case B4800:
      return 4800;
    case B50:
      return 50;
    case B500000:
      return 500000;
    case B57600:
      return 57600;
    case B576000:
      return B576000;
    case B600:
      return 600;
    case B75:
      return 75;
    case B921600:
      return 921600;
    case B9600:
      return 9600;
    default:
      return -1;
  }
}

int getSizeConstant(JNIEnv* env, jobject dataBits)
{
  int en = getEnumOrdinal(env, dataBits);
  switch (en)
  {
    case DATA_5:
      return CS5;
    case DATA_6:
      return CS6;
    case DATA_7:
      return CS7;
    case DATA_8:
      return CS8;
    default:
      return -1;
  }
}

int getSizeOrdinal(int db)
{
  switch (db)
  {
    case CS5:
      return DATA_5;
    case CS6:
      return DATA_6;
    case CS7:
      return DATA_7;
    case CS8:
      return DATA_8;
    default:
      return -1;
  }
}

int getParityConstant(JNIEnv* env, jobject parity)
{
  int en = getEnumOrdinal(env, parity);
  switch (en)
  {
    case NONE:
      return 0;
    case ODD:
      return (PARENB | PARODD);
    case EVEN:
      return PARENB;
    case MARK:
      return (PARENB | PARMRK);
    case SPACE:
      Globals::throwUnsupportedCommOperationException(env, "Parity SPACE not supported");
      return -1;
    default:
      return -1;
  }
}

int getParityOrdinal(int pe)
{
  switch (pe)
  {
    case 0:
      return NONE;
    case PARENB | PARODD:
      return ODD;
    case PARENB:
      return EVEN;
    case PARENB | PARMRK:
      return MARK;
    default:
      return NONE;
  }
}

int getStopConstant(JNIEnv* env, jobject stopBits)
{
  int en = getEnumOrdinal(env, stopBits);
  switch (en)
  {
    case STOP_1:
      return 0;
    case STOP_2:
      return CSTOPB;
    case STOP_1_5:
      Globals::throwUnsupportedCommOperationException(env, "1.5 Stopbits not supported");
      return -1;
    default:
      return -1;
  }
}

int getStopOrdinal(int stop)
{
  switch (stop)
  {
    case 0:
      return STOP_1;
    case CSTOPB:
      return STOP_2;
    default:
      return STOP_1;
  }
}

int initSettingsField(JNIEnv* env)
{
  if (clazzDataBits == NULL) {
    clazzDataBits = loadClass(env, "com/avrwb/comm/DataBits");
  }
  if (clazzParity == NULL) {
    clazzParity = loadClass(env, "com/avrwb/comm/Parity");
  }
  if (clazzStopBits == NULL) {
    clazzStopBits = loadClass(env, "com/avrwb/comm/StopBits");
  }
  if (fldBaudRate == NULL) {
    fldBaudRate = env->GetFieldID(Globals::getAwbSerialPortClass(), "baudRate", "I");
  }
  if (fldDataBits == NULL) {
    fldDataBits = env->GetFieldID(Globals::getAwbSerialPortClass(), "dataBits", "Lcom/avrwb/comm/DataBits;");
  }
  if (fldStopBits == NULL) {
    fldStopBits = env->GetFieldID(Globals::getAwbSerialPortClass(), "stopBits", "Lcom/avrwb/comm/StopBits;");
  }
  if (fldParity == NULL) {
    fldParity = env->GetFieldID(Globals::getAwbSerialPortClass(), "parity", "Lcom/avrwb/comm/Parity;");
  }
  return fldParity != NULL && fldStopBits != NULL && fldDataBits != NULL && fldBaudRate != NULL && clazzDataBits != NULL && clazzStopBits != NULL && clazzStopBits != NULL;
}

void internalReadPortParams(JNIEnv* env, jobject port, int fd)
{
  int baudRate = 0;
  int stopBits = -1;
  int dataBits = -1;
  int parity = -1;
  struct serial_struct serinfo;
  struct termios termios;
  int result = tcgetattr(fd, &termios);
  if (result < 0) {
    throwIOException(env);
    return;
  }
  result = ioctl(fd, TIOCGSERIAL, &serinfo);
  if (result < 0) {
    throwIOException(env);
    return;
  }
  speed_t speed = cfgetospeed(&termios);
  baudRate = getSpeed(speed);
  if (serinfo.flags & ASYNC_SPD_CUST && baudRate == -1) {
    if (serinfo.custom_divisor != 0) {
      baudRate = serinfo.baud_base / serinfo.custom_divisor;
    } else {
      baudRate = -1;
    }
  }
  dataBits = getSizeOrdinal(termios.c_cflag & CSIZE);
  stopBits = getStopOrdinal(termios.c_cflag & CSTOPB);
  parity = getParityOrdinal(termios.c_cflag & (PARENB | PARODD));
  if (dataBits == -1 || stopBits == -1 || parity == -1) {
    return;
  }
  if (!initSettingsField(env)) {
    return;
  }
  jobject db = getDataBitsByOrdinal(env, dataBits);
  if (db == NULL) {
    return;
  }
  jobject sb = getStopBitsByOrdinal(env, stopBits);
  if (sb == NULL) {
    return;
  }
  jobject pa = getParityByOrdinal(env, parity);
  if (pa == NULL) {
    return;
  }
  jobject lock = getLock(env, port);
  if (lock == NULL) {
    return;
  }
  if (env->MonitorEnter(lock) != JNI_OK) {
    return;
  }
  env->SetIntField(port, fldBaudRate, baudRate);
  env->SetObjectField(port, fldDataBits, db);
  env->SetObjectField(port, fldStopBits, sb);
  env->SetObjectField(port, fldParity, pa);
  env->MonitorExit(lock);
}

jlong JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_nativeOpen(JNIEnv * env, jobject object, jstring str)
{
  jboolean isCopy;
  const char* chtmp = env->GetStringUTFChars(str, &isCopy);
  if (chtmp == NULL) {
    return -1;
  }
  int fd = -1;
  fd = open(chtmp, O_RDWR | O_NOCTTY | O_NDELAY);
  env->ReleaseStringUTFChars(str, chtmp);
  if (fd == -1) {
    throwIOException(env);
  }
  internalReadPortParams(env, object, fd);
  return fd;
}

JNIEXPORT jlong JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_nativeClose(JNIEnv * env, jobject port, jlong fd)
{
  if (fd != -1) {
    close((int) fd);
  }
  return -1;
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_setSerialPortParams(JNIEnv * env,
                                                                                  jobject obj,
                                                                                  jint speed,
                                                                                  jobject dataBits,
                                                                                  jobject stopBits,
                                                                                  jobject parity)
{
  int fd = getFd(env, obj);
  if (fd == -1) {
    return;
  }
  struct termios termios;
  struct serial_struct serinfo;
  int result = tcgetattr(fd, &termios);
  if (result < 0) {
    throwIOException(env);
    return;
  }
  result = ioctl(fd, TIOCGSERIAL, &serinfo);
  if (result < 0) {
    throwIOException(env);
    return;
  }
  speed_t c_speed = getSpeedParamenter(speed);
  serinfo.flags &= ~ASYNC_SPD_MASK;
  if (c_speed == -1) { // custom speed
    serinfo.flags |= ASYNC_SPD_CUST;
    if (cfsetispeed(&termios, B38400) < 0) {
      throwIOException(env);
      return;
    }
    if (cfsetospeed(&termios, B38400) < 0) {
      throwIOException(env);
      return;
    }
    serinfo.custom_divisor = serinfo.baud_base / speed;
  } else {
    if (cfsetispeed(&termios, c_speed) < 0) {
      throwIOException(env);
      return;
    }
    if (cfsetospeed(&termios, c_speed) < 0) {
      throwIOException(env);
      return;
    }
  }
  cfmakeraw(&termios);
  termios.c_cflag |= (CLOCAL | CREAD);
  termios.c_cflag &= ~(CSIZE | PARENB | CSTOPB | CRTSCTS);
  int tmp = getSizeConstant(env, dataBits);
  if (tmp == -1) {
    return;
  }
  termios.c_cflag |= tmp;
  tmp = getParityConstant(env, parity);
  if (tmp == -1) {
    return;
  }
  termios.c_cflag |= tmp;
  tmp = getStopConstant(env, stopBits);
  if (tmp == -1) {
    return;
  }
  termios.c_cflag |= tmp;
  termios.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
  termios.c_iflag &= ~(IXON | IXOFF | IXANY);
  termios.c_oflag &= ~(OPOST);
  if (tcsetattr(fd, TCSANOW, &termios) < 0) {
    throwIOException(env);
    return;
  }
  if (ioctl(fd, TIOCSSERIAL, &serinfo) < 0) {
    throwIOException(env);
    return;
  }
  internalReadPortParams(env, obj, fd);
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_readPortParams(JNIEnv* env, jobject port)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return;
  }
  internalReadPortParams(env, port, fd);
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_sendBytes(JNIEnv* env,
                                                                        jobject port,
                                                                        jbyteArray data,
                                                                        jint offset,
                                                                        jint len)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return;
  }
  jbyte* buffer = (jbyte*) malloc(len);
  if (buffer == NULL) {
    throwIOException(env);
    return;
  }
  env->GetByteArrayRegion(data, offset, len, buffer);
  if (write(fd, buffer, len) < len) {
    throwIOException(env);
  }
  free(buffer);
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_sendByte(JNIEnv* env, jobject port, jint byte)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return;
  }
  jbyte data = (jbyte) byte;
  if (write(fd, &data, 1) < 1) {
    throwIOException(env);
  }
}

JNIEXPORT jint JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_readBytes(JNIEnv* env,
                                                                        jobject port,
                                                                        jbyteArray byteArray,
                                                                        jint offset,
                                                                        jint len,
                                                                        jint microsToWait)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return -1;
  }
  int bytesAvailable = 0;
  fd_set fset;
  struct timeval timeout;
  FD_ZERO(&fset);
  FD_SET(fd, &fset);
  timeval* toUsed = NULL;
  if (microsToWait >= 0) {
    timeout.tv_sec = microsToWait / 1000000;
    timeout.tv_usec = (microsToWait % 1000000);
    toUsed = &timeout;
  }
  int selectResult = select(fd + 1, &fset, NULL, &fset, toUsed);
  if (selectResult == -1) {
    if (errno == EINTR) {
      interruptCurrentThread(env);
      return -1;
    } else {
      throwIOException(env);
      return -1;
    }
  }
  if (selectResult == 0) {
    return 0;
  }
  if (ioctl(fd, FIONREAD, &bytesAvailable) < 0) {
    throwIOException(env);
    return -1;
  }
  if (bytesAvailable > 0) {
    int bytesToRead = bytesAvailable > len ? len : bytesAvailable;
    jbyte* buffer = (jbyte*) malloc(bytesToRead);
    if (buffer == NULL) {
      throwIOException(env);
      return -1;
    }
    size_t bytesRead = read(fd, buffer, bytesToRead);
    if (bytesRead < 0) {
      free(buffer);
      throwIOException(env);
      return -1;
    } else if (bytesRead > 0) {
      env->SetByteArrayRegion(byteArray, offset, bytesRead, buffer);
      free(buffer);
      return bytesRead;
    } else {
      free(buffer);
    }
  }
  return bytesAvailable;
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_flush(JNIEnv* env, jobject port)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return;
  }
  if (ioctl(fd, TCFLSH, NULL) < 0) {
    throwIOException(env);
  }
}

JNIEXPORT jint JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_bytesAvailable(JNIEnv* env, jobject port)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return -1;
  }
  int bytesAvailable = 0;
  if (ioctl(fd, FIONREAD, &bytesAvailable) < 0) {
    throwIOException(env);
    return -1;
  }
  return bytesAvailable;
}