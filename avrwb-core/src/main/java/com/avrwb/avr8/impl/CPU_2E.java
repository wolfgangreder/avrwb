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

import com.avrwb.atmelschema.ModuleVector;
import com.avrwb.atmelschema.XA_AvrToolsDeviceFile;
import com.avrwb.atmelschema.XA_Module;
import com.avrwb.atmelschema.XA_Register;
import com.avrwb.atmelschema.XA_RegisterGroup;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.AVRCoreVersion;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBuilder;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.InstanceFactories;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.InstructionResult;
import com.avrwb.avr8.helper.AVRWBDefaults;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.helper.SimulationException;
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
  private Instruction currentInstruction;
  private final SREG sreg;
  private final Register sp;
  private final List<Register> register;
  private final CPU_2EInstructionDecoder instructionDecoder;
  private final AVRCoreVersion version;

  CPU_2E(@NotNull XA_AvrToolsDeviceFile file,
         @NotNull ModuleVector moduleVector,
         XA_Module mod,
         @NotNull NotFoundStrategy nfStrategy) throws NullPointerException, ItemNotFoundException
  {
    Objects.requireNonNull(file,
                           "file==null");
    Objects.requireNonNull(moduleVector,
                           "moduleVector==null");
    Objects.requireNonNull(nfStrategy,
                           "nfStrategy==null");
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
    SREG tmpSREG = null;
    Register tmpSP = null;
    for (XA_RegisterGroup rg : module.getRegisterGroups()) {
      for (XA_Register r : rg.getRegister()) {
        Register reg = registerBuilder.fromDescritpor(file,
                                                      moduleVector.withRegister(r.getName())).build();
        switch (reg.getName()) {
          case "SREG":
            if (reg instanceof SREG) {
              tmpSREG = (SREG) reg;
            } else {
              tmpSREG = new SREGImpl(reg);
            }
            tmpRegister.add(tmpSREG);
            break;
          case "SP":
            tmpSP = reg;
            tmpRegister.add(reg);
            break;
          default:
            tmpRegister.add(reg);
            break;
        }
      }
    }
    this.register = Collections.unmodifiableList(tmpRegister);
    this.sreg = tmpSREG;
    this.sp = tmpSP;
    if (sreg == null) {
      ItemNotFoundException.processItemNotFound(moduleVector.getDeviceName(),
                                                "SREG",
                                                nfStrategy);
    }
    if (sp == null) {
      ItemNotFoundException.processItemNotFound(moduleVector.getDeviceName(),
                                                "SP",
                                                nfStrategy);
    }
    instructionDecoder = new CPU_2EInstructionDecoder();
    version = AVRCoreVersion.valueOf(module.getParameter().get(AVRWBDefaults.PROP_CORE_VERSION));
  }

  @Override
  public int getIP()
  {
    return ip;
  }

  @Override
  public void setIP(Device device,
                    int newIP) throws IllegalArgumentException, NullPointerException, InstructionNotAvailableException
  {
    Objects.requireNonNull(device,
                           "device==null");
    if (newIP < 0) {
      throw new IllegalArgumentException("ip<0");
    }
    this.ip = newIP;
    currentInstruction = instructionDecoder.getInstruction(device,
                                                           newIP * 2);
  }

  @Override
  public SREG getSREG()
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
  public void reset(Device device,
                    ResetSource source) throws SimulationException
  {
    Objects.requireNonNull(device,
                           "device==null");
    Objects.requireNonNull(source,
                           "source==null");
    for (Register r : register) {
      r.setValue(0);
    }
    setIP(device,
          0);
  }

  @Override
  public InstructionDecoder getInstructionDecoder()
  {
    return instructionDecoder;
  }

  @Override
  public void onClock(ClockState clockState,
                      Device device) throws SimulationException
  {
    InstructionResult result = currentInstruction.execute(clockState,
                                                          device);
    if (result.isExecutionFinished()) {
      setIP(device,
            result.getNextIP());
    }
  }

  @Override
  public Instruction getCurrentInstruction()
  {
    return currentInstruction;
  }

  @Override
  public AVRCoreVersion getCoreVersion()
  {
    return version;
  }

}
