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
 * Part of the loadable device driver interface. {@code CommDriver} should not be used by application-level programs.
 *
 * @author Jagane Sundar
 * @author wolfi
 */
public interface CommDriver
{

  /**
   * initialize() will be called by the CommPortIdentifier's static initializer. The responsibility of this method is: 1) Ensure
   * that that the hardware is present. 2) Load any required native libraries. 3) Register the port names with the
   * CommPortIdentifier.
   */
  public void initialize();

  /**
   * getCommPort() will be called by CommPortIdentifier.open() portName is a string that was registered earlier using the
   * CommPortIdentifier.addPortName() method. getCommPort() returns an object that extends either SerialPort or ParallelPort.
   *
   * @param portName name of port
   * @param portType type of port
   * @return commPort
   */
  public CommPort getCommPort(String portName,
                              PortType portType);

}
