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
package com.avrwb.avr8.impl.instructions.helper;

import com.avrwb.avr8.api.ClockPhase;
import com.avrwb.avr8.api.ClockState;
import com.avrwb.avr8.api.ClockStateImpl;

/**
 *
 * @author wolfi
 */
public final class ClockStateTestImpl
{

  private ClockStateImpl state = new ClockStateImpl(1_000_000);

  public ClockStateTestImpl()
  {
  }

  public ClockPhase getPhase()
  {
    return state.getPhase();
  }

  public long getCycleCount()
  {
    return state.getCycleCount();
  }

  public long getClockFrequency()
  {
    return state.getClockFrequency();
  }

  public long getCurrentNanos()
  {
    return state.getCurrentNanos();
  }

  public ClockState getAndNext()
  {
    ClockState result = state;
    state = state.next();
    return result;
  }

  public void reset()
  {
    state = state.reset();
  }

  @Override
  public String toString()
  {
    return state.toString();
  }

}
