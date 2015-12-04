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
package com.avrwb.avr8.impl;

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NullAllowed;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Memory;
import com.avrwb.avr8.MemoryBuilder;
import com.avrwb.avr8.Module;
import com.avrwb.avr8.ModuleBuilderFactory;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.Variant;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.AvrDeviceKey;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.ModuleKey;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlAddressSpace;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolfgang Reder
 */
final class DeviceImpl implements Device
{

  @SuppressWarnings("NonConstantLogger")
  private final Logger deviceLogger;
  private final String name;
  private final AvrDeviceKey deviceKey;
  private final double voltageMin;
  private final double voltageMax;
  private final long speedMax;
  private final List<Memory> memories;
  private final List<Module> modules;
  private final CPU cpu;
  private final Memory flash;
  private final SRAM sram;
  private final Stack stack;
  private Object instructionContext;

  DeviceImpl(@NotNull XmlDevice device,
             Variant variant,
             @NotNull NotFoundStrategy nfStrategy,
             @NullAllowed Logger deviceLogger) throws ItemNotFoundException, NullPointerException
  {
    this.deviceLogger = deviceLogger != null ? deviceLogger : createDeviceLogger(device.getName());
    Objects.requireNonNull(device,
                           "device==null");
    Objects.requireNonNull(nfStrategy,
                           "strategy==null");
    if (variant != null) {
      voltageMax = variant.getVccMax();
      voltageMin = variant.getVccMin();
      speedMax = variant.getSpeedMax();
    } else {
      voltageMax = AVRWBDefaults.VOLTAGE_MAX;
      voltageMin = AVRWBDefaults.VOLTAGE_MIN;
      speedMax = AVRWBDefaults.SPEED_MAX;
    }
    name = device.getName();
    List<Module> tmpModules = initModules(device,
                                          nfStrategy);
    if (tmpModules.isEmpty()) {
      modules = Collections.emptyList();
      cpu = null;
    } else {
      CPU tmpCpu = null;
      modules = Collections.unmodifiableList(tmpModules);
      for (Module m : modules) {
        if (AVRWBDefaults.MODULENAME_CPU.equals(m.getName()) && m instanceof CPU) {
          tmpCpu = (CPU) m;
        }
      }
      cpu = tmpCpu;
    }
    List<Memory> tmpMemories = initMemories(device,
                                            nfStrategy);
    Memory tmpFlash = null;
    SRAM tmpSRAM = null;
    if (tmpMemories.isEmpty()) {
      memories = Collections.emptyList();
    } else {
      memories = Collections.unmodifiableList(tmpMemories);
      for (Memory m : memories) {
        if (AVRWBDefaults.MEMID_FLASH.equals(m.getId())) {
          tmpFlash = m;
          m.initialize(0xff);
        } else if (AVRWBDefaults.MEMID_SRAM.equals(m.getId())) {
          tmpSRAM = (SRAM) m;
        }
      }
    }
    flash = tmpFlash;
    sram = tmpSRAM;
    stack = new MemoryStack(cpu.getStackPointer(),
                            sram);
    deviceKey = new AvrDeviceKey(device.getFamily(),
                                 device.getAvrCore(),
                                 device.getName());
  }

  private Logger createDeviceLogger(String deviceName)
  {
    Logger result = Logger.getLogger(AVRWBDefaults.LOGGER.getName() + ".dev." + deviceName);
    String levelProperty = System.getProperty(result.getName() + ".level");
    if (levelProperty != null) {
      try {
        Level newLevel = Level.parse(levelProperty);
        result.setLevel(newLevel);
      } catch (IllegalArgumentException ex) {
      }
    }
    return result;
  }

  @Override
  public Logger getLogger()
  {
    return deviceLogger;
  }

  private static List<Module> initModules(XmlDevice device,
                                          NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    ModuleKey baseKey = new ModuleKey("",
                                      device.getAvrCore(),
                                      device.getFamily(),
                                      ModuleClass.OTHER);
    final ModuleResolver moduleResolver = ModuleResolver.getInstance();
    final List<Module> result = new ArrayList<>();
    for (XmlModule mod : device.getModules().getModule()) {
      ModuleKey currentKey = baseKey.withNameAndClass(mod.getName(),
                                                      mod.getClazz());
      ModuleBuilderFactory factory = moduleResolver.findModuleBuilder(currentKey);
      if (factory != null) {
        result.add(factory.createBuilder().
                moduleDescriptor(mod).
                device(device).
                notFoundStrategy(nfStrategy).
                build());
      } else {
        ItemNotFoundException.processItemNotFound(device.getName(),
                                                  mod.getName(),
                                                  nfStrategy);
      }
    }
    return result;
  }

  private static List<Memory> initMemories(XmlDevice device,
                                           NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    MemoryBuilder builder = InstanceFactories.getMemoryBuilder();
    List<Memory> result = new ArrayList<>();
    for (XmlAddressSpace space : device.getAddressSpaces().getAddressSpace()) {
      result.add(builder.fromAddressSpace(space).build());
    }
    return result;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public AvrDeviceKey getDeviceKey()
  {
    return deviceKey;
  }

  @Override
  public double getVoltageMin()
  {
    return voltageMin;
  }

  @Override
  public double getVoltageMax()
  {
    return voltageMax;
  }

  @Override
  public long getSpeedMax()
  {
    return speedMax;
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

  @Override
  public Stack getStack()
  {
    return stack;
  }

  @Override
  public void onClock(ClockState clockState,
                      Device device) throws SimulationException
  {
    for (Module mod : modules) {
      mod.onClock(clockState,
                  this);
    }
    for (Memory mem : memories) {
      mem.onClock(clockState,
                  this);
    }
  }

  @Override
  public String toString()
  {
    return "Device{" + "name=" + name + '}';
  }

}
