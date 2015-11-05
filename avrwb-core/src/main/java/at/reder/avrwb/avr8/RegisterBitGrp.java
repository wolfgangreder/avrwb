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
 * Beschreibt ein Bit(feld) eines Registers. Wenn es sich um ein Bitfeld handelt, kann mit {@link #getValues()} ein Liste der
 * möglich Bitkombinationen und deren Bedeutung erfragt werden.
 *
 * @author Wolfgang Reder
 */
@NotThreadSave
public interface RegisterBitGrp
{

  /**
   * Der Kurzame des Bit(felde)s.
   *
   * @return name
   */
  @NotNull
  public String getName();

  /**
   * Der vollständige Name des Bit(felde)s
   *
   * @return caption
   */
  @NotNull
  public String getCaption();

  /**
   * Maske der Bits innerhalb des Registers.
   *
   * @return mask
   */
  public int getMask();

  /**
   * Liste mit den möglichen Bitkombinationen
   *
   * @return values
   */
  @NotNull
  public List<RegisterBitGrpValue> getValues();

}
