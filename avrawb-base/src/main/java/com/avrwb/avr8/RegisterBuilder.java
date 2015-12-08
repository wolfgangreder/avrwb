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

import com.avrwb.annotations.Invariants;
import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.schema.XmlRegister;

/**
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public interface RegisterBuilder
{

  /**
   * Initialisiert den Builder mit den Werten aus der Descriptordatei.
   *
   * @param register XmlRegister
   * @return {@code this}
   * @throws NullPointerException wenn {@code descriptorFile} oder {@code vector} gleich {@code null} ist.
   * @throws IllegalArgumentException wenn das Register nicht gefunden werden kann.
   */
  @NotNull
  public RegisterBuilder fromDescritpor(@NotNull XmlRegister register) throws NullPointerException,
                                                                              IllegalArgumentException;

  /**
   * Setzt den Kurznamen des zu erzeugenden Registers.
   *
   * @see Register#getName()
   * @param name name
   * @return {@code this}
   * @throws NullPointerException wenn {@code name} gleich {@code null}.
   * @throws IllegalArgumentException wenn {@code name} leer ist.
   */
  @NotNull
  public RegisterBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                             IllegalArgumentException;

  /**
   * Setzt den vollständigen Namen des Registers.
   *
   * @see Register#getCaption()
   * @param caption caption
   * @return {@code this}
   * @throws NullPointerException wenn {@code caption} gleich {@code null}.
   */
  @NotNull
  public RegisterBuilder caption(@NotNull @Invariants(emptyAllowed = true) String caption) throws NullPointerException;

  /**
   * Setzt die Adresse des Registers im SRAM.
   *
   * @see Register#getMemoryAddress()
   * @param memoryAddress adresse
   * @return {@code this}
   * @throws IllegalArgumentException wenn {@code memoryAddress<0}
   */
  @NotNull
  public RegisterBuilder memoryAddress(@Invariants(minValue = "0") int memoryAddress) throws IllegalArgumentException;

  /**
   * Setzt die Addresse des Register im IO Speicher
   *
   * @see Register#getIOAddress()
   * @param ioAddress ioAdresse
   * @return {@code this}
   * @throws IllegalArgumentException wenn {@code ioAddress<0}
   */
  @NotNull
  public RegisterBuilder ioAddress(@Invariants(minValue = "0") int ioAddress) throws IllegalArgumentException;

  /**
   * Legt die aktiven Bits des Registers fest.
   *
   * @see Register#getMask()
   * @param mask makse
   * @return {@code this}
   */
  @NotNull
  public RegisterBuilder mask(int mask);

  /**
   * Legt die größe des Registers in Byte fest.
   *
   * @see Register#getSize()
   * @param size größe
   * @return {@code this}
   * @throws IllegalArgumentException wenn {@code size<1} oder {@code size>4}.
   */
  @NotNull
  public RegisterBuilder size(@Invariants(minValue = "1", maxValue = "4") int size) throws IllegalArgumentException;

  /**
   * Fügt ein Registerbit hinzu.
   *
   * @param bit bit
   * @return {@code this}
   * @throws NullPointerException wenn {@code bit==null}.
   */
  @NotNull
  public RegisterBuilder addRegisterBit(@NotNull RegisterBitGrp bit) throws NullPointerException;

  /**
   * Entfernt alle Registerbits.
   *
   * @return {@code this}
   */
  @NotNull
  public RegisterBuilder clearRegisterBits();

  /**
   * Weist den Dataspace zu.
   *
   * @param sram dataspace
   * @return {@code this}
   * @throws NullPointerException wenn {@code sram==null}.
   */
  @NotNull
  public RegisterBuilder sram(@NotNull SRAM sram) throws NullPointerException;

  /**
   * Erzeugt die Registerinstanz.
   *
   * @return ein neues Register
   * @throws NullPointerException wenn eine der erforderlichen Eigenschaften {@code null} ist.
   * @throws IllegalStateException wenn eine der Invarianten nicht erfüllt werden.
   */
  @NotNull
  public Register build() throws NullPointerException, IllegalStateException;

}
