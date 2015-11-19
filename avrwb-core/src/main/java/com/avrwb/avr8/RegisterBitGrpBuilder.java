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
package com.avrwb.avr8;

import com.avrwb.atmelschema.RegisterVector;
import com.avrwb.atmelschema.XA_AvrToolsDeviceFile;
import com.avrwb.atmelschema.XA_Bitfield;
import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;

/**
 * Dient zum Erzeugen von RegisterBitGrp's.
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public interface RegisterBitGrpBuilder
{

  /**
   * Initialisiert den Builder mit den Werten aus dem Descriptor.
   *
   * @param descriptor descriptorDate
   * @param bitField register
   * @param registerVector
   * @return {@code this}
   * @throws NullPointerException wenn {@code descriptor}, {@code bitField} oder {@code registerVecotor==null} gleich {@code null}
   */
  @NotNull
  public RegisterBitGrpBuilder fromDescriptor(@NotNull XA_AvrToolsDeviceFile descriptor,
                                              @NotNull XA_Bitfield bitField,
                                              @NotNull RegisterVector registerVector) throws NullPointerException;

  /**
   * Setzt den Kurznamen des Registerbits.
   *
   * @param name name
   * @return {@code this}
   * @throws NullPointerException wenn {@code name==null}
   * @throws IllegalArgumentException wenn {@code name} ist leer.
   */
  @NotNull
  public RegisterBitGrpBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                                   IllegalArgumentException;

  /**
   * Setzt den vollständigen Name des Registerbits.
   *
   * @param caption caption
   * @return {@code this}
   * @throws NullPointerException wenn {@code caption} gleich {@code null}.
   */
  @NotNull
  public RegisterBitGrpBuilder caption(@NotNull String caption) throws NullPointerException;

  /**
   * Setzt die Bitmaske innerhalb des Registers.
   *
   * @param mask maske
   * @return {@code this}
   */
  @NotNull
  public RegisterBitGrpBuilder mask(int mask);

  /**
   * Fügt einen möglichen Wert hinzu.
   *
   * @param bitValue bitValue
   * @return {@code this}
   * @throws NullPointerException wenn {@code bitValue} gleich {@code null}.
   */
  @NotNull
  public RegisterBitGrpBuilder addBitValue(@NotNull RegisterBitGrpValue bitValue) throws NullPointerException;

  /**
   * Entfernt alle Wertdefinitionen.
   *
   * @return {@code this}
   */
  @NotNull
  public RegisterBitGrpBuilder clearBitValues();

  /**
   * Erzeugt eine neue Instanz.
   *
   * @return {@code this}
   * @throws NullPointerException wenn {@code name} order {@code caption} gleich null.
   * @throws IllegalStateException wenn {@code name} leer ist.
   */
  @NotNull
  public RegisterBitGrp build() throws NullPointerException, IllegalStateException;

}
