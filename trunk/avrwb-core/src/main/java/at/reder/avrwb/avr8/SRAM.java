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

/**
 *
 * @author wolfi
 */
public interface SRAM extends Memory
{

  /**
   * Liefert den Wert des Registerpaares {@code ptr}
   *
   * @param ptr ptr
   * @return ptr word
   */
  public int getPointer(@NotNull Pointer ptr);

  /**
   * Setzt den Wert des Registerpaares {@code ptr}
   *
   * @param ptr ptr
   * @param newValue newValue
   * @return oldValue
   */
  public int setPointer(@NotNull Pointer ptr,
                        int newValue);

  /**
   * Liefert {@code true} wenn die Adresse {@code address} im externen RAM liegt.
   *
   * @param address adresse
   * @return {@code true} wenn extern
   */
  public boolean isAddressExternal(int address);

}
