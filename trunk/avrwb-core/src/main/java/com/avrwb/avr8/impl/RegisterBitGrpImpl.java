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
package com.avrwb.avr8.impl;

import com.avrwb.avr8.RegisterBitGrp;
import com.avrwb.avr8.RegisterBitGrpValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Standardimplementierung von RegisterBitGrp
 *
 * @see com.avrwb.avr8.RegisterBitGrp
 * @see at.reder.avrwb.core.RegisterBitBuilder
 * @author Wolfgang Reder
 */
final class RegisterBitGrpImpl implements RegisterBitGrp
{

  private final String name;
  private final String caption;
  private final int mask;
  private final int bitShift;
  private final List<RegisterBitGrpValue> bitValues;

  RegisterBitGrpImpl(String name,
                     String caption,
                     int mask,
                     Collection<? extends RegisterBitGrpValue> bitValues)
  {
    this.name = name;
    this.caption = caption;
    this.mask = mask;
    if (bitValues == null || bitValues.isEmpty()) {
      this.bitValues = Collections.emptyList();
    } else {
      this.bitValues = Collections.unmodifiableList(new ArrayList<>(bitValues));
    }
    bitShift = Integer.numberOfTrailingZeros(mask) % 32;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getCaption()
  {
    return caption;
  }

  @Override
  public int getMask()
  {
    return mask;
  }

  @Override
  public int getRightShift()
  {
    return bitShift;
  }

  @Override
  public List<RegisterBitGrpValue> getValues()
  {
    return bitValues;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.name);
    hash = 29 * hash + this.mask;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RegisterBitGrpImpl other = (RegisterBitGrpImpl) obj;
    if (this.mask != other.mask) {
      return false;
    }
    return Objects.equals(this.name,
                          other.name);
  }

  @Override
  public String toString()
  {
    return "RegisterBit{" + "name=" + name + '}';
  }

}
