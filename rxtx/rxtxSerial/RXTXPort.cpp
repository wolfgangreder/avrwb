/*-------------------------------------------------------------------------
|   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
|   RXTX is a native interface to serial ports in java.
|   Copyright 1997-2009 by Trent Jarvi tjarvi@qbang.org and others who
|   actually wrote it.  See individual source files for more information.
|
|   A copy of the LGPL v 2.1 may be found at
|   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
|   here for your convenience.
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Lesser General Public
|   License as published by the Free Software Foundation; either
|   version 2.1 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Lesser General Public License for more details.
|
|   An executable that contains no derivative of any portion of RXTX, but
|   is designed to work with RXTX by being dynamically linked with it,
|   is considered a "work that uses the Library" subject to the terms and
|   conditions of the GNU Lesser General Public License.
|
|   The following has been added to the RXTX License to remove
|   any confusion about linking to RXTX.   We want to allow in part what
|   section 5, paragraph 2 of the LGPL does not permit in the special
|   case of linking over a controlled interface.  The intent is to add a
|   Java Specification Request or standards body defined interface in the
|   future as another exception but one is not currently available.
|
|   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
|
|   As a special exception, the copyright holders of RXTX give you
|   permission to link RXTX with independent modules that communicate with
|   RXTX solely through the Sun Microsytems CommAPI interface version 2,
|   regardless of the license terms of these independent modules, and to copy
|   and distribute the resulting combined work under terms of your choice,
|   provided that every copy of the combined work is accompanied by a complete
|   copy of the source code of RXTX (the version of RXTX used to produce the
|   combined work), being distributed under the terms of the GNU Lesser General
|   Public License plus this exception.  An independent module is a
|   module which is not derived from or based on RXTX.
|
|   Note that people who make modified versions of RXTX are not obligated
|   to grant this special exception for their modified versions; it is
|   their choice whether to do so.  The GNU Lesser General Public License
|   gives permission to release a modified version without this exception; this
|   exception also makes it possible to release a modified version which
|   carries forward this exception.
|
|   You should have received a copy of the GNU Lesser General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
|   All trademarks belong to their respective owners.
--------------------------------------------------------------------------*/

extern int errno;

#include "RXTXPort.h"

JavaVM *javaVM = NULL;

void JNICALL Java_gnu_io_RXTXPort_Initialize(JNIEnv * env, jclass object) {
}

jint JNICALL Java_gnu_io_RXTXPort_open(JNIEnv * env, jobject jobj, jstring jstr) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeGetParity(JNIEnv * env, jobject object, jint param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeGetFlowControlMode(JNIEnv * env, jobject object, jint param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetSerialPortParams(JNIEnv * env, jobject object, jint param1, jint param2, jint param3, jint param4) {
}

void JNICALL Java_gnu_io_RXTXPort_setflowcontrol(JNIEnv * env, jobject object, jint param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_NativegetReceiveTimeout(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_NativeisReceiveTimeoutEnabled(JNIEnv * env, jobject object) {
}

void JNICALL Java_gnu_io_RXTXPort_NativeEnableReceiveTimeoutThreshold(JNIEnv * env, jobject object, jint param1, jint param2, jint param3) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isDTR(JNIEnv * env, jobject object) {
}

void JNICALL Java_gnu_io_RXTXPort_setDTR(JNIEnv * env, jobject object, jboolean param1) {
}

void JNICALL Java_gnu_io_RXTXPort_setRTS(JNIEnv * env, jobject object, jboolean param1) {
}

void JNICALL Java_gnu_io_RXTXPort_setDSR(JNIEnv * env, jobject object, jboolean param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isCTS(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isDSR(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isCD(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isRI(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_isRTS(JNIEnv * env, jobject object) {
}

void JNICALL Java_gnu_io_RXTXPort_sendBreak(JNIEnv * env, jobject object, jint param1) {
}

void JNICALL Java_gnu_io_RXTXPort_writeByte(JNIEnv * env, jobject object, jint param1, jboolean param2) {
}

void JNICALL Java_gnu_io_RXTXPort_writeArray(JNIEnv * env, jobject object, jbyteArray param1, jint param2, jint param3, jboolean param4) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeDrain(JNIEnv * env, jobject object, jboolean param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeavailable(JNIEnv * env, jobject object) {
}

jint JNICALL Java_gnu_io_RXTXPort_readByte(JNIEnv * env, jobject object) {
}

jint JNICALL Java_gnu_io_RXTXPort_readArray(JNIEnv * env, jobject object, jbyteArray param1, jint param2, jint param3) {
}

jint JNICALL Java_gnu_io_RXTXPort_readTerminatedArray(JNIEnv * env, jobject object, jbyteArray param1, jint param2, jint param3, jbyteArray param4) {
}

void JNICALL Java_gnu_io_RXTXPort_eventLoop(JNIEnv * env, jobject object) {
}

void JNICALL Java_gnu_io_RXTXPort_interruptEventLoop(JNIEnv * env, jobject object) {
}

void JNICALL Java_gnu_io_RXTXPort_nativeSetEventFlag(JNIEnv * env, jobject object, jint param1, jint param2, jboolean param3) {
}

void JNICALL Java_gnu_io_RXTXPort_nativeClose(JNIEnv * env, jobject jobj, jstring jstr) {
}

void JNICALL Java_gnu_io_RXTXPort_nativeStaticSetSerialPortParams(JNIEnv * env, jclass object, jstring param1, jint param2, jint param3, jint param4, jint param5) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticSetDSR(JNIEnv * env, jclass object, jstring param1, jboolean param2) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticSetDTR(JNIEnv * env, jclass object, jstring param1, jboolean param2) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticSetRTS(JNIEnv * env, jclass object, jstring param1, jboolean param2) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsDSR(JNIEnv * env, jclass object, jstring param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsDTR(JNIEnv * env, jclass object, jstring param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsRTS(JNIEnv * env, jclass object, jstring param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsCTS(JNIEnv * env, jclass object, jstring param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsCD(JNIEnv * env, jclass object, jstring param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeStaticIsRI(JNIEnv * env, jclass object, jstring param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeStaticGetBaudRate(JNIEnv * env, jclass object, jstring param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeStaticGetDataBits(JNIEnv * env, jclass object, jstring param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeStaticGetParity(JNIEnv * env, jclass object, jstring param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeStaticGetStopBits(JNIEnv * env, jclass object, jstring param1) {
}

jbyte JNICALL Java_gnu_io_RXTXPort_nativeGetParityErrorChar(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetParityErrorChar(JNIEnv * env, jobject object, jbyte param1) {
}

jbyte JNICALL Java_gnu_io_RXTXPort_nativeGetEndOfInputChar(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetEndOfInputChar(JNIEnv * env, jobject object, jbyte param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetUartType(JNIEnv * env, jobject object, jstring param1, jboolean param2) {
}

jstring JNICALL Java_gnu_io_RXTXPort_nativeGetUartType(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetBaudBase(JNIEnv * env, jobject object, jint param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeGetBaudBase(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetDivisor(JNIEnv * env, jobject object, jint param1) {
}

jint JNICALL Java_gnu_io_RXTXPort_nativeGetDivisor(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetLowLatency(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeGetLowLatency(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeSetCallOutHangup(JNIEnv * env, jobject object, jboolean param1) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeGetCallOutHangup(JNIEnv * env, jobject object) {
}

jboolean JNICALL Java_gnu_io_RXTXPort_nativeClearCommInput(JNIEnv * env, jobject object) {
}
