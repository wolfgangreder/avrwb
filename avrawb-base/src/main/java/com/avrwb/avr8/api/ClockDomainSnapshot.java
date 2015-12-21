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

import com.avrwb.avr8.ResetSource;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author wolfi
 */
public final class ClockDomainSnapshot implements ClockDomain
{

  private final String id;
  private final ClockState clockState;
  private final long frequency;
  private final double time;

  public static ClockDomain getSnapshot(ClockDomain cd)
  {
    if (cd == null || cd instanceof ClockDomainSnapshot) {
      return cd;
    } else {
      return new ClockDomainSnapshot(cd);
    }
  }

  private ClockDomainSnapshot(ClockDomain cd)
  {
    this.id = cd.getId();
    this.clockState = cd.getState();
    this.frequency = cd.getClockFrequency();
    this.time = cd.getCurrentTime();
  }

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public String getImplementationId()
  {
    return "SNAPSHOT";
  }

  @Override
  public ClockState getState()
  {
    return clockState;
  }

  @Override
  public ClockState next()
  {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public long getClockFrequency()
  {
    return frequency;
  }

  @Override
  public double getCurrentTime()
  {
    return time;
  }

  @Override
  public double getTimeAtClockState(ClockState cs) throws NullPointerException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double getNextEventTime()
  {
    return time;
  }

  @Override
  public long getCycleCount()
  {
    return clockState.getCycleCount();
  }

  @Override
  public List<ClockSink> getClockSinks()
  {
    return Collections.emptyList();
  }

  @Override
  public void addClockSink(ClockSink cs)
  {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void removeClockSink(ClockSink cs)
  {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void run(SimulationContext ctx)
  {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void reset(SimulationContext ctx,
                    ResetSource source)
  {
    throw new UnsupportedOperationException("Not supported.");
  }

}
