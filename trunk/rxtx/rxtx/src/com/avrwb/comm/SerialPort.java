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
package com.avrwb.comm;

import java.io.IOException;
import java.util.Set;

/**
 * An RS-232 serial communications port. {@code SerialPort} describes the low-level interface to a serial communications port made
 * available by the underlying system. {@code SerialPort} defines the minimum required functionality for serial communications
 * ports.
 *
 * @author Jagane Sundar
 * @author wolfi
 * @see CommPort
 * @see CommPortIdentifier
 */
public abstract class SerialPort extends CommPort
{

  protected SerialPort(String name)
  {
    super(name);
  }

  /**
   * Gets the currently configured baud rate.
   *
   * @return integer value indicating the baud rate
   */
  public abstract int getBaudRate();

  /**
   * Gets the currently configured number of data bits.
   *
   * @return dataBits
   */
  public abstract DataBits getDataBits();

  /**
   * Gets the currently defined stop bits.
   *
   * @return stopBits
   */
  public abstract StopBits getStopBits();

  /**
   * Get the currently configured parity setting.
   *
   * @return parity
   */
  public abstract Parity getParity();

  /**
   * Sends a break of {@code millis} milliseconds duration. Note that it may not be possible to time the duration of the break
   * under certain Operating Systems. Hence this parameter is advisory.
   *
   * @param millis duration of break to send
   */
  public abstract void sendBreak(int millis);

  /**
   * Sets the flow control mode.
   *
   * @param flowcontrol flowControl
   * @throws UnsupportedCommOperationException if any of the flow control mode was not supported by the underline OS, or if input
   * and output flow control are set to different values, i.e. one hardware and one software. The flow control mode will revert to
   * the value before the call was made.
   */
  public abstract void setFlowControlMode(Set<FlowControl> flowcontrol)
          throws UnsupportedCommOperationException;

  /**
   * Gets the currently configured flow control mode.
   *
   * @return flowControl
   */
  public abstract Set<FlowControl> getFlowControlMode();

  /**
   * Sets serial port parameters.
   *
   * @param baudrate If the baudrate passed in by the application is unsupported by the driver, the driver will throw an
   * UnsupportedCommOperationException.
   * @param dataBits dataBits
   * @param stopBits stopBits
   * @param parity parity
   * @throws UnsupportedCommOperationException if any of the above parameters are specified incorrectly. All four of the
   * parameters will revert to the values before the call was made.
   * <p>
   * DEFAULT: 9600 baud, 8 data bits, 1 stop bit, no parity</p>
   * @throws java.io.IOException
   */
  public abstract void setSerialPortParams(int baudrate,
                                           DataBits dataBits,
                                           StopBits stopBits,
                                           Parity parity)
          throws UnsupportedCommOperationException, IOException;

  /**
   * Sets or clears the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation.
   *
   * @param dtr <ul>
   * <li>
   * true:	set DTR
   * </li><li>
   * false:	clear DTR
   * </li></ul>
   */
  public abstract void setDTR(boolean dtr);

  /**
   * Gets the state of the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation.
   *
   * @return dtr
   */
  public abstract boolean isDTR();

  /**
   * Sets or clears the RTS (Request To Send) bit in the UART, if supported by the underlying implementation.
   *
   * @param rts rts
   */
  public abstract void setRTS(boolean rts);

  /**
   * Gets the state of the RTS (Request To Send) bit in the UART, if supported by the underlying implementation.
   *
   * @return rts
   */
  public abstract boolean isRTS();

  /**
   * Gets the state of the CTS (Clear To Send) bit in the UART, if supported by the underlying implementation.
   *
   * @return cts
   */
  public abstract boolean isCTS();

  /**
   * Gets the state of the DSR (Data Set Ready) bit in the UART, if supported by the underlying implementation.
   *
   * @return dsr
   */
  public abstract boolean isDSR();

  /**
   * Gets the state of the RI (Ring Indicator) bit in the UART, if supported by the underlying implementation.
   *
   * @return ri
   */
  public abstract boolean isRI();

  /**
   * Gets the state of the CD (Carrier Detect) bit in the UART, if supported by the underlying implementation.
   *
   * @return cd
   */
  public abstract boolean isCD();

  /**
   * Registers a {@code SerialPortEventListener} object to listen for {@code SerialEvent}s. Interest in specific events may be
   * expressed using the {@code notifyOn<i>XXX</i>} calls. The {@code serialEvent} method of {@code SerialPortEventListener} will
   * be called with a {@code SerialEvent} object describing the event.
   * <p>
   * The current implementation only allows one listener per {@code SerialPort}. Once a listener is registered, subsequent call
   * attempts to {@code addEventListener} will throw a TooManyListenersException without effecting the listener already
   * registered.
   * </p><p>
   * All the events received by this listener are generated by one dedicated thread that belongs to the SerialPort object. After
   * the port is closed, no more event will be generated. Another call to {@code open()} of the port's {@code CommPortIdentifier}
   * object will return a new {@code CommPort} object, and the lsnr has to be added again to the new {@code CommPort} object to
   * receive event from this port.
   * </p>
   *
   * @param lsnr The SerialPortEventListener object whose serialEvent method will be called with a SerialEvent describing the
   * event.
   * @throws java.util.TooManyListenersException If an initial attempt to attach a listener succeeds, subsequent attempts will
   * throw TooManyListenersException without effecting the first listener.
   */
  public abstract void addEventListener(SerialPortEventListener lsnr)
          throws java.util.TooManyListenersException;

  /**
   * <p>
   * Deregisters event listener registered using addEventListener.</p>
   * <p>
   * This is done automatically at port close. </p>
   */
  public abstract void removeEventListener();

  /**
   * Expresses interest in receiving notification when input data is available. This may be used to drive asynchronous input. When
   * data is available in the input buffer, this event is propagated to the listener registered using {@code addEventListener}.
   * <p>
   * The event will be generated once when new data arrive at the serial port. Even if the user doesn't read the data, it won't be
   * generated again until next time new data arrive.
   * </p>
   *
   * @param enable enable
   */
  public abstract void notifyOnDataAvailable(boolean enable);

  /**
   *
   * Expresses interest in receiving notification when the output buffer is empty. This may be used to drive asynchronous output.
   * When the output buffer becomes empty, this event is propagated to the listener registered using
   * <code>addEventListener</code>.
   *
   * The event will be generated after a write is completed, when the system buffer becomes empty again.
   *
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.
   * </p>
   *
   * @param enable en
   */
  public abstract void notifyOnOutputEmpty(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when the CTS (Clear To Send) bit changes.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnCTS(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when the DSR (Data Set Ready) bit changes.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnDSR(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when the RI (Ring Indicator) bit changes.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations. </p>
   *
   * @param enable en
   */
  public abstract void notifyOnRingIndicator(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when the CD (Carrier Detect) bit changes.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnCarrierDetect(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when there is an overrun error.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnOverrunError(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when there is a parity error.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnParityError(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when there is a framing error.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnFramingError(boolean enable);

  /**
   * <p>
   * Expresses interest in receiving notification when there is a break interrupt on the line.</p>
   * <p>
   * This notification is hardware dependent and may not be supported by all implementations.</p>
   *
   * @param enable en
   */
  public abstract void notifyOnBreakInterrupt(boolean enable);

}
