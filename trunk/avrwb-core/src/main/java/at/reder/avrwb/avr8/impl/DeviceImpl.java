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
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Device;
import at.reder.atmelschema.XA_DeviceModule;
import at.reder.atmelschema.XA_Module;
import at.reder.atmelschema.XA_Variant;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.avr8.AVRCoreVersion;
import at.reder.avrwb.avr8.Architecture;
import at.reder.avrwb.avr8.CPU;
import at.reder.avrwb.avr8.Device;
import at.reder.avrwb.avr8.Family;
import at.reder.avrwb.avr8.Memory;
import at.reder.avrwb.avr8.Module;
import at.reder.avrwb.avr8.ModuleBuilderFactory;
import at.reder.avrwb.avr8.helper.AVRWBDefaults;
import at.reder.avrwb.avr8.helper.ItemNotFoundException;
import at.reder.avrwb.avr8.helper.ModuleKey;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
final class DeviceImpl implements Device
{

  private final String name;
  private final Architecture architecture;
  private final Family family;
  private final double voltageMin;
  private final double voltageMax;
  private final long speedMax;
  private final List<Memory> memories;
  private final List<Module> modules;
  private final CPU cpu;
  private final Memory flash;

  DeviceImpl(@NotNull XA_AvrToolsDeviceFile file,
             XA_Variant variant,
             @NotNull XA_Device device,
             @NotNull NotFoundStrategy nfStrategy) throws ItemNotFoundException, NullPointerException
  {
    Objects.requireNonNull(file, "file==null");
    Objects.requireNonNull(device, "device==null");
    Objects.requireNonNull(nfStrategy, "strategy==null");
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
    architecture = device.getArchitecture();
    family = device.getFamily();
    List<Module> tmpModules = initModules(file, device, nfStrategy);
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
    memories = Collections.emptyList();
    flash = null;
  }

  private List<Module> initModules(XA_AvrToolsDeviceFile file,
                                   XA_Device device,
                                   NotFoundStrategy nfStrategy) throws ItemNotFoundException
  {
    final ModuleKey baseKey = new ModuleKey(".", AVRCoreVersion.ANY, architecture);
    final ModuleResolver moduleResolver = ModuleResolver.getInstance();
    final List<Module> result = new ArrayList<>();
    for (XA_DeviceModule moduleDescriptor : device.getModules()) {
      XA_Module module = file.findModule(new ModuleVector(name, moduleDescriptor.getName()));
      if (module != null) {
        ModuleKey currentKey = baseKey.withName(moduleDescriptor.getName());
        ModuleBuilderFactory factory = moduleResolver.findModuleBuilder(currentKey);
        if (factory != null) {
          result.add(factory.createBuilder().fromDescriptor(file, module).build());
        } else {
          ItemNotFoundException.processItemNotFound(device.getName(), moduleDescriptor.getName(), nfStrategy);
        }
      } else {
        ItemNotFoundException.processItemNotFound(device.getName(), moduleDescriptor.getName(), nfStrategy);
      }
    }
    return result;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Architecture getArchitecture()
  {
    return architecture;
  }

  @Override
  public Family getFamily()
  {
    return family;
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
  public String toString()
  {
    return "Device{" + "name=" + name + '}';
  }

}
