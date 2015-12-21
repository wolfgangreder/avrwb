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

import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.Resetable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
public interface Device extends Resetable
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

  public Map<Integer, Register> getIOSpace();

  public ClockDomain getCPUDomain();

  public List<ClockDomain> getClockDomains();

}
