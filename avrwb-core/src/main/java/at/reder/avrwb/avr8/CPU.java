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
package at.reder.avrwb.avr8;

import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;

public interface CPU extends Module
{

  /**
   * Aktueller Instruction Pointer.
   *
   * @return ip
   */
  public int getIP();

  /**
   * Setzt den Instruction Pointer.
   *
   * @param newIP newIP
   */
  public void setIP(@Invariants(minValue = "0") int newIP) throws IllegalArgumentException;

  /**
   * Das Statusregister
   *
   * @return sreg
   */
  @NotNull
  public Register getSREG();

  /**
   * Der Stackpointer
   *
   * @return sp
   */
  @NotNull
  public Register getStackPointer();

}
