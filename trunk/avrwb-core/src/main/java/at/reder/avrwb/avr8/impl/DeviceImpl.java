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
package at.reder.avrwb.avr8.impl;

import at.reder.atmelschema.ModuleVector;
import at.reder.atmelschema.XA_AddressSpace;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Device;
import at.reder.atmelschema.XA_DeviceModule;
import at.reder.atmelschema.XA_Module;
import at.reder.atmelschema.XA_Variant;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NullAllowed;
import at.reder.avrwb.avr8.AVRCoreVersion;
import at.reder.avrwb.avr8.AVRDeviceKey;
import at.reder.avrwb.avr8.CPU;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.MemoryBuilder;
import at.reder.avrwb.avr8.Module;
import at.reder.avrwb.avr8.ModuleBuilderFactory;
import at.reder.avrwb.avr8.ResetSource;
import at.reder.avrwb.avr8.SRAM;
import at.reder.avrwb.avr8.Stack;
import at.reder.avrwb.avr8.api.InstanceFactories;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.ItemNotFoundException;
import at.reder.avrwb.avr8.helper.ModuleKey;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import at.reder.avrwb.avr8.helper.SimulationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author Wolfgang Reder
 */
final class DeviceImpl implements Device
{

  @SuppressWarnings("NonConstantLogger")
  private final Logger deviceLogger;
  private final String name;
  private final AVRDeviceKey deviceKey;
  private final double voltageMin;
  private final double voltageMax;
  private final long speedMax;
  private final List<Memory> memories;
  private final List<Module> modules;
  private final CPU cpu;
  private final Memory flash;
  private final SRAM sram;
  private final Stack stack;

  DeviceImpl(@NotNull XA_AvrToolsDeviceFile file,
             XA_Variant variant,
             @NotNull XA_Device device,
             @NotNull NotFoundStrategy nfStrategy,
             @NullAllowed Logger deviceLogger) throws ItemNotFoundException, NullPointerException
  {
    this.deviceLogger = deviceLogger != null ? deviceLogger : createDeviceLogger(device.getName());
    Objects.requireNonNull(file,
                           "file==null");
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
    List<Module> tmpModules = initModules(file,
                                          device,
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
    List<Memory> tmpMemories = initMemories(file,
                                            device,
                                            nfStrategy);
    Memory tmpFlash = null;
    SRAM tmpSRAM = null;
    if (tmpMemories.isEmpty()) {
      memories = Collections.emptyList();
    } else {
      memories = Collections.unmodifiableList(tmpMemories);
      for (Memory m : memories) {
        if (AVRWBDefaults.MEMNAME_FLASH.equals(m.getId())) {
          tmpFlash = m;
          m.initialize(0xff);
        } else if (AVRWBDefaults.MEMNAME_SRAM.equals(m.getId())) {
          tmpSRAM = (SRAM) m;
        }
      }
    }
    flash = tmpFlash;
    sram = tmpSRAM;
    if (!"AT90S1200".equals(name)) {
      stack = new MemoryStack(cpu.getStackPointer(),
                              sram);
    } else {
      stack = new HardwareStack();
    }
    deviceKey = new AVRDeviceKey(device.getFamily(),
                                 device.getArchitecture(),
                                 cpu.getCoreVersion(),
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

  private static AVRCoreVersion extractCoreVersion(XA_AvrToolsDeviceFile file,
                                                   String deviceName) throws ItemNotFoundException
  {
    XA_Module modCPU = file.findModule(new ModuleVector(deviceName,
                                                        "CPU"));
    AVRCoreVersion result = AVRCoreVersion.V1;
    if (modCPU != null) {
      String strProp = modCPU.getParameter().get(AVRWBDefaults.PROP_CORE_VERSION);
      if (strProp != null) {
        try {
          result = AVRCoreVersion.valueOf(strProp);
        } catch (IllegalArgumentException ex) {
          Exceptions.printStackTrace(ex);
          throw new ItemNotFoundException("cannot parse core version \"" + strProp + "\"");
        }
      }
    }
    return result;
  }

  private static List<Module> initModules(XA_AvrToolsDeviceFile file,
                                          XA_Device device,
                                          NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    final AVRCoreVersion version = extractCoreVersion(file,
                                                      device.getName());
    final ModuleKey baseKey = new ModuleKey(".",
                                            version,
                                            device.getArchitecture());
    final ModuleResolver moduleResolver = ModuleResolver.getInstance();
    final List<Module> result = new ArrayList<>();

    for (XA_DeviceModule moduleDescriptor : device.getModules()) {
      ModuleVector mv = new ModuleVector(device.getName(),
                                         moduleDescriptor.getName());
      XA_Module module = file.findModule(mv);
      if (module != null) {
        ModuleKey currentKey = baseKey.withName(moduleDescriptor.getName());
        ModuleBuilderFactory factory = moduleResolver.findModuleBuilder(currentKey);
        if (factory != null) {
          result.add(factory.createBuilder().
                  descriptor(file).
                  moduleDescriptor(module).
                  moduleVector(mv).
                  notFoundStrategy(nfStrategy).
                  build());
        } else {
          ItemNotFoundException.processItemNotFound(device.getName(),
                                                    moduleDescriptor.getName(),
                                                    nfStrategy);
        }
      } else {
        ItemNotFoundException.processItemNotFound(device.getName(),
                                                  moduleDescriptor.getName(),
                                                  nfStrategy);
      }
    }
    return result;
  }

  private static List<Memory> initMemories(XA_AvrToolsDeviceFile file,
                                           XA_Device device,
                                           NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    MemoryBuilder builder = InstanceFactories.getMemoryBuilder();
    List<Memory> result = new ArrayList<>();
    for (XA_AddressSpace space : device.getAdressSpaces()) {
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
  public AVRDeviceKey getDeviceKey()
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
  public String toString()
  {
    return "Device{" + "name=" + name + '}';
  }

}
