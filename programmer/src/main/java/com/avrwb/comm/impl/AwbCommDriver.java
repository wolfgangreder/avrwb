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
package com.avrwb.comm.impl;

import com.avrwb.avr8.helper.Utils;
import com.avrwb.comm.CommDriver;
import com.avrwb.comm.CommPort;
import com.avrwb.comm.CommPortIdentifier;
import com.avrwb.comm.PortType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wolfi
 */
public final class AwbCommDriver implements CommDriver
{

  private static final class InstanceHolder
  {

    private static final AwbCommDriver instance = new AwbCommDriver();
  }
  private final Object lock = new Object();
  private final Map<String, Map<PortType, CommPortIdentifier>> openedPorts = new HashMap<>();

  public static AwbCommDriver getInstance()
  {
    return InstanceHolder.instance;
  }

  private AwbCommDriver()
  {
    initialize();
  }

  @Override
  public final void initialize()
  {
    System.load(Utils.findNativeLibrary("rxtxSerial"));
  }

  @Override
  public CommPort getCommPort(String portName,
                              PortType portType)
  {
    throw new UnsupportedOperationException();
  }

  public Object getLock()
  {
    return lock;
  }

}
