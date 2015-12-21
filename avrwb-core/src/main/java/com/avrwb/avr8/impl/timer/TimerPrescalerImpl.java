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
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.SRAM;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.ClockSink;
import com.avrwb.avr8.api.ClockStateImpl;
import com.avrwb.avr8.api.ItemNotFoundException;
import com.avrwb.avr8.api.NotFoundStrategy;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.avr8.api.TimerPrescaler;
import com.avrwb.schema.ModuleClass;
import com.avrwb.schema.XmlDevice;
import com.avrwb.schema.XmlModule;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author wolfi
 */
final class TimerPrescalerImpl implements TimerPrescaler
{

  private static final class SubDomain
  {

    private final int divisor;
    private ClockStateImpl currentState;
    private final Set<ClockSink> listener = new CopyOnWriteArraySet<>();

    public SubDomain(int divisor)
    {
      this.divisor = divisor;
      currentState = new ClockStateImpl();
    }

    public int getDivisor()
    {
      return divisor;
    }

    public ClockStateImpl getCurrentState()
    {
      return currentState;
    }

    public void next()
    {
      currentState = currentState.next();
    }

    @Override
    public int hashCode()
    {
      int hash = 7;
      hash = 47 * hash + this.divisor;
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final SubDomain other = (SubDomain) obj;
      return this.divisor == other.divisor;
    }

    @Override
    public String toString()
    {
      return "SubDomain{" + "divisor=" + divisor + ", currentState=" + currentState + '}';
    }

  }
  private int counterValue;
  private final String name;
  private final Map<Integer, SubDomain> subDomains = new ConcurrentHashMap<>();

  public TimerPrescalerImpl(@NotNull XmlDevice device,
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
    if (module.getClazz() != ModuleClass.OTHER) {
      throw new IllegalArgumentException("invalid module class");
    }
    this.name = module.getName();

  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Map<Integer, Register> getRegister()
  {
    return Collections.emptyMap();
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    if (source == ResetSource.POWER_UP) {
      counterValue = 0;
    }
  }

  @Override
  public void onClock(SimulationContext ctx,
                      ClockDomain clockDomain)
  {
    switch (clockDomain.getState().getPhase()) {
      case FALLING:
      case RISING:
        ++counterValue;
        subDomains.entrySet().stream().
                filter((Map.Entry<Integer, SubDomain> t) -> (counterValue % t.getKey()) == 0).
                forEach((Map.Entry<Integer, SubDomain> t) -> {
                  t.getValue().listener.forEach((ClockSink cs) -> cs.onClock(ctx,
                                                                             clockDomain));
                });
    }
  }

  @Override
  public void registerClockSink(int prescale,
                                ClockSink cs) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(cs,
                           "clockSink==null");
    if (prescale <= 0) {
      throw new IllegalArgumentException("invalid prescale");
    }
    SubDomain sd = subDomains.computeIfAbsent(prescale,
                                              (Integer pc) -> new SubDomain(pc));
    sd.listener.add(cs);
  }

  @Override
  public void unregisterClockSink(int prescale,
                                  ClockSink cs) throws NullPointerException, IllegalArgumentException
  {
    Objects.requireNonNull(cs,
                           "clockSink==null");
    if (prescale <= 0) {
      throw new IllegalArgumentException("invalid prescale");
    }
    SubDomain sd = subDomains.computeIfAbsent(prescale,
                                              (Integer pc) -> new SubDomain(pc));
    sd.listener.remove(cs);
  }

}
