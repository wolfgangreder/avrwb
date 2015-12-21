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
package com.avrwb.avr8.impl.cpu;

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
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.Instruction;
import com.avrwb.avr8.api.InstructionDecoder;
import com.avrwb.avr8.api.InstructionNotAvailableException;
import com.avrwb.avr8.api.InstructionResult;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.api.NotFoundStrategy;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.api.SimulationError;
import com.avrwb.avr8.impl.SREGImpl;
import com.avrwb.avr8.impl.instructions.Nop;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.AvrCore;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import com.avrwb.schema.XmlRegister;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
  private final Map<Integer, Register> register;
  private final CPU_2EInstructionDecoder instructionDecoder;
  private final AvrCore version;
  private final Register rampd;
  private final Register rampx;
  private final Register rampy;
  private final Register rampz;
  private final Register eind;
  private long executionCounter;

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
    final Map<Integer, Register> tmpRegister = new HashMap<>();
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
          tmpRegister.put(tmpSREG.getIOAddress(),
                          tmpSREG);
          break;
        case "SP":
          tmpSP = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "RAMPD":
          tmprampd = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "RAMPX":
          tmprampx = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "RAMPY":
          tmprampy = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "RAMPZ":
          tmprampz = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "EIND":
          tmpeind = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        default:
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
      }
    }
    this.register = Collections.unmodifiableMap(tmpRegister);
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
  public void setIP(SimulationContext ctx,
                    int newIP) throws IllegalArgumentException, NullPointerException
  {
    Objects.requireNonNull(ctx,
                           "ctx==null");
    if (newIP < 0) {
      throw new IllegalArgumentException("ip<0");
    }
    this.ip = newIP;
    try {
      currentInstruction = instructionDecoder.getInstruction(ctx.getDevice(),
                                                             newIP * 2);
    } catch (InstructionNotAvailableException ex) {
      currentInstruction = Nop.getInstance(ctx.getDevice().getDeviceKey(),
                                           newIP,
                                           newIP);
      ctx.addEvent(new SimulationError(ctx.getCPUDomain(),
                                       ex));
    }
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
  public Map<Integer, Register> getRegister()
  {
    return register;
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    Objects.requireNonNull(ctx,
                           "ctx==null");
    Objects.requireNonNull(source,
                           "source==null");
    for (Register r : register.values()) {
      r.setValue(0);
    }
    setIP(ctx,
          0);
    executionCounter = 0;
  }

  @Override
  public InstructionDecoder getInstructionDecoder()
  {
    return instructionDecoder;
  }

  @Override
  public void onClock(SimulationContext ctx,
                      ClockDomain clockDomain)
  {
    final Device device = Objects.requireNonNull(ctx,
                                                 "ctx==null").getDevice();
    InstructionResult result = currentInstruction.execute(clockDomain,
                                                          device);
    Set<Integer> changedAddresses = result.getModifiedDataAddresses();
    if (!changedAddresses.isEmpty()) {
      device.getSRAM().fireMemoryChanged(changedAddresses);
    }
    if (result.isExecutionFinished()) {
      ++executionCounter;
      setIP(ctx,
            result.getNextIP());
    }
  }

  @Override
  public long getExecutionCounter()
  {
    return executionCounter;
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
  public Register getRAMP(Pointer ptr
  )
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
