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

import com.avrwb.annotations.NotNull;
import java.util.List;

/**
 *
 * @author wolfi
 */
public interface ClockDomain extends Resetable
{

  public String getId();

  public String getImplementationId();

  @NotNull
  public ClockState getState();

  @NotNull
  public ClockState next();

  /**
   * Bestimmt die eingestellte Taktfrequenz in Herz.
   *
   * @return Aktuelle Taktfrequenz
   */
  public long getClockFrequency();

  /**
   * Bestimmt die Zeit in Nanosekunden seit Reset.
   *
   * @return Zeit seit letztem Reset
   */
  public double getCurrentTime();

  public double getTimeAtClockState(@NotNull ClockState cs) throws NullPointerException;

  public long getCycleCount();

  @NotNull
  public List<ClockSink> getClockSinks();

  public void addClockSink(@NotNull ClockSink cs);

  public void removeClockSink(@NotNull ClockSink cs);

  public double getNextEventTime();

  public void run(SimulationContext ctx);

}
