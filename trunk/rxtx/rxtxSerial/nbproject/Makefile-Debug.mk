#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/7077a4e0/AwbCommDriver.o \
	${OBJECTDIR}/_ext/7077a4e0/AwbSerialPort.o \
	${OBJECTDIR}/_ext/7077a4e0/RXTXPort.o \
	${OBJECTDIR}/Globals.o \
	${OBJECTDIR}/SerialPeer.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-lstdc++

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/librxtxSerial.${CND_DLIB_EXT}

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/librxtxSerial.${CND_DLIB_EXT}: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/librxtxSerial.${CND_DLIB_EXT} ${OBJECTFILES} ${LDLIBSOPTIONS} -shared -fPIC

${OBJECTDIR}/_ext/7077a4e0/AwbCommDriver.o: /home/wolfi/projects/avrwb/rxtx/rxtxSerial/AwbCommDriver.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/7077a4e0
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/java/latest/include -I/usr/java/latest/include/linux -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/7077a4e0/AwbCommDriver.o /home/wolfi/projects/avrwb/rxtx/rxtxSerial/AwbCommDriver.cpp

${OBJECTDIR}/_ext/7077a4e0/AwbSerialPort.o: /home/wolfi/projects/avrwb/rxtx/rxtxSerial/AwbSerialPort.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/7077a4e0
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/java/latest/include -I/usr/java/latest/include/linux -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/7077a4e0/AwbSerialPort.o /home/wolfi/projects/avrwb/rxtx/rxtxSerial/AwbSerialPort.cpp

${OBJECTDIR}/_ext/7077a4e0/RXTXPort.o: /home/wolfi/projects/avrwb/rxtx/rxtxSerial/RXTXPort.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/7077a4e0
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/java/latest/include -I/usr/java/latest/include/linux -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/7077a4e0/RXTXPort.o /home/wolfi/projects/avrwb/rxtx/rxtxSerial/RXTXPort.cpp

${OBJECTDIR}/Globals.o: Globals.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/java/latest/include -I/usr/java/latest/include/linux -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/Globals.o Globals.cpp

${OBJECTDIR}/SerialPeer.o: SerialPeer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/java/latest/include -I/usr/java/latest/include/linux -fPIC  -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/SerialPeer.o SerialPeer.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/librxtxSerial.${CND_DLIB_EXT}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
