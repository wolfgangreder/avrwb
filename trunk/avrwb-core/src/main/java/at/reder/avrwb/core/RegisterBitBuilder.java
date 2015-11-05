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

import at.reder.atmelschema.RegisterVector;
import at.reder.atmelschema.XA_AvrToolsDeviceFile;
import at.reder.atmelschema.XA_Bitfield;
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;

/**
 * Dient zum Erzeugen von RegisterBit's.
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public interface RegisterBitBuilder
{

  /**
   * Initialisiert den Builder mit den Werten aus dem Descriptor.
   *
   * @param descriptor descriptorDate
   * @param registerVector register
   * @param rb rb
   * @return {@code this}
   * @throws NullPointerException
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBitBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                           @NotNull RegisterVector registerVector,
                                           @NotNull XA_Bitfield rb) throws NullPointerException,
                                                                           IllegalArgumentException;

  /**
   * Initialisiert den Builder mit den Werten aus dem Descriptor.
   *
   * @param descriptor descriptorDate
   * @param registerVector register
   * @param name name
   * @return {@code this}
   * @throws NullPointerException
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBitBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                           @NotNull RegisterVector registerVector,
                                           @NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                          IllegalArgumentException;

  /**
   * Setzt den Kurznamen des Registerbits.
   *
   * @param name name
   * @return {@code this}
   * @throws NullPointerException
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBitBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                IllegalArgumentException;

  /**
   * Setzt den vollständigen Name des Registerbits.
   *
   * @param caption caption
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBitBuilder caption(@NotNull String caption) throws NullPointerException;

  /**
   * Setzt die Bitmaske innerhalb des Registers.
   *
   * @param mask maske
   * @return {@code this}
   */
  @NotNull
  public RegisterBitBuilder mask(int mask);

  /**
   * Fügt einen möglichen Wert hinzu.
   *
   * @param bitValue bitValue
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBitBuilder addBitValue(@NotNull RegisterBitValue bitValue) throws NullPointerException;

  /**
   * Entfernt alle Wertdefinitionen.
   *
   * @return {@code this}
   */
  @NotNull
  public RegisterBitBuilder clearBitValues();

  /**
   * Erzeugt eine neue Instanz.
   *
   * @return {@code this}
   * @throws NullPointerException
   * @throws IllegalStateException
   */
  @NotNull
  public RegisterBit build() throws NullPointerException, IllegalStateException;

}
