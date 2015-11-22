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

import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.NotNull;
import java.util.List;

/**
 * Ergebnis der Befehlsausführung
 *
 * @author wolfi
 */
@Immutable
public interface InstructionResult
{

  /**
   *
   * @return {@code true} wenn die Ausführung beendet ist.
   */
  public boolean isExecutionFinished();

  /**
   * Die Adresse des nächste Befehls.
   *
   * @return next IP
   */
  public int getNextIP();

  /**
   * <p>
   * Die Adressen der veränderten Daten im SRAM.</p> Es können auch Adressen verändert worden sein, ohne dass
   * {@link #isExecutionFinished() } {@code true} liefert.
   *
   * @return list of modified addresses.
   */
  @NotNull
  public List<Integer> getModifiedDataAddresses();

}
