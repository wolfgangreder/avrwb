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
package at.reder.avrwb.core.impl;

import at.reder.avrwb.core.RegisterBit;
import at.reder.avrwb.core.RegisterBitValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Standardimplementierung von RegisterBit
 *
 * @see at.reder.avrwb.core.RegisterBit
 * @see at.reder.avrwb.core.RegisterBitBuilder
 * @author Wolfgang Reder
 */
final class RegisterBitImpl implements RegisterBit
{

  private final String name;
  private final String caption;
  private final int mask;
  private final List<RegisterBitValue> bitValues;

  RegisterBitImpl(String name,
                  String caption,
                  int mask,
                  Collection<? extends RegisterBitValue> bitValues)
  {
    this.name = name;
    this.caption = caption;
    this.mask = mask;
    if (bitValues == null || bitValues.isEmpty()) {
      this.bitValues = Collections.emptyList();
    } else {
      this.bitValues = Collections.unmodifiableList(new ArrayList<>(bitValues));
    }
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
  public List<RegisterBitValue> getValues()
  {
    return bitValues;
  }

  @Override
  public String toString()
  {
    return "RegisterBit{" + "name=" + name + '}';
  }

}
