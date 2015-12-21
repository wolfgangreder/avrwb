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
package com.avrwb.avr8.impl.instructions.helper;

import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.MemoryBuilder;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.impl.MemoryBuilderImpl;
import com.avrwb.avr8.impl.MemoryStack;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.AvrFamily;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.avrwb.avr8.Module;

/**
 *
 * @author wolfi
 */
public class DummyDevice implements Device
{

  private final CPU cpu;
  private final Memory flash;
  private final SRAM sram;
  private final List<Module> modules;
  private final List<Memory> memories;
  private final Stack stack;
  private final AvrDeviceKey deviceKey;

  public DummyDevice(CPU cpu)
  {
    this(cpu,
         32,
         1024);
  }

  public DummyDevice(CPU cpu,
                     int sramSize,
                     int flashSize)
  {
    this.cpu = cpu;
    modules = Collections.singletonList(cpu);
    MemoryBuilder builder = new MemoryBuilderImpl().endianess(ByteOrder.LITTLE_ENDIAN).start(0);
    flash = builder.id("prog").name("prog").size(flashSize).build();
    sram = (SRAM) builder.id("data").name("data").size(sramSize).build();
    memories = Collections.unmodifiableList(Arrays.asList(flash,
                                                          sram));
    stack = new MemoryStack(cpu.getStackPointer(),
                            sram);
    deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                 AvrCore.V1,
                                 "dummy");
  }

  public DummyDevice(CPU cpu,
                     Memory flash,
                     SRAM sram,
                     Collection<? extends Module> extraModules,
                     Collection<? extends Memory> extraMemories)

  {
    this.cpu = cpu;
    this.flash = flash;
    this.sram = sram;
    if (extraModules == null || extraModules.isEmpty()) {
      this.modules = Collections.singletonList(cpu);
    } else {
      List<Module> tmp = new ArrayList<>(extraModules);
      tmp.add(cpu);
      this.modules = Collections.unmodifiableList(tmp);
    }
    if (extraMemories == null || extraMemories.isEmpty()) {
      this.memories = Collections.unmodifiableList(Arrays.asList(flash,
                                                                 sram));
    } else {
      List<Memory> tmp = new ArrayList<>(extraMemories);
      tmp.add(flash);
      tmp.add(sram);
      this.memories = Collections.unmodifiableList(tmp);
    }
    stack = new MemoryStack(cpu.getStackPointer(),
                            sram);
    deviceKey = new AvrDeviceKey(AvrFamily.TINY,
                                 AvrCore.V1,
                                 "dummy");
  }

  @Override
  public Logger getLogger()
  {
    return Logger.getGlobal();
  }

  @Override
  public String getName()
  {
    return "dummy";
  }

  @Override
  public AvrDeviceKey getDeviceKey()
  {
    return deviceKey;
  }

  @Override
  public double getVoltageMin()
  {
    return 2.7;
  }

  @Override
  public double getVoltageMax()
  {
    return 5;
  }

  @Override
  public long getSpeedMax()
  {
    return 12_000_000;
  }

  @Override
  public List<Memory> getMemories()
  {
    return memories;
  }

  @Override
  public List<Module> getModules()
  {
    return modules;
  }

  @Override
  public CPU getCPU()
  {
    return cpu;
  }

  @Override
  public Memory getFlash()
  {
    return flash;
  }

  @Override
  public SRAM getSRAM()
  {
    return sram;
  }

  @Override
  public Stack getStack()
  {
    return stack;
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    for (Module mod : modules) {
      mod.reset(ctx,
                source);
    }
  }

  @Override
  public Map<Integer, Register> getIOSpace()
  {
    return Collections.emptyMap();
  }

  @Override
  public ClockDomain getCPUDomain()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<ClockDomain> getClockDomains()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
