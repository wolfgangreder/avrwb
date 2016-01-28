/*-------------------------------------------------------------------------
|   rxtx is a native interface to serial ports in java.
|   Copyright 1997-2004 by Trent Jarvi taj@www.linux.org.uk.
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Library General Public
|   License as published by the Free Software Foundation; either
|   version 2 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Library General Public License for more details.
|
|   You should have received a copy of the GNU Library General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
--------------------------------------------------------------------------*/
package gnu.io;
import java.io.*;
import java.util.*;

/**
 * <p>Abstract SerialPort class.</p>
 *
 * @author Trent Jarvi
 * @version %I%, %G%
 * @since JDK1.0
 */
public abstract class SerialPort extends CommPort {
	/** Constant <code>DATABITS_5=5</code> */
	public static final int  DATABITS_5             =5;
	/** Constant <code>DATABITS_6=6</code> */
	public static final int  DATABITS_6             =6;
	/** Constant <code>DATABITS_7=7</code> */
	public static final int  DATABITS_7             =7;
	/** Constant <code>DATABITS_8=8</code> */
	public static final int  DATABITS_8             =8;
	/** Constant <code>PARITY_NONE=0</code> */
	public static final int  PARITY_NONE            =0;
	/** Constant <code>PARITY_ODD=1</code> */
	public static final int  PARITY_ODD             =1;
	/** Constant <code>PARITY_EVEN=2</code> */
	public static final int  PARITY_EVEN            =2;
	/** Constant <code>PARITY_MARK=3</code> */
	public static final int  PARITY_MARK            =3;
	/** Constant <code>PARITY_SPACE=4</code> */
	public static final int  PARITY_SPACE           =4;
	/** Constant <code>STOPBITS_1=1</code> */
	public static final int  STOPBITS_1             =1;
	/** Constant <code>STOPBITS_2=2</code> */
	public static final int  STOPBITS_2             =2;
	/** Constant <code>STOPBITS_1_5=3</code> */
	public static final int  STOPBITS_1_5           =3;
	/** Constant <code>FLOWCONTROL_NONE=0</code> */
	public static final int  FLOWCONTROL_NONE       =0;
	/** Constant <code>FLOWCONTROL_RTSCTS_IN=1</code> */
	public static final int  FLOWCONTROL_RTSCTS_IN  =1;
	/** Constant <code>FLOWCONTROL_RTSCTS_OUT=2</code> */
	public static final int  FLOWCONTROL_RTSCTS_OUT =2;
	/** Constant <code>FLOWCONTROL_XONXOFF_IN=4</code> */
	public static final int  FLOWCONTROL_XONXOFF_IN =4;
	/** Constant <code>FLOWCONTROL_XONXOFF_OUT=8</code> */
	public static final int  FLOWCONTROL_XONXOFF_OUT=8;

	/**
	 * <p>setSerialPortParams.</p>
	 *
	 * @param b a int.
	 * @param d a int.
	 * @param s a int.
	 * @param p a int.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract void setSerialPortParams( int b, int d, int s, int p )
		throws UnsupportedCommOperationException;
	/**
	 * <p>getBaudRate.</p>
	 *
	 * @return a int.
	 */
	public abstract int getBaudRate();
	/**
	 * <p>getDataBits.</p>
	 *
	 * @return a int.
	 */
	public abstract int getDataBits();
	/**
	 * <p>getStopBits.</p>
	 *
	 * @return a int.
	 */
	public abstract int getStopBits();
	/**
	 * <p>getParity.</p>
	 *
	 * @return a int.
	 */
	public abstract int getParity();
	/**
	 * <p>setFlowControlMode.</p>
	 *
	 * @param flowcontrol a int.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract void setFlowControlMode( int flowcontrol )
		throws UnsupportedCommOperationException;
	/**
	 * <p>getFlowControlMode.</p>
	 *
	 * @return a int.
	 */
	public abstract int getFlowControlMode();
	/**
	 * <p>isDTR.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isDTR();
	/**
	 * <p>setDTR.</p>
	 *
	 * @param state a boolean.
	 */
	public abstract void setDTR( boolean state );
	/**
	 * <p>setRTS.</p>
	 *
	 * @param state a boolean.
	 */
	public abstract void setRTS( boolean state );
	/**
	 * <p>isCTS.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isCTS();
	/**
	 * <p>isDSR.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isDSR();
	/**
	 * <p>isCD.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isCD();
	/**
	 * <p>isRI.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isRI();
	/**
	 * <p>isRTS.</p>
	 *
	 * @return a boolean.
	 */
	public abstract boolean isRTS();
	/**
	 * <p>sendBreak.</p>
	 *
	 * @param duration a int.
	 */
	public abstract void sendBreak( int duration );
	/**
	 * <p>addEventListener.</p>
	 *
	 * @param lsnr a {@link gnu.io.SerialPortEventListener} object.
	 * @throws java.util.TooManyListenersException if any.
	 */
	public abstract void addEventListener( SerialPortEventListener lsnr )
		throws TooManyListenersException;
	/**
	 * <p>removeEventListener.</p>
	 */
	public abstract void removeEventListener();
	/**
	 * <p>notifyOnDataAvailable.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnDataAvailable( boolean enable );
	/**
	 * <p>notifyOnOutputEmpty.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnOutputEmpty( boolean enable );
	/**
	 * <p>notifyOnCTS.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnCTS( boolean enable );
	/**
	 * <p>notifyOnDSR.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnDSR( boolean enable );
	/**
	 * <p>notifyOnRingIndicator.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnRingIndicator( boolean enable );
	/**
	 * <p>notifyOnCarrierDetect.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnCarrierDetect( boolean enable );
	/**
	 * <p>notifyOnOverrunError.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnOverrunError( boolean enable );
	/**
	 * <p>notifyOnParityError.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnParityError( boolean enable );
	/**
	 * <p>notifyOnFramingError.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnFramingError( boolean enable );
	/**
	 * <p>notifyOnBreakInterrupt.</p>
	 *
	 * @param enable a boolean.
	 */
	public abstract void notifyOnBreakInterrupt( boolean enable );
/*
	public abstract void setRcvFifoTrigger(int trigger);
         deprecated
*/
/* ----------------------   end of commapi ------------------------ */

/*
	can't have static abstract?

	public abstract static boolean staticSetDTR( String port, boolean flag )
		throws UnsupportedCommOperationException;
	public abstract static boolean staticSetRTS( String port, boolean flag )
		throws UnsupportedCommOperationException;
*/
	/**
	 * <p>getParityErrorChar.</p>
	 *
	 * @return a byte.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract byte getParityErrorChar( )
		throws UnsupportedCommOperationException;
	/**
	 * <p>setParityErrorChar.</p>
	 *
	 * @param b a byte.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean setParityErrorChar( byte b )
		throws UnsupportedCommOperationException;
	/**
	 * <p>getEndOfInputChar.</p>
	 *
	 * @return a byte.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract byte getEndOfInputChar( )
		throws UnsupportedCommOperationException;
	/**
	 * <p>setEndOfInputChar.</p>
	 *
	 * @param b a byte.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean setEndOfInputChar( byte b )
		throws UnsupportedCommOperationException;
	/**
	 * <p>setUARTType.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 * @param test a boolean.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean setUARTType(String type, boolean test)
		throws UnsupportedCommOperationException;
	/**
	 * <p>getUARTType.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract String getUARTType()
		throws UnsupportedCommOperationException;
	/**
	 * <p>setBaudBase.</p>
	 *
	 * @param BaudBase a int.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 * @throws java.io.IOException if any.
	 */
	public abstract boolean setBaudBase(int BaudBase)
		throws UnsupportedCommOperationException,
		IOException;
	/**
	 * <p>getBaudBase.</p>
	 *
	 * @return a int.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 * @throws java.io.IOException if any.
	 */
	public abstract int getBaudBase()
		throws UnsupportedCommOperationException,
		IOException;
	/**
	 * <p>setDivisor.</p>
	 *
	 * @param Divisor a int.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 * @throws java.io.IOException if any.
	 */
	public abstract boolean setDivisor(int Divisor)
		throws UnsupportedCommOperationException,
		IOException;
	/**
	 * <p>getDivisor.</p>
	 *
	 * @return a int.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 * @throws java.io.IOException if any.
	 */
	public abstract int getDivisor()
		throws UnsupportedCommOperationException,
		IOException;
	/**
	 * <p>setLowLatency.</p>
	 *
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean setLowLatency()
		throws UnsupportedCommOperationException;
	/**
	 * <p>getLowLatency.</p>
	 *
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean getLowLatency()
		throws UnsupportedCommOperationException;
	/**
	 * <p>setCallOutHangup.</p>
	 *
	 * @param NoHup a boolean.
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean setCallOutHangup(boolean NoHup)
		throws UnsupportedCommOperationException;
	/**
	 * <p>getCallOutHangup.</p>
	 *
	 * @return a boolean.
	 * @throws gnu.io.UnsupportedCommOperationException if any.
	 */
	public abstract boolean getCallOutHangup()
		throws UnsupportedCommOperationException;
}
