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
package com.avrwb.avr8.impl.timer;

import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.Register;
import com.avrwb.avr8.RegisterBuilder;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.api.NotFoundStrategy;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.spi.InstanceFactories;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import com.avrwb.schema.XmlRegister;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.avrwb.avr8.Module;

/**
 *
 * @author wolfi
 */
public final class Timer_0 implements Module
{

  private final String name;
  private final Map<Integer, Register> register;
  private final Register tifr;
  private final Register timsk;
  private final Register tccr0a;
  private final Register tccr0b;
  private final Register tcnt0;
  private final Register ocr0a;
  private final Register ocr0b;

  public Timer_0(@NotNull XmlDevice device,
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
    if (module.getClazz() != ModuleClass.TIMER) {
      throw new IllegalArgumentException("module is no timer");
    }
    this.name = module.getName();
    final RegisterBuilder registerBuilder = InstanceFactories.getRegisterBuilder().sram(sram);
    final Map<Integer, Register> tmpRegister = new HashMap<>();
    Register tmpTifr = null;
    Register tmpTimsk = null;
    Register tmpTccr0a = null;
    Register tmpTccr0b = null;
    Register tmpTcnt0 = null;
    Register tmpOcr0a = null;
    Register tmpOcr0b = null;
    for (XmlRegister r : module.getRegister()) {
      Register reg = registerBuilder.fromDescritpor(r).build();
      switch (reg.getName()) {
        case "TIFR":
          tmpTifr = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "TIMSK":
          tmpTimsk = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "TCCR0A":
          tmpTccr0a = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "TCCR0B":
          tmpTccr0b = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "TCNT0":
          tmpTcnt0 = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "OCR0A":
          tmpOcr0a = reg;
          tmpRegister.put(reg.getIOAddress(),
                          reg);
          break;
        case "OCR0B":
          tmpOcr0b = reg;
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
    this.ocr0a = tmpOcr0a;
    if (ocr0a == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "OCR0A",
                                                nfStrategy);
    }
    this.ocr0b = tmpOcr0b;
    if (ocr0b == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "OCR0B",
                                                nfStrategy);
    }
    this.tccr0a = tmpTccr0a;
    if (tccr0a == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "TCCR0A",
                                                nfStrategy);
    }
    this.tccr0b = tmpTccr0b;
    if (tccr0b == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "TCCR0B",
                                                nfStrategy);
    }
    this.tcnt0 = tmpTcnt0;
    if (tcnt0 == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "TCNT0",
                                                nfStrategy);
    }
    this.tifr = tmpTifr;
    if (tifr == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "TIFR",
                                                nfStrategy);
    }
    this.timsk = tmpTimsk;
    if (timsk == null) {
      ItemNotFoundException.processItemNotFound(name,
                                                "TIMSK",
                                                nfStrategy);
    }
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
    for (Register r : register.values()) {
      r.setValue(0);
    }
  }

  @Override
  public void onClock(SimulationContext ctx,
                      ClockDomain clockDomain)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
