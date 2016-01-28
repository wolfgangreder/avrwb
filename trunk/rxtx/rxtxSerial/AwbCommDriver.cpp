// Native methods implementation of
// /home/wolfi/projects/avrwb/rxtx/rxtx/src/com/avrwb/comm/impl/AwbCommDriver.java

#include "AwbCommDriver.h"
#include "Globals.h"

void JNICALL Java_com_avrwb_comm_impl_AwbCommDriver_nativeInit
(JNIEnv * env, jobject object)
{
  JavaVM* vm;
  jint i = env->GetJavaVM (&vm);
  if (i == 0) {
    Globals::setJVM (vm);
  }
}
