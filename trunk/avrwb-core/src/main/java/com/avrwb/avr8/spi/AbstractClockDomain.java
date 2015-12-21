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
package com.avrwb.avr8.spi;

import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.ResetSource;
import com.avrwb.avr8.api.ClockDomain;
import com.avrwb.avr8.api.ClockSink;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.ClockStateImpl;
import com.avrwb.avr8.api.SimulationContext;
import com.avrwb.schema.XmlClockDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author wolfi
 */
public final class AbstractClockDomain implements ClockDomain
{

  private ClockStateImpl currentState;
  private ClockStateImpl nextState;
  private double currentTime;
  private double nextTime;
  private final String id;
  private final long frequencyMin;
  private final long frequencyMax;
  private final long currentFrequency;
  private final Set<ClockSink> clockSinks = new CopyOnWriteArraySet<>();
  private final String implementationId;
  private final double cycleNanos;

  public AbstractClockDomain(@NotNull XmlClockDomain clockDomain,
                             @NotNull String implementationId)
  {
    Objects.requireNonNull(clockDomain,
                           "clockDomain==null");
    this.implementationId = Objects.requireNonNull(implementationId,
                                                   "implementationId==null");
    this.id = clockDomain.getId();
    this.frequencyMin = clockDomain.getSpeedMin();
    this.frequencyMax = clockDomain.getSpeedMax();
    currentFrequency = clockDomain.getSpeedDefault();
    currentState = new ClockStateImpl();
    cycleNanos = 1e9d / currentFrequency;
    nextState = currentState.next();
    currentTime = getTimeAtClockState(currentState);
    nextTime = getTimeAtClockState(nextState);
  }

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public String getImplementationId()
  {
    return implementationId;
  }

  @Override
  public ClockState getState()
  {
    return currentState;
  }

  @Override
  public ClockState next()
  {
    currentState = nextState;
    currentTime = nextTime;
    nextState = currentState.next();
    nextTime = getTimeAtClockState(nextState);
    return currentState;
  }

  @Override
  public List<ClockSink> getClockSinks()
  {
    return new ArrayList<>(clockSinks);
  }

  @Override
  public void addClockSink(ClockSink cs)
  {
    clockSinks.add(Objects.requireNonNull(cs,
                                          "clockSink==null"));
  }

  @Override
  public void removeClockSink(ClockSink cs)
  {
    if (cs != null) {
      clockSinks.remove(cs);
    }
  }

  @Override
  public double getNextEventTime()
  {
    return nextTime;
  }

  @Override
  public double getTimeAtClockState(ClockState cs) throws NullPointerException
  {
    switch (cs.getPhase()) {
      case FALLING:
      case LO:
        return (cs.getCycleCount() + 1) * cycleNanos - cycleNanos / 2d;
      case RISING:
      case HI:
        return cs.getCycleCount() * cycleNanos;
    }
    return 0;
  }

  @Override
  public long getCycleCount()
  {
    return currentState.getCycleCount();
  }

  @Override
  public long getClockFrequency()
  {
    return currentFrequency;
  }

  @Override
  public double getCurrentTime()
  {
    return currentTime;
  }

  @Override
  public void run(SimulationContext ctx)
  {
    for (ClockSink cs : clockSinks) {
      cs.onClock(ctx,
                 this);
    }
    next();
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    if (source == ResetSource.POWER_UP) {
      currentState = currentState.reset();
    }
  }

}
