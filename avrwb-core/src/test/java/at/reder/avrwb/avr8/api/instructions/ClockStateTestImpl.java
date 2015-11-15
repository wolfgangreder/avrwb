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
package at.reder.avrwb.avr8.api.instructions;

import at.reder.avrwb.avr8.api.ClockPhase;
import at.reder.avrwb.avr8.api.ClockState;

/**
 *
 * @author wolfi
 */
public final class ClockStateTestImpl implements ClockState
{

  private ClockPhase phase;
  private long cycleCount;

  public ClockStateTestImpl()
  {
    reset();
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
    return 1_000_000;
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

  public void next()
  {
    if (phase == ClockPhase.HI) {
      phase = ClockPhase.LO;
    } else {
      phase = ClockPhase.HI;
      ++cycleCount;
    }
  }

  public void reset()
  {
    phase = ClockPhase.HI;
    cycleCount = 0;
  }

  @Override
  public String toString()
  {
    return "ClockStateTestImpl{" + "phase=" + phase + ", cycleCount=" + cycleCount + '}';
  }

}
