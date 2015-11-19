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
 * Bestimmt den aktuellen Zustand des Taktsignals seit Reset. Für die Taktphasen {@link ClockPhase#FALLING} und
 * {@link ClockPhase#RISING}.
 *
 * @see ClockPhase
 * @see ClockSink
 *
 * @author Wolfgang Reder
 */
public interface ClockState
{

  /**
   * Bestimmt den aktuellen zustand des Taktsignals.
   *
   * @return aktulle Taktphase
   */
  @NotNull
  public ClockPhase getPhase();

  /**
   * Bestimmt der wievielte Taktzyklus gerade läuft.
   *
   * @return Anzahl der takte seit letztem Reset
   */
  public long getCycleCount();

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
  public long getCurrentNanos();

}
