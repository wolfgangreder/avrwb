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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.avrwb.avr8.Architecture;
import at.reder.avrwb.avr8.CPU;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Family;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.MemoryBuilder;
import at.reder.avrwb.avr8.Module;
import at.reder.avrwb.avr8.ResetSource;
import at.reder.avrwb.avr8.helper.SimulationException;
import at.reder.avrwb.avr8.impl.MemoryBuilderImpl;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author wolfi
 */
public class DummyDevice implements Device
{

  private final CPU cpu;
  private final Memory flash;
  private final Memory sram;
  private final List<Module> modules;
  private final List<Memory> memories;

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
    sram = builder.id("data").name("data").size(sramSize).build();
    memories = Collections.unmodifiableList(Arrays.asList(flash,
                                                          sram));
  }

  public DummyDevice(CPU cpu,
                     Memory flash,
                     Memory sram,
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
  public Architecture getArchitecture()
  {
    return Architecture.AVR8;
  }

  @Override
  public Family getFamily()
  {
    return Family.megaAVR;
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
  public Memory getSRAM()
  {
    return sram;
  }

  @Override
  public void reset(ResetSource source) throws SimulationException
  {
    for (Memory mem : memories) {
      mem.reset(this,
                source);
    }
    for (Module mod : modules) {
      mod.reset(this,
                source);
    }
  }

}
