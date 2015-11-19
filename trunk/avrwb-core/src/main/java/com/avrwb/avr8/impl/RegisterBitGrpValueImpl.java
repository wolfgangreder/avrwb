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

import com.avrwb.atmelschema.util.HexIntAdapter;
import com.avrwb.annotations.Immutable;
import com.avrwb.annotations.NotNull;
import com.avrwb.avr8.RegisterBitGrpValue;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
@Immutable
final class RegisterBitGrpValueImpl implements RegisterBitGrpValue
{

  private final String name;
  private final String caption;
  private final int value;

  RegisterBitGrpValueImpl(@NotNull String name,
                          @NotNull String caption,
                          int value)
  {
    this.name = name;
    this.caption = caption;
    this.value = value;
  }

  @Override
  @NotNull
  public String getName()
  {
    return name;
  }

  @Override
  @NotNull
  public String getCaption()
  {
    return caption;
  }

  @Override
  public int getValue()
  {
    return value;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 29 * hash + Objects.hashCode(this.name);
    hash = 29 * hash + this.value;
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
    final RegisterBitGrpValueImpl other = (RegisterBitGrpValueImpl) obj;
    if (this.value != other.value) {
      return false;
    }
    return Objects.equals(this.name,
                          other.name);
  }

  @Override
  public String toString()
  {
    return "RegisterBitGrpValueImpl{" + "name=" + name + ", value=" + HexIntAdapter.toHexString(value,
                                                                                                2) + '}';
  }

}
