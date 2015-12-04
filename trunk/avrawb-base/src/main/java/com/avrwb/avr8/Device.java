/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
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
package com.avrwb.avr8;

import com.avrwb.avr8.api.ClockSink;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.SimulationException;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
public interface Device extends ClockSink
{

  public Logger getLogger();

  public String getName();

  public AvrDeviceKey getDeviceKey();

  public double getVoltageMin();

  public double getVoltageMax();

  public long getSpeedMax();

  public List<Memory> getMemories();

  public List<Module> getModules();

  public CPU getCPU();

  public Memory getFlash();

  public SRAM getSRAM();

  public Stack getStack();

  public void reset(ResetSource source) throws SimulationException;

}
