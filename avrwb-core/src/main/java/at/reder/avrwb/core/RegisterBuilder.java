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
import at.reder.avrwb.annotations.Invariants;
import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;

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
   * @param descriptorFile vollständige Descriptordatei
   * @param vector vektor auf das Register
   * @return {@code this}
   * @throws NullPointerException
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBuilder fromDescritpor(@NotNull XA_AvrToolsDeviceFile descriptorFile,
                                        @NotNull RegisterVector vector) throws NullPointerException,
                                                                               IllegalArgumentException;

  /**
   * Setzt den Kurznamen des zu erzeugenden Registers.
   *
   * @see Register#getName()
   * @param name
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBuilder name(@NotNull @Invariants(emptyAllowed = false) String name) throws NullPointerException,
                                                                                             IllegalArgumentException;

  /**
   * Setzt den vollständigen Namen des Registers.
   *
   * @see Register#getCaption()
   * @param caption
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBuilder caption(@NotNull @Invariants(emptyAllowed = true) String caption) throws NullPointerException;

  /**
   * Setzt die Adresse des Registers im SRAM.
   *
   * @see Register#getMemoryAddress()
   * @param memoryAddress
   * @return {@code this}
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBuilder memoryAddress(@Invariants(minValue = "0") int memoryAddress) throws IllegalArgumentException;

  /**
   * Setzt die Addresse des Register im IO Speicher
   *
   * @see Register#getIOAddress()
   * @param ioAddress
   * @return {@code this}
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBuilder ioAddress(@Invariants(minValue = "0") int ioAddress) throws IllegalArgumentException;

  /**
   * Legt die aktiven Bits des Registers fest.
   *
   * @see Register#getMask()
   * @param mask
   * @return {@code this}
   */
  @NotNull
  public RegisterBuilder mask(int mask);

  /**
   * Legt die größe des Registers in Byte fest.
   *
   * @see Register#getSize()
   * @param size
   * @return {@code this}
   * @throws IllegalArgumentException
   */
  @NotNull
  public RegisterBuilder size(@Invariants(minValue = "1") int size) throws IllegalArgumentException;

  /**
   * Fügt ein Registerbit hinzu.
   *
   * @param bit
   * @return {@code this}
   * @throws NullPointerException
   */
  @NotNull
  public RegisterBuilder addRegisterBit(@NotNull RegisterBit bit) throws NullPointerException;

  /**
   * Entfernt alle Registerbits.
   *
   * @return {@code this}
   */
  @NotNull
  public RegisterBuilder clearRegisterBits();

  /**
   * Erzeugt die Registerinstanz.
   *
   * @return ein neues Register
   * @throws NullPointerException
   * @throws IllegalStateException
   */
  @NotNull
  public Register build() throws NullPointerException, IllegalStateException;

}
