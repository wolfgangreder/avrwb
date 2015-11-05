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

import at.reder.avrwb.annotations.NotNull;
import at.reder.avrwb.annotations.NotThreadSave;
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

}
