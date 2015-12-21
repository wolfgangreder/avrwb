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

import com.avrwb.annotations.NotNull;
import com.avrwb.annotations.NotThreadSave;
import com.avrwb.avr8.api.ItemNotFoundException;
import java.util.List;

/**
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public interface Register
{

  /**
   * Der Kurzname des Registers.
   *
   * @return name
   */
  @NotNull
  public String getName();

  /**
   * Der vollständige Name des Registers.
   *
   * @return caption
   */
  @NotNull
  public String getCaption();

  /**
   * Der aktuelle wert des Registers
   *
   * @return value
   */
  public int getValue();

  /**
   * Verändert den aktuellen wert des Registers.
   *
   * @param newValue der neue Wert des Registers.
   */
  public void setValue(int newValue);

  /**
   * Adresse des Registers im SRAM
   *
   * @return memoryAddress
   */
  public int getMemoryAddress();

  /**
   * Adresse des Registers im IO Space.
   *
   * @return ioAddress
   */
  public int getIOAddress();

  /**
   * Maske der aktive Bits im Register. Falls einzelne Bits des Registers bei dem aktuellen Controller nicht genutzt werden,
   * werden sie mit diesem Wert ausmaskiert.
   *
   * @return mask
   */
  public int getMask();

  /**
   * Grösse des Registers in Byte.
   *
   * @return size
   */
  public int getSize();

  /**
   * Liste der Bits des Registers. Falls das Register bits mit eigenständigen Bedeutungen enthaält wird hier die Liste dieser Bits
   * geliefert.
   *
   * @return bitList
   */
  @NotNull
  public List<RegisterBitGrp> getBits();

  /**
   * <p>
   * Liefert den aktuellen Wert der Bits in Bitgruppe {@code bitGrp}.</p>
   * Die Bits werden entsprechen der Maske nach rechtes verschoben.
   *
   * @param bitGrp bitGruppe
   * @return aktueller Wert der Registergruppe
   * @throws ItemNotFoundException wenn die Bitgruppe {@code bitGrp} nicht im Register vorhanden ist.
   * @throws NullPointerException wenn {@code bitGrp==null}
   */
  public int getBitGrpValue(@NotNull RegisterBitGrp bitGrp) throws ItemNotFoundException, NullPointerException;

  /**
   * Set den Wert der Bitgruppe {@code bitGrp}.
   *
   * @param bitGrp bitGruppe
   * @param value neuerWert
   * @return alter Wert der bitGruppe
   * @throws ItemNotFoundException wenn {@code bitGrp} oder {@code value} nicht im Register vorhanden ist.
   * @throws NullPointerException wenn {@code bitGrp} oder {@code value} gleich {@code null}.
   */
  public int setBitGrpValue(@NotNull RegisterBitGrp bitGrp,
                            @NotNull RegisterBitGrpValue value) throws ItemNotFoundException, NullPointerException;

  /**
   * Set den Wert der Bitgruppe {@code bitGrp}.
   *
   * @param bitGrp bitGruppe
   * @param value neuerWert
   * @return alter Wert der bitGruppe
   * @throws ItemNotFoundException wenn {@code bitGrp} nicht im Register vorhanden ist.
   * @throws NullPointerException wenn {@code bitGrp==null}.
   */
  public int setBitGrpValue(@NotNull RegisterBitGrp bitGrp,
                            int value) throws ItemNotFoundException, NullPointerException;

  /**
   * Liefert den Wert des Bits {@code bit}.
   *
   * @param bit Index des Bits ({@code 0...7})
   * @return {@code true} wenn das Bit gesetzt ist.
   * @throws IllegalArgumentException wenn {@code bit&lt;0} oder {@code bit&gt;7}.
   */
  public boolean getBit(int bit) throws IllegalArgumentException;

  /**
   * Setzt das Bit {@code bit} auf den Wert {@code state}.
   *
   * @param bit zu veränderndes Bit
   * @param state neuer Zustand
   * @return alter Zustand
   * @throws IllegalArgumentException wenn {@code bit&lt;0} oder {@code bit&gt;7}.
   */
  public boolean setBit(int bit,
                        boolean state) throws IllegalArgumentException;

  /**
   * setValue(getValue()+1);
   *
   */
  public void increment();

  /**
   * setValue(getValue()-1);
   *
   */
  public void decrement();

  /**
   *
   * @return dataspace
   */
  public SRAM getMemory();

}
