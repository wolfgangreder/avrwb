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
package at.reder.avrwb.core;

import at.reder.atmelschema.XA_ValueGroup;
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;

/**
 *
 * @author Wolfgang Reder
 */
public interface RegisterBitValueBuilder
{

  /**
   * Initialisiert den Builder aus den Werten von {@code valueGroup}.
   *
   * @param valueGroup valueGroup
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBitValueBuilder fromDescriptor(@NotNull XA_ValueGroup valueGroup) throws NullPointerException;

  /**
   * Setzt den Kurzamen des Wertes.
   *
   * @param name name
   * @return{@code this}
   * @throws NullPointerException
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBitValueBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                     IllegalArgumentException;

  /**
   * Setzt den vollständigen Namen des Wertes.
   *
   * @param caption caption
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBitValueBuilder caption(@NotNull String caption) throws NullPointerException;

  /**
   * Setzt den Wert.
   *
   * @param value value
   * @return {@code this}
   */
  @NotNull
  public RegisterBitValueBuilder value(int value);

  /**
   * Erzeugt eine Instanz.
   *
   * @return neue Instanz
   * @throws NullPointerException
   * @throws IllegalStateException
   */
  @NotNull
  public RegisterBitValue build() throws NullPointerException, IllegalStateException;

}
