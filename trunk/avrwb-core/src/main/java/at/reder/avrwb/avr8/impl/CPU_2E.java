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
import at.reder.atmelschema.XA_Module;
import at.reder.atmelschema.XA_Register;
import at.reder.atmelschema.XA_RegisterGroup;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;
import at.reder.avrwb.avr8.CPU;
import at.reder.avrwb.avr8.Register;
import at.reder.avrwb.avr8.RegisterBuilder;
import at.reder.avrwb.avr8.ResetSource;
import at.reder.avrwb.avr8.api.ClockState;
import at.reder.avrwb.avr8.api.InstanceFactories;
import at.reder.avrwb.avr8.helper.ItemNotFoundException;
import at.reder.avrwb.avr8.helper.NotFoundStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public class CPU_2E implements CPU
{

  private final String name;
  private final String caption;
  private final Map<String, String> params;
  private int ip;
  private final Register sreg;
  private final Register sp;
  private final List<Register> register;

  CPU_2E(@NotNull XA_AvrToolsDeviceFile file,
         @NotNull ModuleVector moduleVector,
         XA_Module mod,
         @NotNull NotFoundStrategy nfStrategy) throws NullPointerException, ItemNotFoundException
  {
    Objects.requireNonNull(file, "file==null");
    Objects.requireNonNull(moduleVector, "moduleVector==null");
    Objects.requireNonNull(nfStrategy, "nfStrategy==null");
    XA_Module module = mod != null ? mod : file.findModule(moduleVector);
    if (module == null) {
      ItemNotFoundException.processItemNotFound(moduleVector.getDeviceName(),
                                                moduleVector.getModuleName(),
                                                NotFoundStrategy.ERROR);
      throw new NullPointerException("module==null");
    }
    this.name = module.getName();
    if (module.getCaption().trim().isEmpty()) {
      this.caption = this.name;
    } else {
      this.caption = module.getCaption();
    }
    if (module.getParameter().isEmpty()) {
      params = Collections.emptyMap();
    } else {
      params = Collections.unmodifiableMap(new HashMap<>(module.getParameter()));
    }
    final RegisterBuilder registerBuilder = InstanceFactories.getRegisterBuilder();
    final List<Register> tmpRegister = new ArrayList<>();
    Register tmpSREG = null;
    Register tmpSP = null;
    for (XA_RegisterGroup rg : module.getRegisterGroups()) {
      for (XA_Register r : rg.getRegister()) {
        Register reg = registerBuilder.fromDescritpor(file, moduleVector.withRegister(r.getName())).build();
        tmpRegister.add(reg);
        if ("SREG".equals(reg.getName())) {
          tmpSREG = reg;
        } else if ("SP".equals(reg.getName())) {
          tmpSP = reg;
        }
      }
    }
    this.register = Collections.unmodifiableList(tmpRegister);
    this.sreg = tmpSREG;
    this.sp = tmpSP;
    if (sreg == null) {
      ItemNotFoundException.processItemNotFound(moduleVector.getDeviceName(), "SREG", nfStrategy);
    }
    if (sp == null) {
      ItemNotFoundException.processItemNotFound(moduleVector.getDeviceName(), "SP", nfStrategy);
    }
  }

  @Override
  public int getIP()
  {
    return ip;
  }

  @Override
  public void setIP(int newIP) throws IllegalArgumentException
  {
    if (newIP < 0) {
      throw new IllegalArgumentException("ip<0");
    }
    this.ip = newIP;
  }

  @Override
  public Register getSREG()
  {
    return sreg;
  }

  @Override
  public Register getStackPointer()
  {
    return sp;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getCaption()
  {
    return caption;
  }

  @Override
  public Map<String, String> getParam()
  {
    return params;
  }

  @Override
  public List<Register> getRegister()
  {
    return register;
  }

  @Override
  public void reset(ResetSource source)
  {
    for (Register r : register) {
      r.setValue(0);
    }
  }

  @Override
  public void onClock(ClockState clockState)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
