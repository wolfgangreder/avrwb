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

/**
 *
 * @author wolfi
 */
public enum SerialPortEventType
{
  /**
   * Break interrupt.
   */
  BI,
  /**
   * Carrier detect.
   */
  CD,
  /**
   * Clear to send.
   */
  CTS,
  /**
   * Data available at the serial port. This event will be generated once when new data arrive at the serial port. Even if the
   * user doesn't read the data, it won't be generated again until next time new data arrive.
   */
  DATA_AVAILABLE,
  /**
   * Data set ready.
   */
  DSR,
  /**
   * Framing error.
   */
  FE,
  /**
   * Overrun error.
   */
  OE,
  /**
   * Output buffer is empty. The event will be generated after a write is completed, when the system buffer becomes empty again.
   */
  OUTPUT_BUFFER_EMPTY,
  /**
   * Parity error.
   */
  PE,
  /**
   * Parity error.
   */
  RI;
}
