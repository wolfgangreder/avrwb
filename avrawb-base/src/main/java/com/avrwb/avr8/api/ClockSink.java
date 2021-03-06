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

/**
 *
 * @author Wolfgang Reder
 */
public interface ClockSink
{

  /**
   * Wird bei einem Taktereignis aufgerufen. {@code clockState.getPhase()} ist immer entwender {@code ClockPhase.RISING} oder
   * {@code ClockPhase.FALLING}.
   *
   * @param ctx aktueller Kontext
   * @param clockDomain currentClockDomain
   */
  @NotNull
  public void onClock(@NotNull SimulationContext ctx,
                      @NotNull ClockDomain clockDomain);

}
