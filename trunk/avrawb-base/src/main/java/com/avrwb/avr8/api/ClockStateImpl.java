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
package com.avrwb.avr8.api;

import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class ClockStateImpl implements ClockState
{

  private final ClockPhase phase;
  private final long cycleCount;
  private final long frequency;

  public ClockStateImpl(long frequency)
  {
    this(ClockPhase.HI,
         0,
         frequency);
  }

  public ClockStateImpl(ClockPhase phase,
                        long cycleCount,
                        long frequency)
  {
    Objects.requireNonNull(phase,
                           "phase==null");
    if (frequency <= 0) {
      throw new IllegalArgumentException("frequency<=0");
    }
    this.phase = phase;
    this.cycleCount = cycleCount;
    this.frequency = frequency;
  }

  @Override
  public ClockPhase getPhase()
  {
    return phase;
  }

  @Override
  public long getCycleCount()
  {
    return cycleCount;
  }

  @Override
  public long getClockFrequency()
  {
    return frequency;
  }

  @Override
  public long getCurrentNanos()
  {
    switch (phase) {
      case FALLING:
      case LO:
        return (cycleCount + 1) * 1000L - 500;
      case RISING:
      case HI:
        return cycleCount * 1000;
    }
    return 0;
  }

  public ClockStateImpl next()
  {
    ClockPhase nextPhase;
    long nextCycleCount = cycleCount;

    if (phase == ClockPhase.HI) {
      nextPhase = ClockPhase.LO;
    } else {
      nextPhase = ClockPhase.HI;
      ++nextCycleCount;
    }
    return new ClockStateImpl(nextPhase,
                              nextCycleCount,
                              frequency);
  }

  public ClockStateImpl reset()
  {
    return new ClockStateImpl(frequency);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.phase);
    hash = 79 * hash + (int) (this.cycleCount ^ (this.cycleCount >>> 32));
    hash = 79 * hash + (int) (this.frequency ^ (this.frequency >>> 32));
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
    final ClockStateImpl other = (ClockStateImpl) obj;
    if (this.cycleCount != other.cycleCount) {
      return false;
    }
    if (this.frequency != other.frequency) {
      return false;
    }
    return this.phase == other.phase;
  }

  @Override
  public String toString()
  {
    return "ClockStateImpl{" + "phase=" + phase + ", cycleCount=" + cycleCount + '}';
  }

}
