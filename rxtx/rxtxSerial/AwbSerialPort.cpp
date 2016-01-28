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
#include "AwbSerialPort.h"
#include "Globals.h"

int getFd(JNIEnv* env, jobject object)
{
  jfieldID fld = env->GetFieldID(Globals::getAwbSerialPortClass(), "fd", "J");
  if (fld == NULL) {
    return -1;
  }
  return env->GetLongField(object, fld);
}

void throwIOException(JNIEnv* env)
{
  char* msg = strerror(errno);
  Globals::throwIOException(env, msg);
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
  return fd;
}

JNIEXPORT jlong JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_nativeClose(JNIEnv * env, jobject port, jlong fd)
{
  if (fd != -1) {
    close((int) fd);
  }
  return -1;
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

bool isCustomSpeedSelected(termios* ti,
                           serial_struct* si)
{
  bool custSpeed = si->flags & ASYNC_SPD_CUST;
  return custSpeed = ti->c_ispeed == B38400;
}

int getSizeConstant(JNIEnv* env, jobject dataBits)
{
  return CS8;
}

int getParityConstant(JNIEnv* env, jobject parity)
{
  return 0;
}

int getStopConstant(JNIEnv* env, jobject stopBits)
{
  return 0;
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
  speed_t oldISpeed = cfgetospeed(&termios);
  speed_t c_speed = getSpeedParamenter(speed);
  if (c_speed == -1) { // custom speed
    serinfo.flags |= ASYNC_SPD_CUST;
    if (cfsetispeed(&termios, CBAUDEX) < 0) {
      throwIOException(env);
      return;
    }
    if (cfsetospeed(&termios, CBAUDEX) < 0) {
      throwIOException(env);
      return;
    }
    serinfo.custom_divisor = serinfo.baud_base / speed;
  } else {
    serinfo.flags &= ~ASYNC_SPD_MASK;
    if (cfsetispeed(&termios, c_speed) < 0) {
      throwIOException(env);
      return;
    }
    if (cfsetospeed(&termios, c_speed) < 0) {
      throwIOException(env);
      return;
    }
  }
  termios.c_cflag |= (CLOCAL | CREAD);
  termios.c_cflag &= ~(CSIZE | PARENB | CSTOPB | CRTSCTS);
  termios.c_cflag |= getSizeConstant(env, dataBits);
  termios.c_cflag |= getParityConstant(env, parity);
  termios.c_cflag |= getStopConstant(env, stopBits);
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
}

JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_sendBytes(JNIEnv* env,
                                                                        jobject port,
                                                                        jbyteArray data)
{
  int fd = getFd(env, port);
  if (fd == -1) {
    return;
  }
  jsize size = env->GetArrayLength(data);
  if (size > 0) {
    jboolean isCopy;
    jbyte* arr = env->GetByteArrayElements(data, &isCopy);
    if (arr != NULL) {
      if (write(fd, arr, size) < size) {
        throwIOException(env);
      }
      env->ReleaseByteArrayElements(data, arr, 0);
    }
  }

}