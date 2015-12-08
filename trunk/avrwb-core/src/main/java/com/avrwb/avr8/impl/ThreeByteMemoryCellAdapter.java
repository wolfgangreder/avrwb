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

import com.avrwb.avr8.SRAM;
import java.util.Objects;

/**
 *
 * @author wolfi
 */
public final class ThreeByteMemoryCellAdapter implements MemoryCellAdapter
{

  private final SRAM memory;
  private final int address;
  private final int mask;

  public ThreeByteMemoryCellAdapter(SRAM memory,
                                    int address,
                                    int mask)
  {
    Objects.requireNonNull(memory,
                           "memory==null");
    if ((address + 2) >= (memory.getSize() + memory.getStart())) {
      throw new IllegalArgumentException("invalid address");
    }
    this.memory = memory;
    this.address = address;
    this.mask = mask;
  }

  @Override
  public int getValue()
  {
    int result = 0;
    for (int i = 2; i >= 0; --i) {
      result = (result << 8) + memory.getByteAt(address + i);
    }
    return result & mask;
  }

  @Override
  public void setValue(int val)
  {
    final int tmp = val & mask;
    memory.setByteAt(address,
                     tmp & 0xff);
    memory.setByteAt(address + 1,
                     (tmp & 0xff00) >> 8);
    memory.setByteAt(address + 2,
                     (tmp & 0xff0000) >> 16);
  }

  @Override
  public void increment()
  {
    setValue(getValue() + 1);
  }

  @Override
  public void decrement()
  {
    setValue(getValue() - 1);
  }

  @Override
  public int getMask()
  {
    return mask;
  }

  @Override
  public String toString()
  {
    return "ThreeByteMemoryCellAdapter{" + "address=" + address + ", value=0x" + Integer.toHexString(getValue()) + '}';
  }

}
