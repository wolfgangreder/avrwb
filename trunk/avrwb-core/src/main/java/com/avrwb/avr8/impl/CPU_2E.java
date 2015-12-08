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
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.CPU;
import com.avrwb.avr8.Device;
import com.avrwb.avr8.Pointer;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBuilder;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.SREG;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.InstructionResult;
import com.avrwb.avr8.helper.InstructionNotAvailableException;
import com.avrwb.avr8.helper.ItemNotFoundException;
import com.avrwb.avr8.helper.NotFoundStrategy;
import com.avrwb.avr8.helper.SimulationException;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import com.avrwb.schema.XmlRegister;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public class CPU_2E implements CPU
{

  private final String name;
  private int ip;
  private Instruction currentInstruction;
  private final SREG sreg;
  private final Register sp;
  private final List<Register> register;
  private final CPU_2EInstructionDecoder instructionDecoder;
  private final AvrCore version;
  private final Register rampd;
  private final Register rampx;
  private final Register rampy;
  private final Register rampz;
  private final Register eind;

  CPU_2E(@NotNull XmlDevice device,
         @NotNull XmlModule module,
         @NotNull SRAM sram,
         @NotNull NotFoundStrategy nfStrategy) throws NullPointerException, ItemNotFoundException
  {
    Objects.requireNonNull(device,
                           "file==null");
    Objects.requireNonNull(module,
                           "module==null");
    Objects.requireNonNull(nfStrategy,
                           "nfStrategy==null");
    Objects.requireNonNull(sram,
                           "sram==null");
    if (module.getClazz() != ModuleClass.CPU) {
      throw new IllegalArgumentException("module is no cpu");
    }
    this.name = module.getName();
    final RegisterBuilder registerBuilder = InstanceFactories.getRegisterBuilder().sram(sram);
    final List<Register> tmpRegister = new ArrayList<>();
    SREG tmpSREG = null;
    Register tmpSP = null;
    Register tmprampd = null;
    Register tmprampx = null;
    Register tmprampy = null;
    Register tmprampz = null;
    Register tmpeind = null;
    for (XmlRegister r : module.getRegister()) {
      Register reg = registerBuilder.fromDescritpor(r).build();
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
        case "RAMPD":
          tmprampd = reg;
          tmpRegister.add(reg);
          break;
        case "RAMPX":
          tmprampx = reg;
          tmpRegister.add(reg);
          break;
        case "RAMPY":
          tmprampy = reg;
          tmpRegister.add(reg);
          break;
        case "RAMPZ":
          tmprampz = reg;
          tmpRegister.add(reg);
          break;
        case "EIND":
          tmpeind = reg;
          tmpRegister.add(reg);
          break;
        default:
          tmpRegister.add(reg);
          break;
      }
    }
    this.register = Collections.unmodifiableList(tmpRegister);
    this.sreg = tmpSREG;
    this.sp = tmpSP;
    this.rampd = tmprampd;
    this.rampx = tmprampx;
    this.rampy = tmprampy;
    this.rampz = tmprampz;
    this.eind = tmpeind;
    if (sreg == null) {
      ItemNotFoundException.processItemNotFound(device.getName(),
                                                "SREG",
                                                nfStrategy);
    }
    if (sp == null) {
      ItemNotFoundException.processItemNotFound(device.getName(),
                                                "SP",
                                                nfStrategy);
    }
    instructionDecoder = new CPU_2EInstructionDecoder();
    version = device.getAvrCore();
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
    Set<Integer> changedAddresses = result.getModifiedDataAddresses();
    if (!changedAddresses.isEmpty()) {
      device.getSRAM().fireMemoryChanged(changedAddresses);
    }
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
  public AvrCore getCoreVersion()
  {
    return version;
  }

  @Override
  public Register getEIND()
  {
    return eind;
  }

  @Override
  public Register getRAMP(Pointer ptr)
  {
    Objects.requireNonNull(ptr,
                           "ptr==null");
    switch (ptr) {
      case X:
        return rampx;
      case Y:
        return rampy;
      case Z:
        return rampz;
      default:
        throw new IllegalArgumentException("unexpected ptr " + ptr.name());
    }
  }

  @Override
  public Register getRAMPD()
  {
    return rampd;
  }

  @Override
  public String toString()
  {
    return "CPU_2E{" + "ip=0x" + Integer.toHexString(ip) + ", currentInstruction=" + currentInstruction + ", sreg=" + sreg + '}';
  }

}
