// Native methods implementation of
// /home/wolfi/projects/avrwb/rxtx/rxtx/src/com/avrwb/comm/impl/AwbSerialPort.java

#include <errno.h>
#include <string.h>
#include "AwbSerialPort.h"
#include "SerialPeer.h"
#include "Globals.h"

int getFileDescriptor(JNIEnv* env,jobject ob)
{
  
}
jlong JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_nativeOpen
(JNIEnv * env, jobject object, jstring portName)
{
  
  jboolean isCopy;
  const char* name = env->GetStringUTFChars (portName, &isCopy);
  SerialPeer* peer = new SerialPeer (name);
  env->ReleaseStringUTFChars (portName, name);
  int openResult = peer->openPort ();
  if (openResult != 0) {
    char* msg = strerror (openResult);
    Globals::throwIOException (env, msg);
    return 0L;
  }
  return (jlong) peer;
}
JNIEXPORT void JNICALL Java_com_avrwb_comm_impl_AwbSerialPort_nativeClose
  (JNIEnv * env, jobject port)
{
  jfieldID field = env->GetFieldID (Globals::getAwbSerialPortClass (),"peer","J");
  jlong lpeer = env->GetLongField (port,field);
  SerialPeer* peer = (SerialPeer*)lpeer;
  delete peer;
  env->SetLongField(port,field,0L);
}