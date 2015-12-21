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
import com.avrwb.avr8.ModuleBuilderFactory;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.Stack;
import com.avrwb.avr8.Variant;
import com.avrwb.avr8.api.AVRWBDefaults;
import com.avrwb.avr8.api.AvrDeviceKey;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.ClockDomainFactory;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.api.NotFoundStrategy;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.helper.ModuleKey;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlAddressSpace;
import com.avrwb.schema.XmlClockDomain;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.lookup.Lookups;
import com.avrwb.avr8.Module;

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
  private final Map<Integer, Register> ioSpace;
  private final List<ClockDomain> clockDomains;
  private final ClockDomain cpuDomain;

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
    List<Module> tmpModules = initModules(device,
                                          nfStrategy,
                                          sram);
    if (tmpModules.isEmpty()) {
      modules = Collections.emptyList();
      cpu = null;
      ioSpace = Collections.emptyMap();
    } else {
      Map<Integer, List<Register>> tmpIOSpace = new HashMap<>();
      CPU tmpCpu = null;
      modules = Collections.unmodifiableList(tmpModules);
      for (Module m : modules) {
        if (AVRWBDefaults.MODULENAME_CPU.equals(m.getName()) && m instanceof CPU) {
          tmpCpu = (CPU) m;
        }
        for (Register r : m.getRegister().values()) {
          if (r.getIOAddress() != -1) {
            tmpIOSpace.computeIfAbsent(r.getIOAddress(),
                                       (Integer ioa) -> new LinkedList<>()).add(r);
          }
        }
      }
      cpu = tmpCpu;
      Map<Integer, Register> tmpIORegister = new HashMap<>();
      for (Map.Entry<Integer, List<Register>> e : tmpIOSpace.entrySet()) {
        if (e.getValue() != null && e.getKey() != null && !e.getValue().isEmpty()) {
          if (e.getValue().size() == 1) {
            tmpIORegister.put(e.getKey(),
                              e.getValue().get(0));
          } else {
            tmpIORegister.put(e.getKey(),
                              new ProxyRegister(e.getValue()));
          }
        }
      }
      ioSpace = Collections.unmodifiableMap(tmpIORegister);
    }
    stack = new MemoryStack(cpu.getStackPointer(),
                            sram);
    deviceKey = new AvrDeviceKey(device.getFamily(),
                                 device.getAvrCore(),
                                 device.getName());
    this.clockDomains = createClockDomains(device,
                                           nfStrategy);
    ClockDomain cd = null;
    for (ClockDomain c : clockDomains) {
      if (!"WD".equals(c.getId())) {
        cd = c;
        break;
      }
    }
    cpuDomain = cd;
    for (Module m : modules) {
      cpuDomain.addClockSink(m);
    }
  }

  private List<ClockDomain> createClockDomains(XmlDevice device,
                                               NotFoundStrategy nfs) throws ItemNotFoundException
  {
    List<ClockDomain> tmpResult = new LinkedList<>();
    Map<String, ClockDomainFactory> domainFactories = new HashMap<>();
    for (ClockDomainFactory cdf : Lookups.forPath("avrwb").lookupAll(ClockDomainFactory.class)) {
      domainFactories.put(cdf.getImplementationId(),
                          cdf);
    }
    for (XmlClockDomain xcd : device.getClockDomains().getClockDomain()) {
      ClockDomainFactory cdf = domainFactories.get(xcd.getImplementation());
      if (cdf == null) {
        ItemNotFoundException.processItemNotFound(name,
                                                  xcd.getImplementation(),
                                                  nfs);
        continue;
      }
      tmpResult.add(cdf.createDomain(xcd));
    }
    return tmpResult;
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
                                          NotFoundStrategy nfStrategy,
                                          SRAM sram) throws ItemNotFoundException
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
                sram(sram).
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
  public ClockDomain getCPUDomain()
  {
    return cpuDomain;
  }

  @Override
  public List<ClockDomain> getClockDomains()
  {
    return clockDomains;
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
  public Stack getStack()
  {
    return stack;
  }

  @Override
  public Map<Integer, Register> getIOSpace()
  {
    return ioSpace;
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    for (Module m : modules) {
      m.reset(ctx,
              source);
    }
  }

  @Override
  public String toString()
  {
    return "Device{" + "name=" + name + '}';
  }

}
