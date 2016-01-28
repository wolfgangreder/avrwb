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

import java.util.Objects;

/**
 * A serial port event.
 *
 * @author Jagane Sundar
 * @author wolfi
 */
public class SerialPortEvent
{

  private final SerialPort port;
  private final SerialPortEventType type;
  private final boolean oldValue;
  private final boolean newValue;

  /**
   * Constructs a SerialPortEvent with the specified serial port, event type, old and new values. Application programs should not
   * directly create SerialPortEvent objects.
   *
   * @param srcport port
   * @param eventtype event
   * @param oldvalue old
   * @param newvalue new
   */
  public SerialPortEvent(SerialPort srcport,
                         SerialPortEventType eventtype,
                         boolean oldvalue,
                         boolean newvalue)
  {
    this.port = Objects.requireNonNull(srcport,
                                       "srcport==null");
    this.type = Objects.requireNonNull(eventtype,
                                       "eventtype==null");
    this.oldValue = oldvalue;
    this.newValue = newvalue;
  }

  /**
   * Gets the type of this event.
   *
   * @return eventtype
   * @since CommAPI 1.1
   */
  public SerialPortEventType getEventType()
  {
    return type;
  }

  /**
   * The port of event.
   *
   * @return port
   */
  public SerialPort getPort()
  {
    return port;
  }

  /**
   * Gets the old value of the state change that caused the SerialPortEvent to be propagated. For example, when the CD bit
   * changes, oldValue reflects the old value of the CD bit.
   *
   * @return oldValue
   */
  public boolean getOldValue()
  {
    return oldValue;
  }

  /**
   * Gets the new value of the state change that caused the SerialPortEvent to be propagated. For example, when the CD bit
   * changes, newValue reflects the new value of the CD bit.
   *
   * @return newValue
   */
  public boolean getNewValue()
  {
    return newValue;
  }

}
