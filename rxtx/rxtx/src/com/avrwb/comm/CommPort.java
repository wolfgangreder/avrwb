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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * <p>
 * A communications port. {@code CommPort} is an abstract class that describes a communications port made available by the
 * underlying system. It includes high-level methods for controlling I/O that are common to different kinds of communications
 * ports. {@code SerialPort} and {@code ParallelPort} are subclasses of {@code CommPort} that include additional methods for
 * low-level control of physical communications ports.</p>
 *
 * <p>
 * There are no public constructors for {@code CommPort}. Instead an application should use the static method
 * {@code CommPortIdentifier.getPortIdentifiers} to generate a list of available ports. It then chooses a port from this list and
 * calls {@code CommPortIdentifier.open} to create a {@code CommPort} object. Finally, it casts the {@code CommPort} object to a
 * physical communications device class like {@code SerialPort} or {@code ParallelPort}.</p>
 *
 * <p>
 * After a communications port has been identified and opened it can be configured with the methods in the low-level classes like
 * {@code SerialPort} and {@code ParallelPort}. Then an IO stream can be opend for reading and writing data. Once the application
 * is done with the port, it must call the close method. Thereafter the application must not call any methods in the port object.
 * Doing so will cause a {@code java.lang.IllegalStateException} to be thrown.</p>
 *
 * @author Jagane Sundar
 * @author wolfi
 */
public abstract class CommPort implements AutoCloseable
{

  protected final String name;

  protected CommPort(String name)
  {
    this.name = Objects.requireNonNull(name,
                                       "name==null");
  }

  /**
   * Gets the name of the communications port. This name should correspond to something the user can identify, like the label on
   * the hardware.
   *
   * @return name of the port
   */
  public final String getName()
  {
    return name;
  }

  /**
   * Returns an input stream. This is the only way to receive data from the communications port. If the port is unidirectional and
   * doesn't support receiving data, then {@code getInputStream} returns null.
   * <p>
   * The read behaviour of the input stream returned by {@code getInputStream} depends on combination of the threshold and timeout
   * values. The possible behaviours are described in the table below:
   * </p><p>
   * </p><table border="1">
   * <caption></caption>
   * <tbody>
   * <tr><th colspan="2">Threshold</th><th colspan="2">Timeout</th><th rowspan="2">Read Buffer Size</th><th rowspan="2">Read
   * Behaviour</th></tr>
   * <tr><th>State</th><th>Value</th><th>State</th><th>Value</th></tr>
   * <tr><td>disabled</td><td>-</td><td>disabled</td><td>-</td><td>n bytes</td><td>block until any data is available</td></tr>
   * <tr><td>enabled</td><td>m bytes</td><td>disabled</td><td>-</td><td>n bytes</td><td>block until min(<i>m</i>,<i>n</i>) bytes
   * are available</td></tr>
   * <tr><td>disabled</td><td>-</td><td>enabled</td><td>x ms</td><td>n bytes</td><td>block for <i>x</i> ms or until any data is
   * available</td></tr>
   * <tr><td>enabled</td><td>m bytes</td><td>enabled</td><td>x ms</td><td>n bytes</td><td>block for <i>x</i> ms or until
   * min(<i>m</i>,<i>n</i>) bytes are available</td></tr>
   * </tbody></table>
   * <p>
   * Note, however, that framing errors may cause the Timeout and Threshold values to complete prematurely without raising an
   * exception.
   * </p><p>
   * Enabling the Timeout OR Threshold with a value a zero is a special case. This causes the underlying driver to poll for
   * incoming data instead being event driven. Otherwise, the behaviour is identical to having both the Timeout and Threshold
   * disabled.
   * </p>
   *
   * @return InputStream object that can be used to read from the port
   * @throws IOException if an I/O error occurred
   */
  public abstract InputStream getInputStream() throws IOException;

  /**
   * Returns an output stream. This is the only way to send data to the communications port. If the port is unidirectional and
   * doesn't support sending data, then {@code getOutputStream} returns null.
   *
   * @return OutputStream object that can be used to write to the port
   * @throws IOException if an I/O error occurred
   */
  public abstract OutputStream getOutputStream() throws IOException;

  /**
   * Closes the communications port. The application must call {@code close} when it is done with the port. Notification of this
   * ownership change will be propagated to all classes registered using {@code addPortOwnershipListener}.
   *
   * @throws java.io.IOException
   */
  @Override
  public abstract void close() throws IOException;

  /**
   * <p>
   * Enables receive threshold, if this feature is supported by the driver. When the receive threshold condition becomes true, a
   * {@code read} from the input stream for this port will return immediately.</p>
   * <p>
   * {@code enableReceiveThreshold} is an advisory method which the driver may not implement. By default, receive threshold is not
   * enabled.</p>
   * <p>
   * An application can determine whether the driver supports this feature by first calling the {@code enableReceiveThreshold}
   * method and then calling the {@code isReceiveThresholdEnabled} method. If {@code isReceiveThresholdEnabled} still returns
   * false, then receive threshold is not supported by the driver. If the driver does not implement this feature, it will return
   * from blocking reads at an appropriate time.</p>
   *
   * See {@code getInputStream} for description of exact behaviour.
   *
   * @param thresh when this many bytes are in the input buffer, return immediately from {@code read}.
   * @throws UnsupportedCommOperationException is thrown if receive threshold is not supported by the underlying driver.
   * @see #getInputStream()
   */
  public abstract void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException;

  /**
   * Disables receive threshold.
   */
  public abstract void disableReceiveThreshold();

  /**
   * Checks if receive threshold is enabled.
   *
   * @return boolean true if the driver supports receive threshold.
   * @since CommAPI 1.1
   */
  public abstract boolean isReceiveThresholdEnabled();

  /**
   * Gets the integer value of the receive threshold. If the receive threshold is disabled or not supported by the driver, then
   * the value returned is meaningless.
   *
   * @return number of bytes for receive threshold
   */
  public abstract int getReceiveThreshold();

  /**
   * <p>
   * Enables receive timeout, if this feature is supported by the driver. When the receive timeout condition becomes true, a
   * {@code read} from the input stream for this port will return immediately.</p>
   *
   * <p>
   * {@code enableReceiveTimeout} is an advisory method which the driver may not implement. By default, receive timeout is not
   * enabled.</p>
   * <p>
   * An application can determine whether the driver supports this feature by first calling the {@code enableReceiveTimeout}
   * method and then calling the {@code isReceiveTimeout} method. If {@code isReceiveTimeout} still returns false, then receive
   * timeout is not supported by the driver.</p>
   *
   * See {@code getInputStream} for description of exact behaviour.
   *
   * @param rcvTimeout when this many milliseconds have elapsed, return immediately from {@code read}, regardless of bytes in
   * input buffer.
   * @throws UnsupportedCommOperationException is thrown if receive timeout is not supported by the underlying driver.
   * @see #getInputStream()
   */
  public abstract void enableReceiveTimeout(int rcvTimeout) throws UnsupportedCommOperationException;

  /**
   * Disables receive timeout.
   */
  public abstract void disableReceiveTimeout();

  /**
   * Checks if receive timeout is enabled.
   *
   * @return boolean true if the driver supports receive timeout.
   */
  public abstract boolean isReceiveTimeoutEnabled();

  /**
   * Gets the integer value of the receive timeout. If the receive timeout is disabled or not supported by the driver, then the
   * value returned is meaningless.
   *
   * @return number of milliseconds in receive timeout
   */
  public abstract int getReceiveTimeout();

  /**
   * Enables receive framing, if this feature is supported by the driver. When the receive framing condition becomes true, a
   * {@code read} from the input stream for this port will return immediately.
   * <p>
   * {@code enableReceiveFraming} is an advisory method which the driver may not implement. By default, receive framing is not
   * enabled.
   * </p><p>
   * An application can determine whether the driver supports this feature by first calling the {@code enableReceiveFraming}
   * method and then calling the {@code isReceiveFramingEnabled} method. If {@code isReceiveFramingEnabled} still returns false,
   * then receive framing is not supported by the driver.
   * </p><p>
   * Note: As implemented in this method, framing is <b>not</b> related to bit-level framing at the hardware level, and is
   * <b>not</b>
   * associated with data errors.
   * </p>
   *
   * @param framingByte this byte in the input stream suggests the end of the received frame. Blocked reads will return
   * immediately. Only the low 8 bits of {@code framingByte} are used while the upper 24 bits are masked off. A value outside the
   * range of 0-255 will be converted to the value of its lowest 8 bits.
   *
   * @throws UnsupportedCommOperationException is thrown if receive timeout is not supported by the underlying driver.
   */
  public abstract void enableReceiveFraming(int framingByte) throws UnsupportedCommOperationException;

  /**
   * Disables receive framing.
   */
  public abstract void disableReceiveFraming();

  /**
   * Checks if receive framing is enabled.
   *
   * @return boolean true if the driver supports receive framing.
   */
  public abstract boolean isReceiveFramingEnabled();

  /**
   * Gets the current byte used for receive framing. If the receive framing is disabled or not supported by the driver, then the
   * value returned is meaningless. The return value of {@code getReceiveFramingByte} is an integer, the low 8 bits of which
   * represent the current byte used for receive framing.
   * <p>
   * Note: As implemented in this method, framing is <b>not</b> related to bit-level framing at the hardware level, and is
   * <b>not</b>
   * associated with data errors.
   * </p>
   *
   * @return integer current byte used for receive framing
   */
  public abstract int getReceiveFramingByte();

  /**
   * Sets the input buffer size. Note that this is advisory and memory availability may determine the ultimate buffer size used by
   * the driver.
   *
   * @param size size of the input buffer
   */
  public abstract void setInputBufferSize(int size);

  /**
   * Gets the input buffer size. Note that this method is advisory and the underlying OS may choose not to report correct values
   * for the buffer size.
   *
   * @return input buffer size currently in use
   */
  public abstract int getInputBufferSize();

  /**
   * Sets the output buffer size. Note that this is advisory and memory availability may determine the ultimate buffer size used
   * by the driver.
   *
   * @param size size of the output buffer
   */
  public abstract void setOutputBufferSize(int size);

  /**
   * Gets the output buffer size. Note that this method is advisory and the underlying OS may choose not to report correct values
   * for the buffer size.
   *
   * @return output buffer size currently in use
   */
  public abstract int getOutputBufferSize();

}
